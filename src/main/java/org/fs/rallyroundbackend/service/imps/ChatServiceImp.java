package org.fs.rallyroundbackend.service.imps;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.chat.ChatMessageResponse;
import org.fs.rallyroundbackend.dto.chat.ChatMessagesResponse;
import org.fs.rallyroundbackend.dto.chat.MessageRequest;
import org.fs.rallyroundbackend.dto.chat.ToChatMessageRequest;
import org.fs.rallyroundbackend.entity.chats.ChatEntity;
import org.fs.rallyroundbackend.entity.chats.ChatMessageEntity;
import org.fs.rallyroundbackend.entity.chats.EventChatEntity;
import org.fs.rallyroundbackend.entity.chats.PrivateChatEntity;
import org.fs.rallyroundbackend.entity.events.EventState;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;
import org.fs.rallyroundbackend.repository.chat.ChatRepository;
import org.fs.rallyroundbackend.repository.chat.EventChatRepository;
import org.fs.rallyroundbackend.repository.chat.PrivateChatRepository;
import org.fs.rallyroundbackend.repository.user.participant.ParticipantRepository;
import org.fs.rallyroundbackend.service.ChatService;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.UUID;

/**
 * {@link ChatService} implementation.
 */
@Service
@RequiredArgsConstructor
public class ChatServiceImp implements ChatService {
    private final ModelMapper modelMapper;
    private final ChatRepository chatRepository;
    private final EventChatRepository eventChatRepository;

    private final PrivateChatRepository privateChatRepository;
    private final ParticipantRepository participantRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public ChatMessagesResponse getChatMessages(String userEmail, UUID chatId) {
        ParticipantEntity participant = this.participantRepository.findEnabledUserByEmail(userEmail).orElseThrow(
                () -> new EntityNotFoundException("User with email " + userEmail + " not found")
        );

        ChatEntity chat = this.chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room with id " + chatId + " not found"));

        if(!isUserParticipantOfTheGivenChat(participant, chat)) {
            throw new AccessDeniedException("You don't have access to the given chat.");
        }

        ChatMessagesResponse chatMessagesResponse = ChatMessagesResponse.builder()
                .chatId(chat.getChatId())
                .messages(new ArrayList<>())
                .build();

        for(ChatMessageEntity msgEntity : chat.getMessages()) {
            ChatMessageResponse chatMessageResponse = this.modelMapper.map(msgEntity, ChatMessageResponse.class);

            chatMessageResponse.setSubmittedByRequester(msgEntity.getSender().getEmail().equals(userEmail));

            chatMessagesResponse.getMessages().add(chatMessageResponse);
        }

        return chatMessagesResponse;
    }

    @Override
    @Transactional
    public ToChatMessageRequest processToEventMessage(String senderEmail, ToChatMessageRequest message)
            throws AccessDeniedException {
        ParticipantEntity sender = this.participantRepository.findEnabledUserByEmail(senderEmail).orElseThrow(
                () -> new EntityNotFoundException("User with email " + senderEmail + " not found")
        );

        EventChatEntity chat = this.eventChatRepository.findById(message.getChatId()).orElseThrow(
                () -> new EntityNotFoundException("Chat room with id " + message.getChatId() + " not found")
        );

        if(!isUserParticipantOfTheGivenChat(sender, chat)) {
            throw new AccessDeniedException("You don't have access to the given chat.");
        }

        if(chat.getEvent().getState().equals(EventState.FINALIZED)
                || chat.getEvent().getState().equals(EventState.CANCELED)) {
            throw new AccessDeniedException("Access denied: " +
                    "The chat is associated with an event that has been finalized or canceled.");
        }

        ChatMessageEntity messageEntity = this.createChatMessageEntity(message, sender);

        if(chat.getMessages() == null) {
            chat.setMessages(new ArrayList<>());
        }

        chat.getMessages().add(messageEntity);

        this.eventChatRepository.save(chat);

        ChatMessageResponse response = this.modelMapper.map(messageEntity, ChatMessageResponse.class);

        this.messagingTemplate.convertAndSend("/topic/event/" + chat.getChatId(), response);

        return message;
    }

    private ChatMessageEntity createChatMessageEntity(MessageRequest messageRequest, ParticipantEntity sender) {
        ZonedDateTime zonedDateTimeInBuenosAires = messageRequest.getTimestamp()
                .withZoneSameInstant(ZoneId.of("America/Argentina/Buenos_Aires"));

        return ChatMessageEntity.builder()
                .id(messageRequest.getMessageId())
                .message(messageRequest.getMessage())
                .timestamp(zonedDateTimeInBuenosAires)
                .sender(sender)
                .build();
    }

    /**
     * Checks if the given user is a participant of the specified chat.
     *
     * @param user the participant to check
     * @param chat the chat entity (can be an instance of EventChatEntity or PrivateChatEntity)
     * @return true if the user is a participant of the chat, false otherwise
     */
    private boolean isUserParticipantOfTheGivenChat(ParticipantEntity user, ChatEntity chat) {
        boolean result = false;
        if(chat instanceof EventChatEntity eventChatEntity) {
            result = eventChatEntity.getEvent().getEventParticipants()
                    .stream()
                    .anyMatch(ep -> ep.getParticipant().getId().equals(user.getId()));
        } else if (chat instanceof PrivateChatEntity privateChatEntity) {
            result =  privateChatEntity.getParticipants().stream()
                    .anyMatch(p -> p.getId().equals(user.getId()));
        }

        return result;
    }
}

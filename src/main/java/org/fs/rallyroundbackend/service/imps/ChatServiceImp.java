package org.fs.rallyroundbackend.service.imps;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.chat.ChatMessageResponse;
import org.fs.rallyroundbackend.dto.chat.ChatMessagesResponse;
import org.fs.rallyroundbackend.dto.chat.MessageRequest;
import org.fs.rallyroundbackend.entity.chats.ChatEntity;
import org.fs.rallyroundbackend.entity.chats.ChatMessageEntity;
import org.fs.rallyroundbackend.entity.chats.ChatType;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;
import org.fs.rallyroundbackend.repository.chat.ChatRepository;
import org.fs.rallyroundbackend.repository.user.ParticipantRepository;
import org.fs.rallyroundbackend.service.ChatService;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

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
    private final ParticipantRepository participantRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public ChatMessagesResponse getChatMessages(String userEmail, UUID chatId) {
        // TODO: Improvement: If the request have pass the security filter ChatFilter, it means that the existence
        //  of the requested chat room was already checked, so this verification maybe can be omitted in some way.
        ChatEntity eventChatEntity = this.chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room with id " + chatId + " not found"));

        ChatMessagesResponse chatMessagesResponse = ChatMessagesResponse.builder()
                .chatId(eventChatEntity.getChatId())
                .messages(new ArrayList<>())
                .build();

        for(ChatMessageEntity msgEntity : eventChatEntity.getMessages()) {
            ChatMessageResponse chatMessageResponse = this.modelMapper.map(msgEntity, ChatMessageResponse.class);

            chatMessageResponse.setSubmittedByRequester(msgEntity.getSender().getEmail().equals(userEmail));

            chatMessagesResponse.getMessages().add(chatMessageResponse);
        }

        return chatMessagesResponse;
    }

    @Override
    public MessageRequest processMessage(String userEmail, MessageRequest message) {
        ParticipantEntity sender = this.participantRepository.findEnabledUserByEmail(userEmail).orElseThrow(
                () -> new EntityNotFoundException("User with email " + userEmail + " not found")
        );

        // TODO: Improvement: If the request have pass the security filter ChatFilter, it means that the existence
        //  of the requested chat room was already checked, so this verification maybe can be omitted in some way.
        ChatEntity chat = this.chatRepository.findById(message.getChatId()).orElseThrow(
                () -> new EntityNotFoundException("Chat room with id " + message.getChatId() + " not found")
        );

        ZonedDateTime zonedDateTimeInBuenosAires = message.getTimestamp()
                .withZoneSameInstant(ZoneId.of("America/Argentina/Buenos_Aires"));

        ChatMessageEntity messageEntity = ChatMessageEntity.builder()
                .id(message.getMessageId())
                .message(message.getMessage())
                .timestamp(zonedDateTimeInBuenosAires)
                .sender(sender)
                .build();

        if(chat.getMessages() == null) {
            chat.setMessages(new ArrayList<>());
        }

        chat.getMessages().add(messageEntity);

        this.chatRepository.save(chat);

        message.setTimestamp(zonedDateTimeInBuenosAires);

        ChatMessageResponse response = this.modelMapper.map(messageEntity, ChatMessageResponse.class);

        if(chat.getChatType().equals(ChatType.EVENT_CHAT)) {
            this.messagingTemplate.convertAndSend("/topic/event/" + chat.getChatId(), response);
        } else {
            this.messagingTemplate.convertAndSend("/queue/chat/" + chat.getChatId(), response);
        }

        return message;
    }
}

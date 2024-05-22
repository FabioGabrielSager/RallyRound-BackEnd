package org.fs.rallyroundbackend.service;

import org.fs.rallyroundbackend.dto.chat.ChatMessagesResponse;
import org.fs.rallyroundbackend.dto.chat.ToChatMessageRequest;
import org.fs.rallyroundbackend.dto.chat.ToUserMessageRequest;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

/**
 * Service interface for managing chat messages and processing user messages.
 */
public interface ChatService {

    /**
     * Retrieves chat messages for the specified user and chat.
     *
     * @param userEmail The email address of the user retrieving the chat messages.
     * @param chatId    The ID of the chat for which to retrieve messages.
     * @return The response containing the chat messages.
     * @throws AccessDeniedException If the user is not a participant of the chat.
     */
    ChatMessagesResponse getChatMessages(String userEmail, UUID chatId) throws AccessDeniedException;

    /**
     * Processes a message sent by the user.
     *
     * @param senderEmail The email address of the user sending the message.
     * @param message   The request containing the message to be processed.
     * @return The request after being processed.
     * @throws AccessDeniedException If the user is not a participant of the chat
     * or if the chat is in a finalized or canceled state.
     */
    ToChatMessageRequest processToEventMessage(String senderEmail, ToChatMessageRequest message)
            throws AccessDeniedException;
}

package org.fs.rallyroundbackend.service;

import org.fs.rallyroundbackend.dto.chat.ChatMessagesResponse;
import org.fs.rallyroundbackend.dto.chat.MessageRequest;

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
     */
    ChatMessagesResponse getChatMessages(String userEmail, UUID chatId);

    /**
     * Processes a message sent by the user.
     *
     * @param userEmail The email address of the user sending the message.
     * @param message   The request containing the message to be processed.
     * @return The request after being processed.
     */
    MessageRequest processMessage(String userEmail, MessageRequest message);
}

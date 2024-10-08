package org.fs.rallyroundbackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.chat.ChatMessagesResponse;
import org.fs.rallyroundbackend.dto.chat.MessageRequest;
import org.fs.rallyroundbackend.dto.chat.ToChatMessageRequest;
import org.fs.rallyroundbackend.service.ChatService;
import org.fs.rallyroundbackend.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequestMapping("/rr/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {
    private final JwtService jwtService;
    private final ChatService chatService;

    @PostMapping("/message/")
    public ResponseEntity<MessageRequest> processToEventMessage(@RequestBody @Validated ToChatMessageRequest message,
                                                                HttpServletRequest request) throws AccessDeniedException {

        String userEmail = this.jwtService.getUsernameFromToken(this.jwtService.getTokenFromRequest(request));
        return ResponseEntity.ok(this.chatService.processToEventMessage(userEmail, message));

    }

    @GetMapping("/{chatId}")
    public ResponseEntity<ChatMessagesResponse> getChatMessages(@PathVariable(name = "chatId") UUID chatId,
                                                                HttpServletRequest request) throws AccessDeniedException {

        String userEmail = this.jwtService.getUsernameFromToken(this.jwtService.getTokenFromRequest(request));
        return ResponseEntity.ok(this.chatService.getChatMessages(userEmail, chatId));

    }
}

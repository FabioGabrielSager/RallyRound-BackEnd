package org.fs.rallyroundbackend.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fs.rallyroundbackend.dto.chat.MessageRequest;
import org.fs.rallyroundbackend.dto.chat.ToChatMessageRequest;
import org.fs.rallyroundbackend.entity.chats.EventChatEntity;
import org.fs.rallyroundbackend.entity.chats.PrivateChatEntity;
import org.fs.rallyroundbackend.repository.chat.EventChatRepository;
import org.fs.rallyroundbackend.repository.chat.PrivateChatRepository;
import org.fs.rallyroundbackend.service.JwtService;
import org.fs.rallyroundbackend.util.cachedHttpRequest.CachedBodyHttpServletRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * Security filter that checks if the user that is making the request is participant of the requested chat.
 * It only applies to requests matching the URI pattern "/rr/api/v1/chats/**".
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ChatFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final EventChatRepository eventChatRepository;
    private final PrivateChatRepository privateChatRepository;
    private final ObjectMapper objectMapper;
    private static final AntPathRequestMatcher URI_MATCHER = new AntPathRequestMatcher("/rr/api/v1/chats/**");

    @Override
    @Transactional
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        boolean isUserParticipantOfTheChat = false;
        boolean wasChatFound = false;
        CachedBodyHttpServletRequest cachedBodyHttpServletRequest =
                new CachedBodyHttpServletRequest(request);

        try {


            UUID chatId = null;
            final String username = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

            String requestUri = request.getRequestURI();
            if (requestUri.equals("/rr/api/v1/chats/message/") && request.getMethod().equals("POST")) {
                ToChatMessageRequest messageRequest = this.objectMapper
                        .readValue(cachedBodyHttpServletRequest.getReader(), ToChatMessageRequest.class);
                if (messageRequest.getChatId() != null)
                    chatId = messageRequest.getChatId();
            } else if (requestUri
                    .matches("^/rr/api/v1/chats/[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-" +
                            "[a-fA-F0-9]{12}$")) {
                String[] segments = requestUri.split("/");
                chatId = UUID.fromString(segments[segments.length - 1]);
            }

            if (chatId == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("The chat room id must be included in the request.");
            } else {
                Optional<EventChatEntity> eventChatEntity = this.eventChatRepository.findById(chatId);
                if (eventChatEntity.isPresent()) {
                    wasChatFound = true;
                    // TODO: Improvement: Maybe the following operation can be improved making a custom DDBB query
                    if (eventChatEntity.get().getEvent().getEventParticipants()
                            .stream()
                            .anyMatch(ep -> ep.getParticipant().getEmail().equals(username))) {
                        isUserParticipantOfTheChat = true;
                    }
                } else {
                    Optional<PrivateChatEntity> privateChatEntityOptional =
                            this.privateChatRepository.findById(chatId);

                    if (privateChatEntityOptional.isPresent()) {
                        wasChatFound = true;
                        // TODO: Improvement: Maybe the following operation can be improved making a custom DDBB query
                        if (privateChatEntityOptional.get().getParticipants().stream()
                                .anyMatch(p -> p.getEmail().equals(username))) {
                            isUserParticipantOfTheChat = true;
                        }
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().write("Chat room with id " + chatId + " not found.");
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error processing chat filter", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An error occurred during the chat request filter process.");
        }

        if (isUserParticipantOfTheChat) {
            filterChain.doFilter(cachedBodyHttpServletRequest, response);
        } else {
            if (wasChatFound) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("You don't have access to the given chat.");
            }
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !URI_MATCHER.matches(request);
    }
}

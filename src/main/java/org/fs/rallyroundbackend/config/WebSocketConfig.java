package org.fs.rallyroundbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);
    private final ObjectMapper objectMapper;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/notification").setAllowedOrigins("http://localhost:4200");
        registry.addEndpoint("/notification").setAllowedOrigins("http://localhost:4200").withSockJS();
        registry.addEndpoint("/chat").setAllowedOrigins("http://localhost:4200");
        registry.addEndpoint("/chat").setAllowedOrigins("http://localhost:4200").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/rr")
                .setUserDestinationPrefix("/user")
                // TODO: Re-search how to use user current web socket session to send private messages.
                .enableSimpleBroker("/queue", "/topic", "/user");
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);
        return false;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor msgHeaderAccessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                log.info("Headers: {}", msgHeaderAccessor);

                assert msgHeaderAccessor != null;
                if (StompCommand.CONNECT.equals(msgHeaderAccessor.getCommand())) {
                    String authorizationHeader = msgHeaderAccessor.getFirstNativeHeader("Authorization");
                    assert authorizationHeader != null;
                    String token = authorizationHeader.substring(7);

                    String username = jwtService.getUsernameFromToken(token);
                    UserDetails userDetails= userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null,
                                    userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    msgHeaderAccessor.setUser(authToken);
                } else if(StompCommand.SUBSCRIBE.equals(msgHeaderAccessor.getCommand())) {
                    // TODO: Restrict the subscriptions to certain queues and topics
                    String destination = msgHeaderAccessor.getDestination();
                    assert destination != null;
                    if(destination.startsWith("/queue/notification/event/inscription/")) {
                    }
                }

                return message;
            }
        });
    }
}

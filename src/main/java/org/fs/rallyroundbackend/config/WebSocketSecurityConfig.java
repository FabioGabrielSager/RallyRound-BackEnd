package org.fs.rallyroundbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {
    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages
                .nullDestMatcher().authenticated()
                .simpDestMatchers("/rr/**").hasRole("PARTICIPANT")
                .simpSubscribeDestMatchers("/rr/**").denyAll()
                .simpSubscribeDestMatchers("/topic/**", "/queue/**").hasRole("PARTICIPANT")
                .simpMessageDestMatchers("/topic/**", "/queue/**").denyAll()
                .anyMessage().denyAll();
        return messages.build();
    }

    // TODO: This Bean definition overrides the built-in csrfChannelInterceptor bean.
    //  I have done this to temporarily disable websocket CSRF protection.
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean("csrfChannelInterceptor")
    public ChannelInterceptor noopCsrfChannelInterceptor() {
        return new ChannelInterceptor() {};
    }
}

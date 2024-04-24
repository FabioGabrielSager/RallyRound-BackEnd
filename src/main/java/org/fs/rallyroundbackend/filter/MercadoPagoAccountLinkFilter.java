package org.fs.rallyroundbackend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;
import org.fs.rallyroundbackend.repository.user.ParticipantRepository;
import org.fs.rallyroundbackend.service.JwtService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Security filter that checks if the requesting user has a linked Mercado Pago account before processing specific
 * requests. It only applies to requests matching the URI pattern "/rr/api/v1/events/create/".
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MercadoPagoAccountLinkFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final ParticipantRepository userRepository;
    private final AntPathRequestMatcher uriMatcher = new AntPathRequestMatcher("/rr/api/v1/events/create/");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            final String username = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

            Optional<ParticipantEntity> participantEntityOptional = this.userRepository.findEnabledUserByEmail(username);

            if(participantEntityOptional.isPresent() &&
                    participantEntityOptional.get().getMpAuthToken() != null
                    && participantEntityOptional.get().getMpAuthToken().isACompleteToken()) {
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("MercadoPago account not linked");
            }
        } catch (Exception e) {
            log.error("Error processing MercadoPago account linkage filter", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An error occurred");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !this.uriMatcher.matches(request);
    }
}

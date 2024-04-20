package org.fs.rallyroundbackend.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public interface JwtService {
    String getToken(UserDetails user);
    String getUsernameFromToken(String token);
    boolean isValidToken(String token, UserDetails user);
    String getTokenFromRequest(HttpServletRequest request);
}

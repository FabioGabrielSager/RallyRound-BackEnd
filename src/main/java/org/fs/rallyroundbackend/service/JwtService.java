package org.fs.rallyroundbackend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public interface JwtService {
    String getToken(UserDetails user);
    String getUsernameFromToken(String token);
    boolean isValidToken(String token, UserDetails user);
}

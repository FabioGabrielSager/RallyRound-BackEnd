package org.fs.rallyroundbackend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter @Setter
public class AuthResponse {
    protected String token;
    protected String username;
    protected Set<String> userRoles;
    protected UUID notificationTrayId;
    protected List<String> privileges;
}

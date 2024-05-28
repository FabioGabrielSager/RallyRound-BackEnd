package org.fs.rallyroundbackend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Data
@Builder
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter @Setter
public class AuthResponse {
    String token;
    String username;
    protected String token;
    protected String username;
    protected Set<String> userRoles;
    protected List<String> privileges;
}

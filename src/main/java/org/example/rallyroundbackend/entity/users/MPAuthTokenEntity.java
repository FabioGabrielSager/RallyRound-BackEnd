package org.example.rallyroundbackend.entity.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "mp_auth_tokens")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MPAuthTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "access_token", nullable = false)
    private String accessToken;
    @Column(name = "token_type", nullable = false)
    private String tokenType;
    @Column(name = "expire_in", nullable = false)
    private int expireIn;
    @Column(name = "user_code", nullable = false)
    private int userId;
    @Column(name = "scope", nullable = false)
    private String scope;
    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;
    @Column(name = "public_key", nullable = false)
    private String publicKey;

    @OneToOne(mappedBy = "mpAuthToken")
    private ParticipantEntity participant;
}

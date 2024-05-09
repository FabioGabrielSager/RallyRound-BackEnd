package org.fs.rallyroundbackend.entity.users.participant;

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

    @Column(name = "access_token")
    private String accessToken;
    @Column(name = "token_type")
    private String tokenType;
    @Column(name = "expire_in")
    private int expireIn;
    @Column(name = "user_code")
    private Long userId;
    @Column(name = "scope")
    private String scope;
    @Column(name = "refresh_token")
    private String refreshToken;
    @Column(name = "public_key")
    private String publicKey;

    @OneToOne(mappedBy = "mpAuthToken")
    private ParticipantEntity participant;

    public boolean isACompleteToken() {
        return accessToken != null && tokenType != null && expireIn != 0 && userId != 0
                && scope != null && refreshToken != null && publicKey != null;
    }
}

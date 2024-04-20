package org.fs.rallyroundbackend.dto.mercadopago;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class AccessTokenDto {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expire_in")
    private int expireIn;
    @JsonProperty("user_id")
    private int userId;
    @JsonProperty("scope")
    private String scope;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("public_key")
    private String publicKey;
    @JsonProperty("live_mode")
    private boolean liveMode;
}

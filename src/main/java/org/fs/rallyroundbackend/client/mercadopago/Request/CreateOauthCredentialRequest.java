package org.fs.rallyroundbackend.client.mercadopago.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateOauthCredentialRequest {
    /**
     * Specify type of operation to perform to get your credentials. This is a fixed parameter with an
     * authorization_code value.
     */
    @JsonProperty("grant_type")
    private final String grantType = "authorization_code";
    /**
     * Private key to be used in some plugins to generate payments. You can get it in Your
     * credentials.
     */
    @JsonProperty("client_secret")
    private final String clientSecret;
    /** Unique ID that identifies your integration. You can get it in Your credentials. */
    @JsonProperty("client_id")
    private final String clientId;
    /** The authorization code you get in the authorization url for linking. */
    @JsonProperty("code")
    private final String code;

    /** This is the URL you set up in the Redirect URL field in your application. */
    @JsonProperty("redirect_uri")
    private final String redirectUri;

    /** Used when you want to generate credentials for tests */
    @JsonProperty("test_token")
    private final boolean testToken;
}

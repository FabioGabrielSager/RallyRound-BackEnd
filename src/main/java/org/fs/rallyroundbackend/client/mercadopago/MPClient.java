package org.fs.rallyroundbackend.client.mercadopago;

import org.fs.rallyroundbackend.client.mercadopago.Request.CreateOauthCredentialRequest;
import org.fs.rallyroundbackend.client.mercadopago.response.MPErrorResponse;
import org.fs.rallyroundbackend.dto.mercadopago.AccessTokenDto;
import org.fs.rallyroundbackend.exception.mercadopago.MPAccessTokenRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class MPClient {
    private WebClient webClient;
    @Value("${mp.client.id}")
    private String CLIENT_ID = "";
    @Value("${mp.client.secret}")
    private String CLIENT_SECRET = "";
    @Value("${mp.redirect.url}")
    private String REDIRECT_URI;
    @Value("${mp.test.token}")
    private boolean isTestToken = false;

    public MPClient(WebClient webClient) {
        this.webClient = webClient.mutate().baseUrl("https://api.mercadopago.com").build();
    }

    public AccessTokenDto getAccessToken(String code) throws MPAccessTokenRequestException {
        CreateOauthCredentialRequest body = CreateOauthCredentialRequest.builder()
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .code(code)
                .redirectUri(REDIRECT_URI)
                .testToken(isTestToken)
                .build();

        try {
            return webClient.post().uri(uriBuilder -> uriBuilder
                            .path("/oauth/token")
                            .build())
                    .body(BodyInserters.fromValue(body))
                    .retrieve()
                    .bodyToMono(AccessTokenDto.class)
                    .block();

        } catch (WebClientResponseException e) {
            MPErrorResponse error = e.getResponseBodyAs(MPErrorResponse.class);
            throw new MPAccessTokenRequestException(error.message(), error);
        }
    }
}

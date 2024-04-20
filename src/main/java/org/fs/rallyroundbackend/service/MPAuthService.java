package org.fs.rallyroundbackend.service;

import org.fs.rallyroundbackend.exception.mercadopago.MPAccessTokenRequestException;
import org.fs.rallyroundbackend.exception.mercadopago.MPAccountAlreadyLinkedException;

import java.util.UUID;

public interface MPAuthService {
    String getAuthenticationUrl(String userEmail) throws MPAccountAlreadyLinkedException;
    void getAccessToken(String code, UUID tokenId) throws MPAccessTokenRequestException;
    boolean isAccountAlreadyLinked(String userEmail);
}

package org.fs.rallyroundbackend.service;

import jakarta.persistence.EntityNotFoundException;
import org.fs.rallyroundbackend.exception.mercadopago.MPAccessTokenRequestException;
import org.fs.rallyroundbackend.exception.mercadopago.MPAccountAlreadyLinkedException;

import java.util.UUID;

/**
 * Service interface for Mercado Pago authentication and authorization.
 */
public interface MPAuthService {
    /**
     * Generates the authentication URL for linking a Mercado Pago account with the user.
     *
     * @param userEmail The email address of the user.
     * @return The authentication URL for linking the Mercado Pago account.
     * @throws MPAccountAlreadyLinkedException If the Mercado Pago account is already linked with the user's email.
     */
    String getAuthenticationUrl(String userEmail) throws MPAccountAlreadyLinkedException;

    /**
     * Retrieves and saves the access token from Mercado Pago using the provided authorization code and token ID.
     *
     * @param code    The authorization code received from Mercado Pago.
     * @param tokenId The token ID associated with the access token request.
     * @throws MPAccessTokenRequestException If an error occurs while retrieving the access token.
     */
    void getAccessToken(String code, UUID tokenId) throws MPAccessTokenRequestException;

    /**
     * Checks if the Mercado Pago account is already linked with the specified user account.
     *
     * @param userEmail The email address of the user.
     * @return True if the Mercado Pago account is already linked, otherwise false.
     * @throws EntityNotFoundException if there is no enabled account registered with the indicated user's email.
     */
    boolean isAccountAlreadyLinked(String userEmail);
}

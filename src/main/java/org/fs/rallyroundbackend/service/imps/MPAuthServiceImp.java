package org.fs.rallyroundbackend.service.imps;

import jakarta.persistence.EntityNotFoundException;
import lombok.Setter;
import org.fs.rallyroundbackend.client.mercadopago.MPClient;
import org.fs.rallyroundbackend.dto.mercadopago.AccessTokenDto;
import org.fs.rallyroundbackend.entity.users.participant.MPAuthTokenEntity;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;
import org.fs.rallyroundbackend.exception.mercadopago.MPAccessTokenRequestException;
import org.fs.rallyroundbackend.exception.mercadopago.MPAccountAlreadyLinkedException;
import org.fs.rallyroundbackend.repository.MPAuthTokenRepository;
import org.fs.rallyroundbackend.repository.user.participant.ParticipantRepository;
import org.fs.rallyroundbackend.service.MPAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * {@link MPAuthService} implementation.
 */
@Service
public class MPAuthServiceImp implements MPAuthService {
    @Value("${mp.app.id}")
    private String APP_ID = "";
    @Value("${mp.redirect.url}")
    private String REDIRECT_URL = "";
    private MPAuthTokenRepository mpAuthTokenRepository;
    private ParticipantRepository participantRepository;
    @Setter
    private MPClient mpClient;

    public MPAuthServiceImp(MPAuthTokenRepository mpAuthTokenRepository,
                            ParticipantRepository participantRepository, MPClient mpClient) {
        this.mpAuthTokenRepository = mpAuthTokenRepository;
        this.participantRepository = participantRepository;
        this.mpClient = mpClient;
    }

    @Override
    public String getAuthenticationUrl(String userEmail) throws MPAccountAlreadyLinkedException {
        String mpAuthTokenId;

        ParticipantEntity participantEntity = this.participantRepository
                .findEnabledUserByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        if(participantEntity.getMpAuthToken() != null) {
            if(participantEntity.getMpAuthToken().isACompleteToken()) {
                throw new MPAccountAlreadyLinkedException();
            }

            mpAuthTokenId = participantEntity.getMpAuthToken().getId().toString();
        } else {
            MPAuthTokenEntity mpAuthTokenEntity = new MPAuthTokenEntity();
            mpAuthTokenEntity.setParticipant(participantEntity);
            mpAuthTokenId = this.mpAuthTokenRepository.save(mpAuthTokenEntity).getId().toString();
            participantEntity.setMpAuthToken(mpAuthTokenEntity);
            participantRepository.saveAndFlush(participantEntity);
        }

        return String.format(
                "https://auth.mercadopago.com/authorization?client_id=%s&response_type=code" +
                        "&platform_id=mp&state=%s&redirect_uri=%s", this.APP_ID, mpAuthTokenId, REDIRECT_URL);
    }

    @Override
    public void getAccessToken(String code, UUID tokenId) throws MPAccessTokenRequestException {
        MPAuthTokenEntity mpAuthTokenEntity = this.mpAuthTokenRepository.findById(tokenId).orElseThrow(
                () -> new EntityNotFoundException("Token not found.")
        );

        AccessTokenDto accessTokenDto = this.mpClient.getAccessToken(code);

        mpAuthTokenEntity.setAccessToken(accessTokenDto.getAccessToken());
        mpAuthTokenEntity.setRefreshToken(accessTokenDto.getRefreshToken());
        mpAuthTokenEntity.setTokenType(accessTokenDto.getTokenType());
        mpAuthTokenEntity.setExpireIn(accessTokenDto.getExpireIn());
        mpAuthTokenEntity.setUserId(accessTokenDto.getUserId());
        mpAuthTokenEntity.setScope(accessTokenDto.getScope());
        mpAuthTokenEntity.setPublicKey(accessTokenDto.getPublicKey());

        this.mpAuthTokenRepository.save(mpAuthTokenEntity);
    }

    @Override
    public boolean isAccountAlreadyLinked(String userEmail) {
        ParticipantEntity participantEntity = this.participantRepository
                .findEnabledUserByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        return participantEntity.getMpAuthToken() != null
                && participantEntity.getMpAuthToken().isACompleteToken();
    }
}

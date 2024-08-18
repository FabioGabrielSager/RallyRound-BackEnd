package org.fs.rallyroundbackend.service;

import jakarta.persistence.EntityNotFoundException;
import org.fs.rallyroundbackend.client.mercadopago.MPClient;
import org.fs.rallyroundbackend.client.mercadopago.response.MPErrorResponse;
import org.fs.rallyroundbackend.config.MappersConfig;
import org.fs.rallyroundbackend.dto.mercadopago.AccessTokenDto;
import org.fs.rallyroundbackend.entity.users.participant.MPAuthTokenEntity;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;
import org.fs.rallyroundbackend.exception.mercadopago.MPAccessTokenRequestException;
import org.fs.rallyroundbackend.exception.mercadopago.MPAccountAlreadyLinkedException;
import org.fs.rallyroundbackend.repository.MPAuthTokenRepository;
import org.fs.rallyroundbackend.repository.user.participant.ParticipantRepository;
import org.fs.rallyroundbackend.service.imps.MPAuthServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Import(MappersConfig.class)
public class MPAuthServiceTest {
    @Mock
    private MPAuthTokenRepository mpAuthTokenRepository;
    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private MPClient mpClient;
    @InjectMocks
    private MPAuthServiceImp mpAuthService;

    @Autowired
    private ModelMapper modelMapper;

    private ParticipantEntity participantEntityWithoutMPtoken = new ParticipantEntity();
    private MPAuthTokenEntity mpAuthTokenEntity = new MPAuthTokenEntity();

    @BeforeEach
    public void setUp() {

        this.participantEntityWithoutMPtoken = ParticipantEntity.builder()
                .name("dummy")
                .lastName("dummy")
                .email("dummy@email.com")
                .birthdate(LocalDate.of(2001, 10, 31))
                .password("dummypassword")
                .build();

        this.mpAuthTokenEntity = MPAuthTokenEntity.builder()
                .id(UUID.randomUUID())
                .build();
    }

    @Test
    public void getAuthenticationUrl_notRegisteredEmail() {
        when(participantRepository.findEnabledUserByEmail(any(String.class)))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                mpAuthService.getAuthenticationUrl("dummy@email.com"));
    }

    @Test
    public void getAuthenticationUrl_UserWithoutMPtoken() throws MPAccountAlreadyLinkedException {
        when(participantRepository.findEnabledUserByEmail(this.participantEntityWithoutMPtoken.getEmail()))
                .thenReturn(Optional.of(participantEntityWithoutMPtoken));
        when(mpAuthTokenRepository.save(any(MPAuthTokenEntity.class))).thenReturn(mpAuthTokenEntity);

        String result = this.mpAuthService.getAuthenticationUrl(this.participantEntityWithoutMPtoken.getEmail());

        assertNotNull(result);

        String stateValue = result.substring(result.lastIndexOf("state=")+6, result.indexOf("&redirect_uri"));
        assertEquals(this.mpAuthTokenEntity.getId().toString(), stateValue);

        System.out.println(result);
    }

    @Test
    public void getAuthenticationUrl_UserWithMPtoken() throws MPAccountAlreadyLinkedException {
        this.participantEntityWithoutMPtoken.setMpAuthToken(this.mpAuthTokenEntity);

        when(participantRepository.findEnabledUserByEmail(this.participantEntityWithoutMPtoken.getEmail()))
                .thenReturn(Optional.of(participantEntityWithoutMPtoken));

        String result = this.mpAuthService.getAuthenticationUrl(this.participantEntityWithoutMPtoken.getEmail());

        assertNotNull(result);

        String stateValue = result.substring(result.lastIndexOf("state=")+6, result.indexOf("&redirect_uri"));
        assertEquals(this.mpAuthTokenEntity.getId().toString(), stateValue);
    }

    @Test
    public void getAccessToken_TokenNotFound() {
        when(this.mpAuthTokenRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> this.mpAuthService
                .getAccessToken("1", UUID.randomUUID()));
    }

    @Test
    public void getAccessToken_MPbadRequest() throws MPAccessTokenRequestException {
        when(this.mpAuthTokenRepository.findById(any(UUID.class))).thenReturn(Optional.of(this.mpAuthTokenEntity));
        when(this.mpClient.getAccessToken(any(String.class))).thenThrow(
                new MPAccessTokenRequestException("invalid client_id or client_secret", new
                        MPErrorResponse("invalid client_id or client_secret", "invalid_client",
                        400, new Object[]{}))
        );

        assertThrows(MPAccessTokenRequestException.class, () ->
                this.mpAuthService.getAccessToken("1", UUID.randomUUID()));
    }

    @Test
    public void getAccessToken() throws MPAccessTokenRequestException {
        when(this.mpAuthTokenRepository.findById(any(UUID.class))).thenReturn(Optional.of(this.mpAuthTokenEntity));

        this.mpAuthTokenEntity = MPAuthTokenEntity.builder()
                .id(UUID.randomUUID())
                .accessToken("accessToken")
                .tokenType("bearer")
                .expireIn(15552000)
                .userId(241983636L)
                .scope(UUID.randomUUID().toString())
                .refreshToken("refreshToken")
                .publicKey("APP_USR-d0a26210-XXXXXXXX-479f0400869e")
                .build();

        when(this.mpClient.getAccessToken(any(String.class)))
                .thenReturn(this.modelMapper.map(this.mpAuthTokenEntity, AccessTokenDto.class));

        this.mpAuthService.getAccessToken("1", this.mpAuthTokenEntity.getId());

        verify(this.mpAuthTokenRepository).save(any(MPAuthTokenEntity.class));
    }

    @Test
    public void isAccountAlreadyLinked_isNotLinked_tokenNull() {
        when(participantRepository.findEnabledUserByEmail(this.participantEntityWithoutMPtoken.getEmail()))
                .thenReturn(Optional.of(participantEntityWithoutMPtoken));

        assertFalse(this.mpAuthService.isAccountAlreadyLinked(this.participantEntityWithoutMPtoken.getEmail()));
    }

    @Test
    public void isAccountAlreadyLinked_isNotLinked_tokenIncomplete() {
        this.participantEntityWithoutMPtoken.setMpAuthToken(this.mpAuthTokenEntity);

        when(participantRepository.findEnabledUserByEmail(this.participantEntityWithoutMPtoken.getEmail()))
                .thenReturn(Optional.of(participantEntityWithoutMPtoken));

        assertFalse(this.mpAuthService.isAccountAlreadyLinked(this.participantEntityWithoutMPtoken.getEmail()));
    }
}

package org.fs.rallyroundbackend.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.fs.rallyroundbackend.client.BingMaps.BingMapApiClient;
import org.fs.rallyroundbackend.config.MappersConfig;
import org.fs.rallyroundbackend.dto.auth.ConfirmParticipantRegistrationRequest;
import org.fs.rallyroundbackend.dto.auth.LoginRequest;
import org.fs.rallyroundbackend.dto.auth.ParticipantFavoriteActivityRequest;
import org.fs.rallyroundbackend.dto.auth.ParticipantRegistrationRequest;
import org.fs.rallyroundbackend.dto.location.places.PlaceAddressDto;
import org.fs.rallyroundbackend.dto.location.places.PlaceDto;
import org.fs.rallyroundbackend.entity.events.ActivityEntity;
import org.fs.rallyroundbackend.entity.location.EntityType;
import org.fs.rallyroundbackend.entity.users.RoleEntity;
import org.fs.rallyroundbackend.entity.users.UserEntity;
import org.fs.rallyroundbackend.entity.users.participant.EmailVerificationTokenEntity;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;
import org.fs.rallyroundbackend.exception.auth.AgeValidationException;
import org.fs.rallyroundbackend.exception.auth.FavoriteActivitiesNotSpecifiedException;
import org.fs.rallyroundbackend.exception.location.InvalidPlaceException;
import org.fs.rallyroundbackend.exception.auth.UnsuccefulyEmailVerificationException;
import org.fs.rallyroundbackend.repository.ActivityRepository;
import org.fs.rallyroundbackend.repository.user.EmailVerificationTokenRepository;
import org.fs.rallyroundbackend.repository.user.ParticipantRepository;
import org.fs.rallyroundbackend.repository.user.RoleRepository;
import org.fs.rallyroundbackend.repository.user.UserRepository;
import org.fs.rallyroundbackend.service.imps.AuthServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Import(MappersConfig.class)
public class AuthServiceTest {
    // AuthServiceImp dependencies mocks
    @Mock
    private UserRepository userRepository;
    @Mock
    private ActivityRepository activityRepository;
    @Mock
    private BingMapApiClient bingMapApiClient;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    // The following mock is just to avoid the InjectMocksException.
    @Mock
    private ModelMapper mockedModelMapper;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private LocationService locationService;
    @InjectMocks
    private AuthServiceImp authService;

    // Model mapper
    @Autowired
    private ModelMapper modelMapper;

    // Dummy data variables
    private PlaceAddressDto address = new PlaceAddressDto();

    private PlaceDto placeDto = new PlaceDto();

    private ParticipantFavoriteActivityRequest favoriteActivityRequest =
            new ParticipantFavoriteActivityRequest();
    private ParticipantFavoriteActivityRequest favoriteActivityRequest2 =
            new ParticipantFavoriteActivityRequest();

    private ParticipantFavoriteActivityRequest[] favoriteActivities = new ParticipantFavoriteActivityRequest[]{};

    private ParticipantRegistrationRequest pariticpantRegisterRequest = new ParticipantRegistrationRequest();

    private Locale locale = new Locale("es", "AR");

    private ConfirmParticipantRegistrationRequest confirmParticipantRegistrationRequest =
            new ConfirmParticipantRegistrationRequest(123456, UUID.randomUUID().toString());

    private LoginRequest loginRequest = new LoginRequest();

    @Test
    @Tag("registerParticipant")
    public void registerParticipant_withARegisteredEmail() {
        when(this.userRepository.existsByEmailAndEnabled(this.pariticpantRegisterRequest.getEmail(), true))
                .thenReturn(true);

        assertThrows(EntityExistsException.class, () -> this.authService
                .registerParticipant(this.pariticpantRegisterRequest, null,
                        this.locale));
    }

    @BeforeEach
    public void setUp() {
        this.authService.setModelMapper(this.modelMapper);

        // Setting dummy data variables
        this.address = PlaceAddressDto.builder()
                .adminDistrict("dummyDistrict")
                .adminDistrict2("dummySubdistric")
                .locality("dummyLocality")
                .countryRegion("DU")
                .formattedAddress("dummyDistrict, dummySubdistric, dummyLocality")
                .addressLine("dummyDistrict, dummyLocality")
                .postalCode("55555X")
                .neighborhood("dummyNeighborhood")
                .build();

        this.placeDto = PlaceDto.builder()
                .entityType(EntityType.Place)
                .address(address)
                .name("dummy name")
                .build();

        this.favoriteActivityRequest = new ParticipantFavoriteActivityRequest("dummyActivity1", 0);
        this.favoriteActivityRequest2 = new ParticipantFavoriteActivityRequest("dummyActivity2", 1);

        this.favoriteActivities = new ParticipantFavoriteActivityRequest[]{
                favoriteActivityRequest,
                favoriteActivityRequest2
        };

        this.pariticpantRegisterRequest = ParticipantRegistrationRequest.builder()
                .name("dummy")
                .lastName("dummy")
                .email("dummy@email.com")
                .birthdate(LocalDate.of(2001, 10, 31))
                .password("dummypassword")
                .place(placeDto)
                .favoritesActivities(favoriteActivities)
                .build();

        this.loginRequest = this.modelMapper.map(ParticipantRegistrationRequest.class, LoginRequest.class);
    }

    @Test
    @Tag("registerParticipant")
    public void registerParticipant_roleNotFound() {
        when(this.userRepository.existsByEmailAndEnabled(this.pariticpantRegisterRequest.getEmail(), true))
                .thenReturn(false);
        when(this.roleRepository.findByName("ROLE_PARTICIPANT")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> this.authService
                .registerParticipant(this.pariticpantRegisterRequest, null,
                        this.locale));
    }

    @Test
    @Tag("registerParticipant")
    public void registerParticipant_invalidAge() {
        when(this.userRepository.existsByEmailAndEnabled(this.pariticpantRegisterRequest.getEmail(), true))
                .thenReturn(false);

        this.pariticpantRegisterRequest.setBirthdate(LocalDate.of(2010, 10, 31));
        assertThrows(AgeValidationException.class, () -> this.authService
                .registerParticipant(this.pariticpantRegisterRequest, null,
                        this.locale));
    }

    @Test
    @Tag("registerParticipant")
    public void registerParticipant_invalidPlace() {
        when(this.userRepository.existsByEmailAndEnabled(this.pariticpantRegisterRequest.getEmail(), true))
                .thenReturn(false);
        when(this.roleRepository.findByName("ROLE_PARTICIPANT")).thenReturn(
                Optional.of(new RoleEntity(1, "ROLE_PARTICIPANT"))
        );
        when(this.bingMapApiClient.getAutosuggestionByPlace(this.placeDto)).thenReturn(
                Mono.just(new PlaceDto[]{})
        );

        assertThrows(InvalidPlaceException.class, () -> this.authService
                .registerParticipant(this.pariticpantRegisterRequest, null,
                        this.locale));
    }

    @Test
    @Tag("registerParticipant")
    public void registerParticipant_participantFavoriteActivitiesNotSpecified() {
        when(this.userRepository.existsByEmailAndEnabled(this.pariticpantRegisterRequest.getEmail(), true))
                .thenReturn(false);
        when(this.roleRepository.findByName("ROLE_PARTICIPANT")).thenReturn(
                Optional.of(new RoleEntity(1, "ROLE_PARTICIPANT"))
        );
        when(this.bingMapApiClient.getAutosuggestionByPlace(this.placeDto)).thenReturn(
                Mono.just(new PlaceDto[]{this.placeDto})
        );

        this.pariticpantRegisterRequest.setFavoritesActivities(new ParticipantFavoriteActivityRequest[]{});

        assertThrows(FavoriteActivitiesNotSpecifiedException.class, () -> this.authService
                .registerParticipant(this.pariticpantRegisterRequest, null,
                        this.locale));
    }

    @Test
    @Tag("registerParticipant")
    public void registerParticipant() {
        when(this.userRepository.existsByEmailAndEnabled(this.pariticpantRegisterRequest.getEmail(), true))
                .thenReturn(false);
        when(this.roleRepository.findByName("ROLE_PARTICIPANT")).thenReturn(
                Optional.of(new RoleEntity(1, "ROLE_PARTICIPANT"))
        );
        when(this.bingMapApiClient.getAutosuggestionByPlace(this.placeDto)).thenReturn(
                Mono.just(new PlaceDto[]{this.placeDto})
        );
        when(this.activityRepository.findByName(this.favoriteActivities[0].getName()))
                .thenReturn(Optional.empty());
        when(this.activityRepository.findByName(this.favoriteActivities[1].getName()))
                .thenReturn(Optional.of(ActivityEntity.builder()
                        .id(UUID.randomUUID())
                        .name("favoriteActivity2")
                        .build()));
        ParticipantEntity participantEntity = this.modelMapper
                .map(this.pariticpantRegisterRequest, ParticipantEntity.class);
        participantEntity.setId(UUID.randomUUID());
        when(this.userRepository.save(any(ParticipantEntity.class))).thenReturn(participantEntity);

        assertEquals(participantEntity.getEmail(),
                this.authService
                        .registerParticipant(pariticpantRegisterRequest, null, locale)
                        .getUserEmail());
    }

    @Test
    @Tag("registerParticipant")
    public void registerParticipant_withProfilePhoto() throws IOException {
        when(this.userRepository.existsByEmailAndEnabled(this.pariticpantRegisterRequest.getEmail(), true))
                .thenReturn(false);
        when(this.roleRepository.findByName("ROLE_PARTICIPANT")).thenReturn(
                Optional.of(new RoleEntity(1, "ROLE_PARTICIPANT"))
        );
        when(this.bingMapApiClient.getAutosuggestionByPlace(this.placeDto)).thenReturn(
                Mono.just(new PlaceDto[]{this.placeDto})
        );
        when(this.activityRepository.findByName(this.favoriteActivities[0].getName()))
                .thenReturn(Optional.empty());
        when(this.activityRepository.findByName(this.favoriteActivities[1].getName()))
                .thenReturn(Optional.of(ActivityEntity.builder()
                        .id(UUID.randomUUID())
                        .name("favoriteActivity2")
                        .build()));
        ParticipantEntity participantEntity = this.modelMapper
                .map(this.pariticpantRegisterRequest, ParticipantEntity.class);
        participantEntity.setId(UUID.randomUUID());
        when(this.userRepository.save(any(ParticipantEntity.class))).thenReturn(participantEntity);

        String content = "Dummy profile photo content";
        byte[] contentBytes = content.getBytes();

        MultipartFile profilePhoto = new MockMultipartFile(
                "profilePhoto",
                "profilePhoto.jpg",
                "image/jpeg",
                new ByteArrayInputStream(contentBytes));

        assertEquals(participantEntity.getEmail(),
                this.authService
                        .registerParticipant(pariticpantRegisterRequest, profilePhoto, locale)
                        .getUserEmail());
    }

    @Test
    @Tag("confirmParticipantRegistration")
    public void confirmParticipantRegistration_incorrectUserId() {
        try {
            this.authService.confirmParticipantRegistration(this.confirmParticipantRegistrationRequest);
        } catch (Exception ex) {
            assertInstanceOf(EntityNotFoundException.class, ex);
            assertEquals("User not found.", ex.getMessage());
        }
    }

    @Test
    @Tag("confirmParticipantRegistration")
    public void confirmParticipantRegistration_noConfirmationTokenFound() {
        ParticipantEntity participantEntity = this.modelMapper
                .map(this.pariticpantRegisterRequest, ParticipantEntity.class);
        participantEntity.setId(UUID.randomUUID());
        when(this.userRepository
                .existsByEmailAndEnabled(this.confirmParticipantRegistrationRequest.getEmail(), true))
                .thenReturn(false);
        when(this.userRepository.findDisabledUserByEmail(this.confirmParticipantRegistrationRequest.getEmail()))
                .thenReturn(Optional.of(participantEntity));
        when(this.emailVerificationTokenRepository.findByUser(participantEntity))
                .thenReturn(Optional.empty());

        try {
            this.authService.confirmParticipantRegistration(this.confirmParticipantRegistrationRequest);
        } catch (Exception ex) {
            assertInstanceOf(EntityNotFoundException.class, ex);
            assertEquals("There is no registered validation token for this user.", ex.getMessage());
        }
    }

    @Test
    @Tag("confirmParticipantRegistration")
    public void confirmParticipantRegistration_incorrectCode() {
        ParticipantEntity participantEntity = this.modelMapper
                .map(this.pariticpantRegisterRequest, ParticipantEntity.class);
        participantEntity.setId(UUID.randomUUID());

        when(this.userRepository.findDisabledUserByEmail(this.confirmParticipantRegistrationRequest.getEmail()))
                .thenReturn(Optional.of(participantEntity));

        EmailVerificationTokenEntity emailVerificationTokenEntity = EmailVerificationTokenEntity
                .builder()
                .id(UUID.randomUUID())
                .code(654321)
                .user(participantEntity)
                .expiryDate(EmailVerificationTokenEntity.calculateExpiryDate(this.locale))
                .build();
        when(this.emailVerificationTokenRepository.findByUser(participantEntity))
                .thenReturn(Optional.of(emailVerificationTokenEntity));

        assertThrows(UnsuccefulyEmailVerificationException.class, () -> {
            this.authService.confirmParticipantRegistration(this.confirmParticipantRegistrationRequest);
        });
    }

    @Test
    @Tag("confirmParticipantRegistration")
    public void confirmParticipantRegistration_expiredToken() {
        ParticipantEntity participantEntity = this.modelMapper
                .map(this.pariticpantRegisterRequest, ParticipantEntity.class);
        participantEntity.setId(UUID.randomUUID());
        when(this.userRepository
                .findDisabledUserByEmail(this.confirmParticipantRegistrationRequest.getEmail()))
                .thenReturn(Optional.of(participantEntity));

        Calendar cal = Calendar.getInstance(locale);
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, -5);
        EmailVerificationTokenEntity emailVerificationTokenEntity = EmailVerificationTokenEntity
                .builder()
                .id(UUID.randomUUID())
                .code(123456)
                .user(participantEntity)
                .expiryDate(new Date(cal.getTime().getTime()))
                .build();
        when(this.emailVerificationTokenRepository.findByUser(participantEntity))
                .thenReturn(Optional.of(emailVerificationTokenEntity));

        assertThrows(UnsuccefulyEmailVerificationException.class, () -> {
            this.authService.confirmParticipantRegistration(this.confirmParticipantRegistrationRequest);
        });
    }

    @Test
    @Tag("confirmParticipantRegistration")
    public void confirmParticipantRegistration_correctCode() {
        ParticipantEntity participantEntity = this.modelMapper
                .map(this.pariticpantRegisterRequest, ParticipantEntity.class);
        participantEntity.setId(UUID.randomUUID());
        when(this.userRepository
                .findDisabledUserByEmail(this.confirmParticipantRegistrationRequest.getEmail()))
                .thenReturn(Optional.of(participantEntity));

        EmailVerificationTokenEntity emailVerificationTokenEntity = EmailVerificationTokenEntity
                .builder()
                .id(UUID.randomUUID())
                .code(123456)
                .user(participantEntity)
                .expiryDate(EmailVerificationTokenEntity.calculateExpiryDate(this.locale))
                .build();
        when(this.emailVerificationTokenRepository.findByUser(participantEntity))
                .thenReturn(Optional.of(emailVerificationTokenEntity));

        when(this.jwtService.getToken(any(ParticipantEntity.class))).thenReturn(
                "dummyToken"
        );

        assertEquals("dummyToken",
                this.authService.confirmParticipantRegistration(this.confirmParticipantRegistrationRequest).getToken());
    }

    @Test
    @Tag("login")
    public void loginTest_incorrectUsername() {
        when(this.userRepository.findEnabledUserByEmail(this.loginRequest.getUsername())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> this.authService.login(this.loginRequest));
    }

    @Test
    @Tag("login")
    public void loginTest() {
        when(this.userRepository.findEnabledUserByEmail(this.loginRequest.getUsername())).thenReturn(
                Optional.of(this.modelMapper.map(pariticpantRegisterRequest, UserEntity.class)));
        when(this.jwtService.getToken(any(UserEntity.class))).thenReturn("dummyToken");
        assertEquals("dummyToken",
                this.authService.login(this.loginRequest).getToken());
    }
}

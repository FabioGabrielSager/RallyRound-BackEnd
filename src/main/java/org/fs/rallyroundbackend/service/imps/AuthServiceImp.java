package org.fs.rallyroundbackend.service.imps;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.fs.rallyroundbackend.client.BingMaps.BingMapApiClient;
import org.fs.rallyroundbackend.dto.auth.AuthResponse;
import org.fs.rallyroundbackend.dto.auth.ConfirmParticipantRegistrationRequest;
import org.fs.rallyroundbackend.dto.auth.LoginRequest;
import org.fs.rallyroundbackend.dto.auth.ParticipantFavoriteActivityRequest;
import org.fs.rallyroundbackend.dto.auth.ParticipantRegistrationRequest;
import org.fs.rallyroundbackend.dto.auth.ParticipantRegistrationResponse;
import org.fs.rallyroundbackend.dto.location.places.PlaceDto;
import org.fs.rallyroundbackend.entity.events.ActivityEntity;
import org.fs.rallyroundbackend.entity.users.RoleEntity;
import org.fs.rallyroundbackend.entity.users.UserEntity;
import org.fs.rallyroundbackend.entity.users.participant.EmailVerificationTokenEntity;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantFavoriteActivitiesEntity;
import org.fs.rallyroundbackend.event.EmailVerificationRequiredEvent;
import org.fs.rallyroundbackend.exception.auth.AgeValidationException;
import org.fs.rallyroundbackend.exception.auth.FavoriteActivitiesNotSpecifiedException;
import org.fs.rallyroundbackend.exception.location.InvalidPlaceException;
import org.fs.rallyroundbackend.exception.auth.UnsuccefulyEmailVerificationException;
import org.fs.rallyroundbackend.repository.ActivityRepository;
import org.fs.rallyroundbackend.repository.user.EmailVerificationTokenRepository;
import org.fs.rallyroundbackend.repository.user.RoleRepository;
import org.fs.rallyroundbackend.repository.user.UserRepository;
import org.fs.rallyroundbackend.service.AuthService;
import org.fs.rallyroundbackend.service.JwtService;
import org.fs.rallyroundbackend.service.LocationService;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * Service implementation for authentication-related operations.
 */
@Service
@AllArgsConstructor
public class AuthServiceImp implements AuthService {

    private UserRepository userRepository;
    private ActivityRepository activityRepository;
    private BingMapApiClient bingMapApiClient;
    private RoleRepository roleRepository;
    private EmailVerificationTokenRepository emailVerificationTokenRepository;
    private JwtService jwtService;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private ModelMapper modelMapper;
    private ApplicationEventPublisher applicationEventPublisher;
    private LocationService locationService;

    public void setModelMapper(@NonNull ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    /**
     * Logs in a user given their login credentials.
     *
     * @param request The login request containing the username and password.
     * @return An authentication response containing a JWT token.
     * @throws EntityNotFoundException if the specified user is not found.
     */
    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        UserEntity userEntity = userRepository.findEnabledUserByEmail(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        String token = jwtService.getToken(userEntity);

        return AuthResponse.builder()
                .token(token)
                .username(userEntity.getName())
                .build();
    }

    /**
     * Registers a new participant.
     *
     * @param request The registration request containing participant information.
     * @param profilePhoto the user profile photo.
     * @param locale  The locale to be used for the registration process.
     * @return A registration response containing the ID of the newly registered participant.
     * @throws EntityExistsException if an account with the provided email already exists.
     * @throws InvalidPlaceException if the provided place is not found using the Bing Maps API.
     */
    @Override
    public ParticipantRegistrationResponse registerParticipant(@Validated ParticipantRegistrationRequest request,
                                                               MultipartFile profilePhoto, Locale locale) {
        if (userRepository.existsByEmailAndEnabled(request.getEmail(), true)) {
            throw new EntityExistsException("There is already an account registered with that email.");
        }

        // Validate minimum age
        // Calculate the age based on the birthdate
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(request.getBirthdate(), currentDate);
        int age = period.getYears();
        if(age < 18) {
            throw new AgeValidationException("Person must be at least 18 years old.");
        }

        ParticipantEntity participantEntity = modelMapper.map(request, ParticipantEntity.class);
        participantEntity.setPassword(passwordEncoder.encode(participantEntity.getPassword()));

        RoleEntity role = this.roleRepository.findByName("ROLE_PARTICIPANT").orElseThrow(
                () -> new EntityNotFoundException("Role not found.")
        );

        // Validating the place
        PlaceDto[] bingMapApiAutosuggestionResponse =
                this.bingMapApiClient.getAutosuggestionByPlace(request.getPlace().getAddress().getFormattedAddress()).block();

        Optional<PlaceDto> filteredPlace = Arrays.stream(Objects.requireNonNull(bingMapApiAutosuggestionResponse))
                .filter(p -> p.equals(request.getPlace())).findFirst();


        if(filteredPlace.isEmpty()) {
            throw new InvalidPlaceException();
        }

        participantEntity.setPlace(this.locationService.getPlaceEntityFromPlaceDto(request.getPlace()));

        // If the user provides a profile photo, compress and set it to the ParticipantEntity.
        if (profilePhoto != null) {
            try {
                participantEntity.setProfilePhoto(profilePhoto.getBytes());
            } catch (IOException e) {   
                throw new RuntimeException(e);
            }
        }

        if(request.getFavoritesActivities().length == 0) {
            throw new FavoriteActivitiesNotSpecifiedException();
        }

        // Map the new participant favorite activities.
        TreeSet<ParticipantFavoriteActivitiesEntity> participantFavoriteActivitiesEntities = new TreeSet<>();

        for (ParticipantFavoriteActivityRequest fa : request.getFavoritesActivities()) {

            Optional<ActivityEntity> activityEntityOptional =
                    this.activityRepository.findByName(fa.getName());

            ParticipantFavoriteActivitiesEntity participantFavoriteActivitiesEntity =
                    ParticipantFavoriteActivitiesEntity.builder()
                            .participant(participantEntity)
                            .favoriteOrder(fa.getOrder())
                            .build();

            if (activityEntityOptional.isEmpty()) {
                ActivityEntity savedActivity =
                        this.activityRepository.save(ActivityEntity.builder().name(fa.getName()).build());

                participantFavoriteActivitiesEntity.setActivity(savedActivity);
            } else {
                participantFavoriteActivitiesEntity.setActivity(activityEntityOptional.get());
            }

            participantFavoriteActivitiesEntities.add(participantFavoriteActivitiesEntity);
        }

        // Set participant favorite activities
        participantEntity.setFavoriteActivities(participantFavoriteActivitiesEntities);

        // Set participant roles
        participantEntity.setRoles(Set.of(role));

        // The account should remain disabled until its associated email is registered
        participantEntity.setEnabled(false);

        ParticipantEntity savedParticipant = userRepository.save(participantEntity);

        applicationEventPublisher.publishEvent(new EmailVerificationRequiredEvent(savedParticipant.getId(),
               locale));

        return ParticipantRegistrationResponse.builder().userEmail(savedParticipant.getEmail()).build();
    }

    /**
     * Verifies the email and confirms the registration of a participant.
     *
     * @param confirmRegistrationRequest The confirmation request containing the user email and verification code.
     * @return An authentication response containing a JWT token.
     * @throws UnsuccefulyEmailVerificationException if the email verification fails.
     * @throws EntityNotFoundException if a user with the indicated id is not found o if a verificationToken for that user is no found.
     * @throws EntityExistsException if an account with the provided email already exists.
     */
    @Override
    @Transactional
    public AuthResponse confirmParticipantRegistration (
            ConfirmParticipantRegistrationRequest confirmRegistrationRequest) {

        if (userRepository.existsByEmailAndEnabled(confirmRegistrationRequest.getEmail(), true)) {
            throw new EntityExistsException("There is already an account registered with that email.");
        }

        UserEntity user =
                this.userRepository.findDisabledUserByEmail(confirmRegistrationRequest.getEmail())
                        .orElseThrow(() -> new EntityNotFoundException("User not found."));

        EmailVerificationTokenEntity emailVerificationTokenEntity =
                this.emailVerificationTokenRepository.findByUser((ParticipantEntity) user)
                        .orElseThrow( () ->
                                new EntityNotFoundException("There is no registered validation token for this user."));

        if(emailVerificationTokenEntity.isExpired()) {
            this.emailVerificationTokenRepository.delete(emailVerificationTokenEntity);
            throw new UnsuccefulyEmailVerificationException();
        }

        if (confirmRegistrationRequest.getCode() != emailVerificationTokenEntity.getCode()) {
            throw new UnsuccefulyEmailVerificationException();
        }

        user.setEnabled(true);
        this.userRepository.save(user);
        this.emailVerificationTokenRepository.delete(emailVerificationTokenEntity);

        return AuthResponse.builder().token(jwtService.getToken(user)).username(user.getName()).build();
    }

    @Override
    public void refreshEmailVerificationToken(String userEmail, Locale locale) {
        UserEntity user =
                this.userRepository.findDisabledUserByEmail(userEmail)
                        .orElseThrow(() -> new EntityNotFoundException("User not found."));

        applicationEventPublisher.publishEvent(new EmailVerificationRequiredEvent(user.getId(),
                locale));
    }
}

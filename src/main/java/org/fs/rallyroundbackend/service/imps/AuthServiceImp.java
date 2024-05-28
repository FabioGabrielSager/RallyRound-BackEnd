package org.fs.rallyroundbackend.service.imps;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.client.BingMaps.BingMapApiClient;
import org.fs.rallyroundbackend.dto.auth.AuthResponse;
import org.fs.rallyroundbackend.dto.auth.ConfirmParticipantRegistrationRequest;
import org.fs.rallyroundbackend.dto.auth.LoginRequest;
import org.fs.rallyroundbackend.dto.auth.ParticipantFavoriteActivityDto;
import org.fs.rallyroundbackend.dto.auth.ParticipantRegistrationRequest;
import org.fs.rallyroundbackend.dto.auth.ParticipantRegistrationResponse;
import org.fs.rallyroundbackend.dto.location.places.PlaceDto;
import org.fs.rallyroundbackend.entity.events.ActivityEntity;
import org.fs.rallyroundbackend.entity.users.PrivilegeEntity;
import org.fs.rallyroundbackend.entity.users.RoleEntity;
import org.fs.rallyroundbackend.entity.users.UserEntity;
import org.fs.rallyroundbackend.entity.users.participant.EmailVerificationTokenEntity;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantFavoriteActivityEntity;
import org.fs.rallyroundbackend.event.EmailVerificationRequiredEvent;
import org.fs.rallyroundbackend.exception.auth.AgeValidationException;
import org.fs.rallyroundbackend.exception.auth.FavoriteActivitiesNotSpecifiedException;
import org.fs.rallyroundbackend.exception.auth.UnsuccessfullyEmailVerificationException;
import org.fs.rallyroundbackend.exception.location.InvalidPlaceException;
import org.fs.rallyroundbackend.repository.ActivityRepository;
import org.fs.rallyroundbackend.repository.user.RoleRepository;
import org.fs.rallyroundbackend.repository.user.UserRepository;
import org.fs.rallyroundbackend.repository.user.participant.EmailVerificationTokenRepository;
import org.fs.rallyroundbackend.service.AuthService;
import org.fs.rallyroundbackend.service.JwtService;
import org.fs.rallyroundbackend.service.LocationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * {@link AuthService} implementation for authentication-related operations.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final BingMapApiClient bingMapApiClient;
    private final RoleRepository roleRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private ModelMapper modelMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final LocationService locationService;
    @Value("${spring.security.user.roles.prefix}")
    private String SPRING_SECURITY_ROLE_PREFIX;

    @Autowired
    public void setModelMapper(@NonNull ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        UserEntity user = userRepository.findEnabledUserByEmail(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        String token = jwtService.getToken(user);

        Set<String> userPrivileges = new HashSet<>();

        for(PrivilegeEntity p : user.getPrivileges()) {
            userPrivileges.add(p.getCategory().getName());
            userPrivileges.add(p.getName());
        }

        user.setLastLoginTime(LocalDateTime.now());

        this.userRepository.save(user);

        return AuthResponse.builder()
                .token(token)
                .username(user.getName())
                .userRoles(user.getRoles().stream().map(r -> r.getName().substring(SPRING_SECURITY_ROLE_PREFIX.length()))
                        .collect(Collectors.toSet()))
                .privileges(userPrivileges.stream().toList())
                .build();
    }

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
        String bingMapQuery = request.getPlace().getAddress().getAddressLine() == null
                ? request.getPlace().getAddress().getFormattedAddress()
                : request.getPlace().getAddress().getAddressLine();
        PlaceDto[] bingMapApiAutosuggestionResponse =
                this.bingMapApiClient.getAutosuggestionByPlace(bingMapQuery).block();

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
        TreeSet<ParticipantFavoriteActivityEntity> participantFavoriteActivitiesEntities = new TreeSet<>();

        for (ParticipantFavoriteActivityDto fa : request.getFavoritesActivities()) {

            Optional<ActivityEntity> activityEntityOptional =
                    this.activityRepository.findByName(fa.getName());

            ParticipantFavoriteActivityEntity participantFavoriteActivitiesEntity =
                    ParticipantFavoriteActivityEntity.builder()
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
            throw new UnsuccessfullyEmailVerificationException();
        }

        if (confirmRegistrationRequest.getCode() != emailVerificationTokenEntity.getCode()) {
            throw new UnsuccessfullyEmailVerificationException();
        }

        user.setEnabled(true);
        user.setLastLoginTime(LocalDateTime.now());
        user.setRegistrationDate(LocalDateTime.now());
        this.userRepository.save(user);
        this.emailVerificationTokenRepository.delete(emailVerificationTokenEntity);

        return AuthResponse.builder()
                .token(jwtService.getToken(user))
                .username(user.getName())
                .userRoles(user.getRoles().stream().map(r -> r.getName().substring(SPRING_SECURITY_ROLE_PREFIX.length()))
                        .collect(Collectors.toSet()))
                .privateChatId(user.getId())
                .build();
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

package org.fs.rallyroundbackend.service.imps;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.auth.AuthResponse;
import org.fs.rallyroundbackend.dto.auth.ConfirmParticipantRegistrationRequest;
import org.fs.rallyroundbackend.dto.auth.LoginRequest;
import org.fs.rallyroundbackend.dto.auth.ParticipantRegistrationRequest;
import org.fs.rallyroundbackend.dto.auth.ParticipantRegistrationResponse;
import org.fs.rallyroundbackend.entity.users.participant.EmailVerificationTokenEntity;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;
import org.fs.rallyroundbackend.entity.users.RoleEntity;
import org.fs.rallyroundbackend.entity.users.UserEntity;
import org.fs.rallyroundbackend.event.OnRegistrationRequestEvent;
import org.fs.rallyroundbackend.exception.UnsuccefulyEmailVerificationException;
import org.fs.rallyroundbackend.repository.user.EmailVerificationTokenRepository;
import org.fs.rallyroundbackend.repository.user.ParticipantRepository;
import org.fs.rallyroundbackend.repository.user.RoleRepository;
import org.fs.rallyroundbackend.repository.user.UserRepository;
import org.fs.rallyroundbackend.service.AuthService;
import org.fs.rallyroundbackend.service.JwtService;
import org.fs.rallyroundbackend.util.ImageUtil;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Service implementation for authentication-related operations.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ParticipantRepository participantRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

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

        UserEntity userEntity = userRepository.findByEmail(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        String token = jwtService.getToken(userEntity);

        return AuthResponse.builder()
                .token(token)
                .build();
    }

    /**
     * Registers a new participant.
     *
     * @param request The registration request containing participant information.
     * @param locale  The locale to be used for the registration process.
     * @return A registration response containing the ID of the newly registered participant.
     * @throws IllegalArgumentException if an account with the provided email already exists.
     */
    @Override
    public ParticipantRegistrationResponse registerParticipant(ParticipantRegistrationRequest request, Locale locale) {
        if(userRepository.existsByEmailAndEnabled(request.getEmail(), true)) {
            // TODO: Create a user exception for this.
            throw new IllegalArgumentException("There is already an account registered with that email.");
        }

        ParticipantEntity participantEntity = modelMapper.map(request, ParticipantEntity.class);
        participantEntity.setPassword(passwordEncoder.encode(participantEntity.getPassword()));

        RoleEntity role = this.roleRepository.findByName("ROLE_PARTICIPANT").orElseThrow(
                () -> new EntityNotFoundException("Role not found.")
        );

        // Compress and set the profile photo.
        try {
            participantEntity.setProfilePhoto(ImageUtil.compressImage(request.getProfilePhoto().getBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        participantEntity.setRoles(Set.of(role));

        // The account should remain disabled until its associated email is registered
        participantEntity.setEnabled(false);

        ParticipantEntity savedParticipant = userRepository.save(participantEntity);

        applicationEventPublisher.publishEvent(new OnRegistrationRequestEvent(savedParticipant.getId(),
               locale));

        return ParticipantRegistrationResponse.builder().userId(savedParticipant.getId().toString()).build();
    }

    /**
     * Verifies the email and confirms the registration of a participant.
     *
     * @param confirmRegistrationRequest The confirmation request containing the user ID and verification code.
     * @return An authentication response containing a JWT token.
     * @throws UnsuccefulyEmailVerificationException if the email verification fails.
     */
    @Override
    @Transactional
    public AuthResponse confirmParticipantRegistration(
            ConfirmParticipantRegistrationRequest confirmRegistrationRequest)
            throws UnsuccefulyEmailVerificationException {
        ParticipantEntity user =
                this.participantRepository.findById(UUID.fromString(confirmRegistrationRequest.getUserId()))
                        .orElseThrow(() -> new EntityNotFoundException("User not found."));

        EmailVerificationTokenEntity emailVerificationTokenEntity =
                this.emailVerificationTokenRepository.findByUser(user)
                        .orElseThrow(EntityNotFoundException::new);

        if (confirmRegistrationRequest.getCode() != emailVerificationTokenEntity.getCode()) {
            throw new UnsuccefulyEmailVerificationException();
        }

        user.setEnabled(true);
        this.participantRepository.save(user);

        return AuthResponse.builder().token(jwtService.getToken(user)).build();
    }
}

package org.fs.rallyroundbackend.service;

import jakarta.persistence.EntityNotFoundException;
import org.fs.rallyroundbackend.dto.auth.AuthResponse;
import org.fs.rallyroundbackend.dto.auth.ConfirmParticipantRegistrationRequest;
import org.fs.rallyroundbackend.dto.auth.LoginRequest;
import org.fs.rallyroundbackend.dto.auth.ParticipantRegistrationRequest;
import org.fs.rallyroundbackend.dto.auth.ParticipantRegistrationResponse;
import org.fs.rallyroundbackend.exception.auth.UnsuccefulyEmailVerificationException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

/**
 * Service interface for Rally Round authentication-related operations.
 */
public interface AuthService {

    /**
     * Logs in a user given their login credentials.
     *
     * @param loginRequest The login request containing the username and password.
     * @return An authentication response containing a JWT token.
     * @throws EntityNotFoundException if the specified user is not found.
     */
    AuthResponse login(LoginRequest loginRequest);

    /**
     * Registers a new participant.
     *
     * @param registerRequest The registration request containing participant information.
     * @param locale  The locale to be used for the registration process.
     * @return A registration response containing the ID of the newly registered participant.
     * @throws IllegalArgumentException if an account with the provided email already exists.
     */
    ParticipantRegistrationResponse registerParticipant(ParticipantRegistrationRequest registerRequest,
                                                        MultipartFile profilePhoto, Locale locale);

    /**
     * Verifies the email and confirms the registration of a participant.
     *
     * @param confirmRegistrationRequest The confirmation request containing the user ID and verification code.
     * @return An authentication response containing a JWT token.
     * @throws UnsuccefulyEmailVerificationException if the email verification fails.
     */
    AuthResponse confirmParticipantRegistration(ConfirmParticipantRegistrationRequest confirmRegistrationRequest) throws UnsuccefulyEmailVerificationException;

    /**
     * Refresh the email verification token of a specific user.
     *
     * @param userEmail The email of the user.
     * @param locale  The locale to be used for the registration process.
     * @throws IllegalArgumentException if an account with the provided email already exists.
     */
    void refreshEmailVerificationToken(String userEmail, Locale locale);
}

package org.fs.rallyroundbackend.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.fs.rallyroundbackend.dto.auth.AuthResponse;
import org.fs.rallyroundbackend.dto.auth.ConfirmParticipantRegistrationRequest;
import org.fs.rallyroundbackend.dto.auth.LoginRequest;
import org.fs.rallyroundbackend.dto.auth.ParticipantRegistrationRequest;
import org.fs.rallyroundbackend.dto.auth.ParticipantRegistrationResponse;
import org.fs.rallyroundbackend.dto.participant.ChangePasswordRequest;
import org.fs.rallyroundbackend.exception.auth.AgeValidationException;
import org.fs.rallyroundbackend.exception.auth.FavoriteActivitiesNotSpecifiedException;
import org.fs.rallyroundbackend.exception.auth.UnsuccessfullyEmailVerificationException;
import org.fs.rallyroundbackend.exception.location.InvalidPlaceException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
     * @throws EntityExistsException              If there is already an account registered with the provided email.
     * @throws AgeValidationException             If the person's age is less than 18 years old.
     * @throws InvalidPlaceException              If the provided place is not found.
     * @throws FavoriteActivitiesNotSpecifiedException If no favorite activities are specified in the registration
     * request.
     */
    ParticipantRegistrationResponse registerParticipant(ParticipantRegistrationRequest registerRequest,
                                                        MultipartFile profilePhoto, Locale locale);

    /**
     * Verifies the email and confirms the registration of a participant.
     *
     * @param confirmRegistrationRequest The confirmation request containing the user email and verification code.
     * @return An authentication response containing a JWT token.
     * @throws UnsuccessfullyEmailVerificationException if the email verification fails.
     * @throws EntityNotFoundException if a user with the indicated id is not found o if a verificationToken for
     * that user is no found.
     * @throws EntityExistsException if an account with the provided email already exists.
     */
    AuthResponse confirmParticipantRegistration(ConfirmParticipantRegistrationRequest confirmRegistrationRequest);

    /**
     * Refresh the email verification token of a specific user.
     *
     * @param userEmail The email of the user.
     * @param locale  The locale to be used for the registration process.
     * @throws EntityNotFoundException if a user with the indicated id is not found.
     */
    void refreshEmailVerificationToken(String userEmail, Locale locale);

    AuthResponse changeParticipantPassword(String participantEmail, ChangePasswordRequest changePasswordRequest);
}

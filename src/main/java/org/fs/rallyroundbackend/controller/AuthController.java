package org.fs.rallyroundbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.auth.AuthResponse;
import org.fs.rallyroundbackend.dto.auth.ConfirmParticipantRegistrationRequest;
import org.fs.rallyroundbackend.dto.auth.LoginRequest;
import org.fs.rallyroundbackend.dto.auth.ParticipantRegistrationRequest;
import org.fs.rallyroundbackend.dto.auth.ParticipantRegistrationResponse;
import org.fs.rallyroundbackend.dto.participant.ChangePasswordRequest;
import org.fs.rallyroundbackend.exception.auth.IncorrectPasswordException;
import org.fs.rallyroundbackend.service.AuthService;
import org.fs.rallyroundbackend.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@RestController
@RequestMapping("/rr/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(value = "/participant/register",
    consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ParticipantRegistrationResponse>
    registerParticipant(@RequestPart @Validated String participantData,
                        @RequestParam(required = false) MultipartFile profilePhoto, Locale locale)
            throws JsonProcessingException {

        ParticipantRegistrationRequest participantDataPojo = objectMapper.readValue(participantData,
                ParticipantRegistrationRequest.class);

        ParticipantRegistrationResponse response =
                authService.registerParticipant(participantDataPojo, profilePhoto, locale);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/participant/confirm/email")
    public ResponseEntity<AuthResponse> completeRegistration(@RequestBody @Validated ConfirmParticipantRegistrationRequest
                                                                     confirmParticipantRegistrationRequest) {
        return ResponseEntity.ok(
                this.authService.confirmParticipantRegistration(confirmParticipantRegistrationRequest)
        );
    }

    @PutMapping(value = "/participant/refresh/registration/token/{email}")
    public ResponseEntity<Void> refreshEmailVerificationToken(@PathVariable
                                                                  @Validated @Email @NotBlank String email,
                                                              Locale locale) {
        this.authService.refreshEmailVerificationToken(email, locale);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("password/change/")
    public ResponseEntity<AuthResponse> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest,
                                                       HttpServletRequest request) {
        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

        AuthResponse authResponse;
        try {
            authResponse = this.authService.changeParticipantPassword(userEmail, changePasswordRequest);
        } catch (IncorrectPasswordException e) {
            throw new AccessDeniedException("Invalid credentials.");
        }

        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("participant/validate/jwt")
    public ResponseEntity<Boolean> validateJwtToken() {
        return ResponseEntity.ok(true);
    }
}

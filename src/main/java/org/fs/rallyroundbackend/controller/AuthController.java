package org.fs.rallyroundbackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.auth.AuthResponse;
import org.fs.rallyroundbackend.dto.auth.ConfirmParticipantRegistrationRequest;
import org.fs.rallyroundbackend.dto.auth.LoginRequest;
import org.fs.rallyroundbackend.dto.auth.ParticipantRegistrationRequest;
import org.fs.rallyroundbackend.dto.auth.ParticipantRegistrationResponse;
import org.fs.rallyroundbackend.exception.UnsuccefulyEmailVerificationException;
import org.fs.rallyroundbackend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping("/rr/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(value = "/participant/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(value = "/participant/register")
    public ResponseEntity<ParticipantRegistrationResponse>
    registerParticipant(@RequestBody @Validated ParticipantRegistrationRequest request, Locale locale) {
        ParticipantRegistrationResponse response = authService.registerParticipant(request, locale);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/participant/confirm/email")
    public ResponseEntity<AuthResponse> completeRegistration(@RequestBody @Validated ConfirmParticipantRegistrationRequest
                                                                     confirmParticipantRegistrationRequest)
            throws UnsuccefulyEmailVerificationException {
        return ResponseEntity.ok(
                this.authService.confirmParticipantRegistration(confirmParticipantRegistrationRequest)
        );
    }
}

package org.fs.rallyroundbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.auth.AuthResponse;
import org.fs.rallyroundbackend.dto.auth.ConfirmParticipantRegistrationRequest;
import org.fs.rallyroundbackend.dto.auth.LoginRequest;
import org.fs.rallyroundbackend.dto.auth.ParticipantRegistrationRequest;
import org.fs.rallyroundbackend.dto.auth.ParticipantRegistrationResponse;
import org.fs.rallyroundbackend.exception.UnsuccefulyEmailVerificationException;
import org.fs.rallyroundbackend.service.AuthService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import java.util.Locale;

@RestController
@RequestMapping("/rr/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "/participant/login")
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
}

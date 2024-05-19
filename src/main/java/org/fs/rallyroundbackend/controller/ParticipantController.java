package org.fs.rallyroundbackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.event.CreatedEventInscriptionResultDto;
import org.fs.rallyroundbackend.dto.event.EventInscriptionPaymentLinkDto;
import org.fs.rallyroundbackend.dto.event.EventInscriptionResultDto;
import org.fs.rallyroundbackend.dto.event.EventResponseForEventCreators;
import org.fs.rallyroundbackend.dto.event.EventResponseForParticipants;
import org.fs.rallyroundbackend.dto.event.EventResumePageDto;
import org.fs.rallyroundbackend.dto.participant.ReportRequest;
import org.fs.rallyroundbackend.dto.participant.ReportResponse;
import org.fs.rallyroundbackend.dto.participant.UserPersonalDataDto;
import org.fs.rallyroundbackend.dto.participant.UserPublicDataDto;
import org.fs.rallyroundbackend.entity.users.participant.EventInscriptionStatus;
import org.fs.rallyroundbackend.entity.users.participant.MPPaymentStatus;
import org.fs.rallyroundbackend.exception.report.ReportsLimitException;
import org.fs.rallyroundbackend.service.EventInscriptionService;
import org.fs.rallyroundbackend.service.EventService;
import org.fs.rallyroundbackend.service.JwtService;
import org.fs.rallyroundbackend.service.ParticipantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rr/api/v1/participant")
@RequiredArgsConstructor
public class ParticipantController {
    private final EventInscriptionService eventInscriptionService;
    private final EventService eventService;
    private final ParticipantService participantService;
    private final JwtService jwtService;

    @PostMapping("/events/{eventId}/inscriptions/create")
    public ResponseEntity<CreatedEventInscriptionResultDto> createEventInscription(@PathVariable UUID eventId,
                                                                                   HttpServletRequest request) {
        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.eventInscriptionService.createEventInscription(eventId, userEmail));
    }

    @PutMapping("/events/{eventId}/inscriptions/complete/{hourVote}")
    public ResponseEntity<EventInscriptionResultDto> completeEventInscription(@PathVariable UUID eventId,
                                                                              @PathVariable LocalTime hourVote,
                                                                              HttpServletRequest request) {
        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

        return ResponseEntity.ok(this.eventInscriptionService.completeEventInscription(eventId, userEmail, hourVote));
    }

    @GetMapping("/events/{eventId}/inscriptions/paymentlink")
    public ResponseEntity<EventInscriptionPaymentLinkDto> retrieveInscriptionPaymentLink(@PathVariable UUID eventId,
                                                                                         HttpServletRequest request) {
        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));
        return ResponseEntity.ok(this.eventInscriptionService.getEventInscriptionPaymentLink(eventId, userEmail));
    }

    @GetMapping("/events/singedup")
    public ResponseEntity<EventResumePageDto> getEventsByParticipant(
            @Validated
            @RequestParam(required = false) String activity,
            @RequestParam(required = false) String neighborhood, @RequestParam(required = false) String locality,
            @RequestParam(required = false) String adminSubdistrict,
            @RequestParam(required = false) String adminDistrict,
            @RequestParam(required = false) LocalDate dateFrom, @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(required = false) List<LocalTime> hours,
            @RequestParam(required = false) LocalDateTime createdAt,
            @RequestParam(required = false) EventInscriptionStatus status,
            @RequestParam(required = false) MPPaymentStatus paymentStatus,
            @RequestParam(required = false) @Positive Integer limit,
            @RequestParam(required = false) @Positive Integer page,
            HttpServletRequest request) {
        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

        return ResponseEntity.ok(this.eventService
                .getEventsByParticipant(userEmail, createdAt, status, paymentStatus, activity, neighborhood, locality,
                        adminSubdistrict,
                        adminDistrict, dateFrom, dateTo, hours, limit, page));
    }

    @GetMapping("/events/created")
    public ResponseEntity<EventResumePageDto> getEventsByCreator(
            @Validated
            @RequestParam(required = false) String activity,
            @RequestParam(required = false) String neighborhood,
            @RequestParam(required = false) String locality,
            @RequestParam(required = false) String adminSubdistrict,
            @RequestParam(required = false) String adminDistrict,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(required = false) List<LocalTime> hours,
            @RequestParam(required = false) @Positive Integer limit,
            @RequestParam(required = false) @Positive Integer page,
            HttpServletRequest request
    ) {
        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

        return ResponseEntity.ok(this.eventService.getEventsByCreator(
                userEmail, activity, neighborhood, locality, adminSubdistrict,
                adminDistrict, dateFrom, dateTo, hours, limit, page)
        );
    }

    @GetMapping("/events/{id}/enrolled/")
    public ResponseEntity<EventResponseForParticipants> findParticipantSignedEventById(@PathVariable UUID id,
                                                                                       HttpServletRequest request) {
        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

        return ResponseEntity.ok(this.eventService.findParticipantSignedEventById(userEmail, id));
    }

    @GetMapping("/events/{id}/created/")
    public ResponseEntity<EventResponseForEventCreators> findParticipantCreatedEventById(@PathVariable UUID id,
                                                                                         HttpServletRequest request) {
        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

        return ResponseEntity.ok(this.eventService.findParticipantCreatedEventById(userEmail, id));
    }

    @GetMapping("public/{userId}")
    public ResponseEntity<UserPublicDataDto> getUserPublicData(@PathVariable UUID userId) {
        return ResponseEntity.ok(participantService.getParticipantPublicData(userId));
    }

    @GetMapping("personal/")
    public ResponseEntity<UserPersonalDataDto> getUserPersonalData(HttpServletRequest request) {
        // TODO: I think that this endpoint is unsafe because an attacker can change
        //  its JWT username to access to the personal data of another user.
        //  Check for a better protection mechanism.

        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));
        return ResponseEntity.ok(participantService.getPersonalData(userEmail));
    }

    @PostMapping("report/")
    public ResponseEntity<ReportResponse> registerParticipantReport(@RequestBody ReportRequest reportRequest,
                                                                    HttpServletRequest request) {
        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

        ReportResponse reportResponse;
        try {
            reportResponse = this.participantService.registerParticipantReport(reportRequest, userEmail);
        } catch (ReportsLimitException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reportResponse);
    }
}

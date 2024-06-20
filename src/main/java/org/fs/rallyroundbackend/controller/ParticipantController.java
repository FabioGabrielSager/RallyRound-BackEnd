package org.fs.rallyroundbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.event.inscription.EventsInscriptionTrendByMonthAndYear;
import org.fs.rallyroundbackend.dto.event.feedback.EventFeedbackStatistics;
import org.fs.rallyroundbackend.dto.event.inscription.CreatedEventInscriptionResultDto;
import org.fs.rallyroundbackend.dto.event.inscription.EventInscriptionPaymentLinkDto;
import org.fs.rallyroundbackend.dto.event.inscription.EventInscriptionResultDto;
import org.fs.rallyroundbackend.dto.event.EventResponseForEventCreators;
import org.fs.rallyroundbackend.dto.event.EventResponseForParticipants;
import org.fs.rallyroundbackend.dto.event.EventResumePageDto;
import org.fs.rallyroundbackend.dto.participant.ParticipantAccountModificationRequest;
import org.fs.rallyroundbackend.dto.participant.ParticipantNotificationResponse;
import org.fs.rallyroundbackend.dto.participant.ReportRequest;
import org.fs.rallyroundbackend.dto.participant.ReportResponse;
import org.fs.rallyroundbackend.dto.participant.SearchedParticipantResult;
import org.fs.rallyroundbackend.dto.participant.TopEventCreatorsResponse;
import org.fs.rallyroundbackend.dto.participant.UserPersonalDataDto;
import org.fs.rallyroundbackend.dto.participant.UserPublicDataDto;
import org.fs.rallyroundbackend.entity.users.participant.EventInscriptionStatus;
import org.fs.rallyroundbackend.entity.users.participant.MPPaymentStatus;
import org.fs.rallyroundbackend.exception.auth.IncorrectPasswordException;
import org.fs.rallyroundbackend.exception.report.ReportsLimitException;
import org.fs.rallyroundbackend.service.EventInscriptionService;
import org.fs.rallyroundbackend.service.EventService;
import org.fs.rallyroundbackend.service.JwtService;
import org.fs.rallyroundbackend.service.ParticipantNotificationService;
import org.fs.rallyroundbackend.service.ParticipantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
    private final ParticipantNotificationService participantNotificationService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

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

    @DeleteMapping("/events/{eventId}/inscriptions/cancel")
    public ResponseEntity<EventInscriptionResultDto> cancelEventInscription(@PathVariable UUID eventId,
                                                                            HttpServletRequest request) {
        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

        return ResponseEntity.ok(this.eventInscriptionService.cancelEventInscription(eventId, userEmail));
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

    @GetMapping("/events/{id}/created/feedback")
    public ResponseEntity<EventFeedbackStatistics> findParticipantCreatedEventFeedback(@PathVariable UUID id,
                                                                                       HttpServletRequest request) {
        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

        return ResponseEntity.ok(this.eventService.getEventFeedbackResume(id, userEmail));
    }

    @GetMapping("/events/created/inscription-trends")
    public ResponseEntity<EventsInscriptionTrendByMonthAndYear> getCreatedEventsInscriptionTrends(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            HttpServletRequest request) {

        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

        return ResponseEntity.ok(this.eventService.getParticipantCreatedEventsInscriptionTrend(userEmail, month, year));
    }

    @PostMapping("event/{id}/created/invite/{userId}")
    public ResponseEntity<Void> inviteUserToEvent(
            @PathVariable(value = "id") UUID eventId,
            @PathVariable UUID userId,
            HttpServletRequest request) {
        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

        this.participantService.inviteUserToEvent(eventId, userId, userEmail);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/events/{id}/leave/")
    public ResponseEntity<Void> leaveEvent(@PathVariable UUID id, HttpServletRequest request) {
        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

        this.participantService.removeParticipantFromAnEvent(userEmail, id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("public/{userId}")
    public ResponseEntity<UserPublicDataDto> getUserPublicData(@PathVariable UUID userId) {
        return ResponseEntity.ok(participantService.getParticipantPublicData(userId));
    }

    @GetMapping("personal/")
    public ResponseEntity<UserPersonalDataDto> getUserPersonalData(HttpServletRequest request) {
        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));
        return ResponseEntity.ok(participantService.getPersonalData(userEmail));
    }

    @PostMapping("report/")
    public ResponseEntity<ReportResponse> registerParticipantReport(@RequestBody @Validated ReportRequest reportRequest,
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

    @PutMapping(value = "modify/",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UserPersonalDataDto> modifyParticipantAccount(
            @RequestPart String participantData,
            @RequestParam(required = false) MultipartFile profilePhoto,
            HttpServletRequest request) throws JsonProcessingException {
        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

        ParticipantAccountModificationRequest modificationRequest = this.objectMapper.readValue(participantData,
                ParticipantAccountModificationRequest.class);

        return ResponseEntity
                .ok(this.participantService.modifyParticipantAccount(userEmail, modificationRequest, profilePhoto));
    }

    @DeleteMapping("delete/")
    public ResponseEntity<String> deleteParticipantAccount(@RequestParam String password,
                                                           HttpServletRequest request) {
        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

        try {
            this.participantService.deleteParticipantAccount(userEmail, password);
        } catch (IncorrectPasswordException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("notifications/")
    public ResponseEntity<List<ParticipantNotificationResponse>> getParticipantNotifications(HttpServletRequest request) {
        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

        return ResponseEntity.ok(this.participantNotificationService.getNotViewedParticipantNotifications(userEmail));
    }

    @PatchMapping("notifications/{id}/viewed")
    public ResponseEntity<ParticipantNotificationResponse> markNotificationAsViewed(@PathVariable(value = "id")
                                                                                    UUID notificationId,
                                                                                    HttpServletRequest request) {
        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));
        return ResponseEntity
                .ok(this.participantNotificationService.markNotificationAsViewed(notificationId, userEmail));
    }

    @GetMapping("search/{query}")
    public ResponseEntity<SearchedParticipantResult> searchParticipant(@PathVariable String query,
                                                                       @RequestParam(required = false) Integer page,
                                                                       @RequestParam(required = false) Integer limit,
                                                                       HttpServletRequest request) {
        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

        return ResponseEntity.ok(this.participantService.searchParticipant(userEmail, query, page, limit));
    }

    @GetMapping("top/five/event-creators")
    public ResponseEntity<TopEventCreatorsResponse> getTopFiveEventCreators(@RequestParam(required = false) Byte month) {
        return ResponseEntity.ok(this.participantService.getEventCreatorsTop((short) 5, month));
    }
}

package org.fs.rallyroundbackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.event.CreateEventRequest;
import org.fs.rallyroundbackend.dto.event.EventFeedbackRequest;
import org.fs.rallyroundbackend.dto.event.EventFeedbackResponse;
import org.fs.rallyroundbackend.dto.event.EventResponse;
import org.fs.rallyroundbackend.dto.event.EventResponseForEventCreators;
import org.fs.rallyroundbackend.dto.event.EventResumePageDto;
import org.fs.rallyroundbackend.service.EventService;
import org.fs.rallyroundbackend.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rr/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final JwtService jwtService;

    @PostMapping("/create/")
    public ResponseEntity<EventResponseForEventCreators> createEvent(@RequestBody @Validated CreateEventRequest eventDto,
                                                                     HttpServletRequest request) {
        
        String creatorEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

        return ResponseEntity.status(HttpStatus.CREATED).body(this.eventService.createEvent(eventDto, creatorEmail));
    }

    @PatchMapping("/cancel/{eventId}")
    public ResponseEntity<Void> modifyEvent(@PathVariable UUID eventId, HttpServletRequest request) {
        String creatorEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

        this.eventService.cancelEvent(eventId, creatorEmail);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/find/")
    public ResponseEntity<EventResumePageDto> findAllEvents(
            @Validated
            @RequestParam(required = false) String activity,
            @RequestParam(required = false) String neighborhood, @RequestParam(required = false) String locality,
            @RequestParam(required = false) String adminSubdistrict, @RequestParam(required = false) String adminDistrict,
            @RequestParam(required = false) LocalDate dateFrom, @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(required = false) List<LocalTime> hours,
            @RequestParam(required = false) boolean showOnlyAvailableEvents,
            @RequestParam(required = false) @Positive Integer limit,
            @RequestParam(required = false) @Positive Integer page,
            HttpServletRequest request
    ) {
        if(dateFrom != null && dateTo != null && (dateFrom.isEqual(dateTo) || dateFrom.isAfter(dateTo))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The dateFrom is equal to or greater than the dateTo");
        }
        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

        return ResponseEntity.ok(this.eventService.findEvents(userEmail, activity, showOnlyAvailableEvents,
                neighborhood, locality, adminSubdistrict, adminDistrict, dateFrom, dateTo, hours, limit, page));
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<EventResponse> findEventById(@PathVariable UUID id) {
        return ResponseEntity.ok(this.eventService.findEventById(id));
    }

    @PostMapping("/feedback/")
    public ResponseEntity<EventFeedbackResponse> submitFeedback(@RequestBody
                                                                    @Validated EventFeedbackRequest feedbackRequest,
                                                                HttpServletRequest request) {
        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

        EventFeedbackResponse response = this.eventService.submitFeedback(feedbackRequest, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

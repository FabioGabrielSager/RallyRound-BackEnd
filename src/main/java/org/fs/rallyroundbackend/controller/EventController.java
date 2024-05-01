package org.fs.rallyroundbackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.event.EventCompleteDto;
import org.fs.rallyroundbackend.dto.event.EventCompleteWithCreatorReputationDto;
import org.fs.rallyroundbackend.dto.event.EventDto;
import org.fs.rallyroundbackend.dto.event.EventResumePageResponse;
import org.fs.rallyroundbackend.service.EventService;
import org.fs.rallyroundbackend.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<EventCompleteDto> createEvent(@RequestBody @Validated EventDto eventDto,
                                                        HttpServletRequest request) {
        
        String creatorEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

        return ResponseEntity.ok(this.eventService.createEvent(eventDto, creatorEmail));
    }

    @GetMapping("/find/")
    public ResponseEntity<EventResumePageResponse> findAllEvents(
            @Validated
            @RequestParam(required = false) String activity,
            @RequestParam(required = false) String neighborhood, @RequestParam(required = false) String locality,
            @RequestParam(required = false) String adminSubdistrict, @RequestParam(required = false) String adminDistrict,
            @RequestParam(required = false) LocalDate dateFrom, @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(required = false) List<LocalTime> hours,
            @RequestParam(required = false) @Positive Integer limit,
            @RequestParam(required = false) @Positive Integer page
    ) {
        if(dateFrom != null && dateTo != null && (dateFrom.isEqual(dateTo) || dateFrom.isAfter(dateTo))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The dateFrom is equal to or greater than the dateTo");
        }

        // TODO: Add filter to no retrieve the events that was created for the user that is making the request.

       return ResponseEntity.ok(this.eventService.getEvents(activity, neighborhood, locality, adminSubdistrict,
               adminDistrict, dateFrom, dateTo, hours, limit, page));
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<EventCompleteWithCreatorReputationDto> findEventById(@PathVariable UUID id) {
        return ResponseEntity.ok(this.eventService.findEventById(id));
    }
}

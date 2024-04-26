package org.fs.rallyroundbackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.event.EventResponse;
import org.fs.rallyroundbackend.dto.event.EventDto;
import org.fs.rallyroundbackend.service.EventService;
import org.fs.rallyroundbackend.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rr/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final JwtService jwtService;

    @PostMapping("/create/")
    public ResponseEntity<EventResponse> createEvent(@RequestBody @Validated EventDto eventDto,
                                                     HttpServletRequest request) {
        
        String creatorEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));

        return ResponseEntity.ok(this.eventService.createEvent(eventDto, creatorEmail));
    }
}

package org.fs.rallyroundbackend.service;

import org.fs.rallyroundbackend.dto.event.EventResponse;
import org.fs.rallyroundbackend.dto.event.EventDto;

public interface EventService {
    EventResponse createEvent(EventDto eventDto, String creatorEmail);
}

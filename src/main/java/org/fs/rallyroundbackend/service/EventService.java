package org.fs.rallyroundbackend.service;

import org.fs.rallyroundbackend.dto.event.EventDto;

public interface EventService {
    EventDto createEvent(EventDto eventDto, String creatorEmail);
}

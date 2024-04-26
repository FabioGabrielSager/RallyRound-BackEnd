package org.fs.rallyroundbackend.service;

import org.fs.rallyroundbackend.dto.event.EventResponse;
import org.fs.rallyroundbackend.dto.event.EventDto;

/**
 * Service interface for managing rally round events.
 */
public interface EventService {

    /**
     * Creates a new event based on the provided event data.
     *
     * @param eventDto     The DTO representing the event to be created.
     * @param creatorEmail The email address of the user creating the event.
     * @return An {@link EventResponse} object representing the result of the event creation.
     */
    EventResponse createEvent(EventDto eventDto, String creatorEmail);
}

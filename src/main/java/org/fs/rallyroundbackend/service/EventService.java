package org.fs.rallyroundbackend.service;

import jakarta.persistence.EntityNotFoundException;
import org.fs.rallyroundbackend.dto.event.EventCompleteDto;
import org.fs.rallyroundbackend.dto.event.EventCompleteWithCreatorReputationDto;
import org.fs.rallyroundbackend.dto.event.EventDto;
import org.fs.rallyroundbackend.dto.event.EventResumePageResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing rally round events.
 */
public interface EventService {

    /**
     * Creates a new event based on the provided event data.
     *
     * @param eventDto     The DTO representing the event to be created.
     * @param creatorEmail The email address of the user creating the event.
     * @return An {@link EventCompleteDto} object representing the result of the event creation.
     * @throws EntityNotFoundException if the specified user is not found.
     */
    EventCompleteDto createEvent(EventDto eventDto, String creatorEmail);

    /**
     * Retrieves a paginated list of event summaries based on the provided parameters.
     *
     * @param activity        The name of the activity associated with the events. Can be {@code null}.
     * @param neighborhood    The name of the neighborhood where the events take place. Can be {@code null}.
     * @param locality        The name of the locality where the events take place. Can be {@code null}.
     * @param adminSubdistrict The name of the administrative subdistrict where the events take place. Can be {@code null}.
     * @param adminDistrict   The name of the administrative district where the events take place. Can be {@code null}.
     * @param dateFrom        The start date for filtering events. Can be {@code null}.
     * @param dateTo          The end date for filtering events. Can be {@code null}.
     * @param hours           A list of specific hours during which events occur. Can be {@code null}.
     * @param limit           The maximum number of events to return per page. If {@code null}, the default is ten.
     * @param page            The page number (starting from 1) to retrieve. If {@code null}, the default is one.
     * @return An {@link EventResumePageResponse} containing a page of event summaries that match the provided criteria.
     */
    EventResumePageResponse getEvents(String activity, String neighborhood, String locality, String adminSubdistrict,
                                      String adminDistrict, LocalDate dateFrom, LocalDate dateTo, List<LocalTime> hours,
                                      Integer limit, Integer page);

    /**
     * Retrieves the complete information of an event along with the reputation of its creator.
     *
     * @param eventId The unique identifier of the event to retrieve.
     * @return An {@link EventCompleteWithCreatorReputationDto} object containing detailed information about the event
     *         and the reputation of its creator.
     * @throws EntityNotFoundException if the specified event is not found.
     */
    EventCompleteWithCreatorReputationDto findEventById(UUID eventId);
}

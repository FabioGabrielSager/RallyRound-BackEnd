package org.fs.rallyroundbackend.service;

import jakarta.persistence.EntityNotFoundException;
import org.fs.rallyroundbackend.dto.event.CreatedEventDto;
import org.fs.rallyroundbackend.dto.event.EventWithCreatorReputationDto;
import org.fs.rallyroundbackend.dto.event.EventDto;
import org.fs.rallyroundbackend.dto.event.EventResumePageDto;
import org.fs.rallyroundbackend.dto.event.EventWithInscriptionStatusDto;
import org.fs.rallyroundbackend.entity.users.participant.EventInscriptionStatus;
import org.fs.rallyroundbackend.entity.users.participant.MPPaymentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
     * @return An {@link CreatedEventDto} object representing the result of the event creation.
     * @throws EntityNotFoundException if the specified user is not found.
     */
    CreatedEventDto createEvent(EventDto eventDto, String creatorEmail);

    /**
     * Retrieves a paginated list of event summaries based on the provided parameters.
     *
     * @param userEmail       The email of the user that is making the request.
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
     * @return An {@link EventResumePageDto} containing a page of event summaries that match the provided criteria.
     */
    EventResumePageDto getEvents(String userEmail, String activity, String neighborhood, String locality,
                                 String adminSubdistrict, String adminDistrict, LocalDate dateFrom,
                                 LocalDate dateTo, List<LocalTime> hours, Integer limit, Integer page);

    /**
     * Retrieves the complete information of an event along with the reputation of its creator.
     *
     * @param eventId The unique identifier of the event to retrieve.
     * @return An {@link EventWithCreatorReputationDto} object containing detailed information about the event
     *         and the reputation of its creator.
     * @throws EntityNotFoundException if the specified event is not found.
     */
    EventWithCreatorReputationDto findEventById(UUID eventId);

    /**
     * Retrieves the events created by a specific user based on the provided parameters.
     *
     * @param creatorEmail    The email address of the user who created the events.
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
     * @return An {@link EventResumePageDto} containing a page of event summaries created by the specified user
     *         that match the provided criteria.
     */
    EventResumePageDto getEventsByCreator(String creatorEmail, String activity, String neighborhood,
                                          String locality, String adminSubdistrict, String adminDistrict,
                                          LocalDate dateFrom, LocalDate dateTo, List<LocalTime> hours,
                                          Integer limit, Integer page);

    /**
     * Retrieves the events in which a specific user is participant or enrolled based on the provided parameters.
     * Note that event creators are not considered participants for this method,
     * even if they are participants in their own events.
     *
     * @param userEmail       The email address of the user participating in the events.
     * @param createdAt       The timestamp indicating when the user participated in the events.
     * @param status          The status of the user's inscription in the events.
     * @param paymentStatus   The payment status of the user's inscription in the events.
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
     * @return An {@link EventResumePageDto} containing a page of event summaries in which the specified user is
     * enrolled or participant that match the provided criteria.
     */
    EventResumePageDto getEventsByParticipant(String userEmail, LocalDateTime createdAt, EventInscriptionStatus status,
                                              MPPaymentStatus paymentStatus, String activity, String neighborhood,
                                              String locality, String adminSubdistrict, String adminDistrict,
                                              LocalDate dateFrom, LocalDate dateTo, List<LocalTime> hours,
                                              Integer limit, Integer page);

    /**
     * Retrieves detailed information about an event and the inscription status of a participant.
     *
     * @param userEmail The email address of the participant whose inscription status is to be retrieved.
     * @param eventId   The unique identifier of the event for which the inscription status is to be retrieved.
     * @return An {@link EventWithInscriptionStatusDto} object containing detailed information about the event
     *         and the inscription status of the specified participant.
     */
    EventWithInscriptionStatusDto findParticipantSignedEventById(String userEmail, UUID eventId);
}

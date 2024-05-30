package org.fs.rallyroundbackend.service;

import jakarta.persistence.EntityNotFoundException;
import org.fs.rallyroundbackend.dto.event.CreateEventRequest;
import org.fs.rallyroundbackend.dto.event.EventFeedbackRequest;
import org.fs.rallyroundbackend.dto.event.EventFeedbackResponse;
import org.fs.rallyroundbackend.dto.event.EventModificationRequest;
import org.fs.rallyroundbackend.dto.event.EventResponse;
import org.fs.rallyroundbackend.dto.event.EventResponseForEventCreators;
import org.fs.rallyroundbackend.dto.event.EventResponseForParticipants;
import org.fs.rallyroundbackend.dto.event.EventResumePageDto;
import org.fs.rallyroundbackend.entity.users.participant.EventInscriptionStatus;
import org.fs.rallyroundbackend.entity.users.participant.MPPaymentStatus;
import org.fs.rallyroundbackend.exception.event.EventFeedbackAlreadyProvidedException;
import org.fs.rallyroundbackend.exception.event.inscriptions.EventStateException;
import org.springframework.security.access.AccessDeniedException;

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
     * @return An {@link EventResponse} object representing the result of the event creation.
     * @throws EntityNotFoundException if the specified user is not found.
     */
    EventResponseForEventCreators createEvent(CreateEventRequest eventDto, String creatorEmail);

    /**
     * Retrieves a paginated list of event summaries based on the provided parameters.
     *
     * @param userEmail       The email of the user that is making the request.
     * @param activity        The name of the activity associated with the events. Can be {@code null}.
     * @param showOnlyAvailableEvents A flag indicating whether to show only available events or not.
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
    EventResumePageDto findEvents(String userEmail, String activity, boolean showOnlyAvailableEvents,
                                  String neighborhood, String locality, String adminSubdistrict, String adminDistrict,
                                  LocalDate dateFrom, LocalDate dateTo, List<LocalTime> hours, Integer limit, Integer page);

    /**
     * Retrieves the complete information of an event along with the reputation of its creator.
     *
     * @param eventId The unique identifier of the event to retrieve.
     * @return An {@link EventResponse} object containing detailed information about the event
     *         and the reputation of its creator.
     * @throws EntityNotFoundException if the specified event is not found.
     */
    EventResponse findEventById(UUID eventId);

    /**
     * Retrieves the complete information of an event along with the reputation of its creator.
     *
     * @param eventId The unique identifier of the event to retrieve.
     * @return An {@link EventResponseForEventCreators} object containing detailed information about the event
     *         and the reputation of its creator.
     * @throws EntityNotFoundException if the specified event is not found.
     */
    EventResponseForEventCreators findParticipantCreatedEventById(String userEmail, UUID eventId);

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
     * @return An {@link EventResponseForParticipants} object containing detailed information about the event
     *         and the inscription status of the specified participant.
     */
    EventResponseForParticipants findParticipantSignedEventById(String userEmail, UUID eventId);

    /**
     * Submits feedback for a finalized event by a participant.
     *
     * @param feedbackRequest The request containing the feedback details.
     * @param userEmail The email address of the participant submitting the feedback.
     * @return EventFeedbackResponse containing the details of the submitted feedback.
     * @throws EntityNotFoundException if the event or participant is not found.
     * @throws AccessDeniedException if the user did not participate in the event or if the event creator
     * attempts to submit feedback.
     * @throws EventStateException if the event is not finalized.
     * @throws EventFeedbackAlreadyProvidedException if feedback has already been provided for the event
     * xby the given user.
     */
    EventFeedbackResponse submitFeedback(EventFeedbackRequest feedbackRequest, String userEmail);

    void cancelEvent(UUID eventId, String creatorEmail);

    EventResponseForEventCreators modifyEvent(EventModificationRequest request, String creatorEmail);
}

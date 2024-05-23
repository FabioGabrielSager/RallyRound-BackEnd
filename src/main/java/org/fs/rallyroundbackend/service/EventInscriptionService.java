package org.fs.rallyroundbackend.service;

import org.fs.rallyroundbackend.dto.event.inscription.CreatedEventInscriptionResultDto;
import org.fs.rallyroundbackend.dto.event.inscription.EventInscriptionPaymentLinkDto;
import org.fs.rallyroundbackend.dto.event.inscription.EventInscriptionResultDto;

import java.time.LocalTime;
import java.util.UUID;

/**
 * Service interface for managing RR event inscriptions.
 */
public interface EventInscriptionService {
    /**
     * Creates an event inscription for the specified event and user.
     *
     * @param eventId   The ID of the event for which the inscription is created.
     * @param userEmail The email address of the user making the inscription.
     * @return The DTO containing information about the created event inscription.
     */
    CreatedEventInscriptionResultDto createEventInscription(UUID eventId, String userEmail);

    /**
     * Completes an event inscription by specifying the voted hour.
     *
     * @param eventId    The ID of the event inscription to complete.
     * @param userEmail  The email address of the user completing the inscription.
     * @param votedHour  The voted hour chosen by the user.
     * @return The DTO containing information about the completed event inscription.
     */
    EventInscriptionResultDto completeEventInscription(UUID eventId, String userEmail, LocalTime votedHour);

    /**
     * Retrieves the payment link for the event inscription.
     *
     * @param eventId   The ID of the event for which to retrieve the payment link.
     * @param userEmail The email address of the user making the payment.
     * @return The DTO containing the payment link for the event inscription,
     *         or an empty string if the event inscription is not in the state
     *         @code INCOMPLETE_MISSING_PAYMENT_AND_HOUR_VOTE.
     */
    EventInscriptionPaymentLinkDto getEventInscriptionPaymentLink(UUID eventId, String userEmail);
}

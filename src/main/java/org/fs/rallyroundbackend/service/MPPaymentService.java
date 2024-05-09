package org.fs.rallyroundbackend.service;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import org.fs.rallyroundbackend.dto.mercadopago.MpWebHookNotificationDto;
import org.fs.rallyroundbackend.entity.users.participant.EventInscriptionEntity;

/**
 * Service interface for Mercado Pago payment operations.
 */
public interface MPPaymentService {
    /**
     * Creates a preference for an event inscription in Mercado Pago.
     *
     * @param eventId   The ID of the event inscription.
     * @param useremail The email address of the user making the payment.
     * @return The preference ID generated for the event inscription.
     * @throws MPException     If an error occurs while interacting with Mercado Pago.
     * @throws MPApiException  If an error occurs in the Mercado Pago API.
     */
    String createPreferenceForAnEventInscription(EventInscriptionEntity eventId, String useremail)
            throws MPException, MPApiException;

    /**
     * Processes a payment notification for an event inscription from Mercado Pago.
     *
     * @param notification The notification DTO received from Mercado Pago webhook.
     */
    void processEventInscriptionPaymentNotification(MpWebHookNotificationDto notification);
}

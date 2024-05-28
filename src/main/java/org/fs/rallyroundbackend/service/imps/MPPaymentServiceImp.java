package org.fs.rallyroundbackend.service.imps;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.payment.PaymentItem;
import com.mercadopago.resources.preference.Preference;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fs.rallyroundbackend.dto.mercadopago.MpWebHookNotificationDto;
import org.fs.rallyroundbackend.entity.events.EventEntity;
import org.fs.rallyroundbackend.entity.events.EventParticipantEntity;
import org.fs.rallyroundbackend.entity.users.participant.EventInscriptionEntity;
import org.fs.rallyroundbackend.entity.users.participant.EventInscriptionStatus;
import org.fs.rallyroundbackend.entity.users.participant.MPAuthTokenEntity;
import org.fs.rallyroundbackend.entity.users.participant.MPPaymentStatus;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;
import org.fs.rallyroundbackend.exception.event.MissingEventCreatorException;
import org.fs.rallyroundbackend.repository.MPAuthTokenRepository;
import org.fs.rallyroundbackend.repository.event.EventInscriptionRepository;
import org.fs.rallyroundbackend.repository.event.EventRepository;
import org.fs.rallyroundbackend.repository.user.participant.ParticipantRepository;
import org.fs.rallyroundbackend.service.MPPaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MPPaymentServiceImp implements MPPaymentService {

    @Value("${mp.payments.notification.url}")
    private String PAYMENTS_NOTIFICATION_URL = "";
    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;
    private final MPAuthTokenRepository mpAuthTokenRepository;
    private final EventInscriptionRepository eventInscriptionRepository;

    @Override
    public String createPreferenceForAnEventInscription(EventInscriptionEntity inscriptionEntity, String userEmail)
            throws MPException, MPApiException {
        ParticipantEntity participant = this.participantRepository.findEnabledUserByEmail(userEmail).orElseThrow(
                () -> new EntityNotFoundException("User not found.")
        );

        EventEntity eventEntity = this.eventRepository.findById(inscriptionEntity.getEvent().getId()).orElseThrow(
                () -> new EntityNotFoundException("Event not found.")
        );

        ParticipantEntity eventCreator = eventEntity.getEventParticipants()
                .stream()
                .filter(EventParticipantEntity::isEventCreator)
                .findFirst()
                .orElseThrow(MissingEventCreatorException::new)
                .getParticipant();

        String eventCreatorFullName = String.format("%s %s", eventCreator.getName(), eventCreator.getLastName());
        String itemRequestTitle = String.format("%s event of %s", eventEntity.getActivity().getName(),
                eventCreatorFullName);

        PreferenceItemRequest itemRequest =
                PreferenceItemRequest.builder()
                        .id(inscriptionEntity.getId().toString())
                        .title(itemRequestTitle)
                        .description(eventEntity.getDescription())
                        .quantity(1)
                        .currencyId("ARS")
                        .unitPrice(eventEntity.getInscriptionPrice())
                        .build();

        PreferencePayerRequest preferencePayerRequest = PreferencePayerRequest.builder()
                .name(participant.getName())
                .surname(participant.getLastName())
                .email(participant.getEmail())
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(List.of(itemRequest))
                .payer(preferencePayerRequest)
                .notificationUrl(this.PAYMENTS_NOTIFICATION_URL)
                .build();

        PreferenceClient preferenceClient = new PreferenceClient();

        MPRequestOptions requestOptions = MPRequestOptions.builder()
                .accessToken(eventCreator.getMpAuthToken().getAccessToken())
                .build();

        Preference preference = preferenceClient.create(preferenceRequest, requestOptions);

        return preference.getInitPoint();
    }

    @Override
    public void processEventInscriptionPaymentNotification(MpWebHookNotificationDto notification) {
        MPAuthTokenEntity mpAuthTokenEntity = this.mpAuthTokenRepository.findByUserId(notification.getUserId())
                .orElseThrow(() -> {
                    log.error("Error processing event inscription payment notification: UserId doesn't found.");
                    return new EntityNotFoundException("Error processing event inscription payment notification: " +
                            "UserId doesn't found.");
                });

        PaymentClient paymentClient = new PaymentClient();

//        MPRequestOptions requestOptions = MPRequestOptions.builder()
//                .accessToken(mpAuthTokenEntity.getAccessToken())
//                .build();

        MercadoPagoConfig.setAccessToken(mpAuthTokenEntity.getAccessToken());
        Payment payment;
        PaymentItem paymentEvent;

        try {
            payment = paymentClient.get(notification.getData().getId());
            paymentEvent = payment.getAdditionalInfo().getItems().get(0);
        } catch (Exception e) {
            MercadoPagoConfig.setAccessToken(null);
            throw new RuntimeException(e.getMessage(), e.getCause());
        }

        MercadoPagoConfig.setAccessToken(null);

        EventInscriptionEntity eventInscriptionEntity =
                this.eventInscriptionRepository.findById(UUID.fromString(paymentEvent.getId()))
                        .orElseThrow(() -> {
                            log.error("Error processing event inscription payment. Event inscription doesn't found.");
                            return new EntityNotFoundException("Error processing event inscription payment. " +
                                    "Event inscription doesn't found.");
                        });

        eventInscriptionEntity.setPaymentStatus(MPPaymentStatus.valueOf(payment.getStatus()));
        
        MPPaymentStatus mpPaymentStatus = MPPaymentStatus.valueOf(payment.getStatus());   
    
        if(mpPaymentStatus.equals(MPPaymentStatus.approved)) {
            if(eventInscriptionEntity.getStatus().equals(EventInscriptionStatus
                    .INCOMPLETE_MISSING_PAYMENT_AND_HOUR_VOTE)) {
                eventInscriptionEntity
                        .setStatus(EventInscriptionStatus.INCOMPLETE_MISSING_HOUR_VOTE);
            }
        } else if (mpPaymentStatus.equals(MPPaymentStatus.cancelled)
                || mpPaymentStatus.equals(MPPaymentStatus.rejected)
                || mpPaymentStatus.equals(MPPaymentStatus.refunded)) {
            eventInscriptionEntity.setStatus(EventInscriptionStatus.REJECTED);
        }

        this.eventInscriptionRepository.save(eventInscriptionEntity);
    }
}

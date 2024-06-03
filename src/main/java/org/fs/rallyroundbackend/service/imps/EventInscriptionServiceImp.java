package org.fs.rallyroundbackend.service.imps;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.event.inscription.CreatedEventInscriptionResultDto;
import org.fs.rallyroundbackend.dto.event.inscription.EventInscriptionPaymentLinkDto;
import org.fs.rallyroundbackend.dto.event.inscription.EventInscriptionResultDto;
import org.fs.rallyroundbackend.dto.participant.ParticipantNotificationDto;
import org.fs.rallyroundbackend.entity.events.EventEntity;
import org.fs.rallyroundbackend.entity.events.EventParticipantEntity;
import org.fs.rallyroundbackend.entity.events.EventSchedulesEntity;
import org.fs.rallyroundbackend.entity.events.EventState;
import org.fs.rallyroundbackend.entity.events.ScheduleVoteEntity;
import org.fs.rallyroundbackend.entity.users.participant.EventInscriptionEntity;
import org.fs.rallyroundbackend.entity.users.participant.EventInscriptionStatus;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantNotificationType;
import org.fs.rallyroundbackend.exception.event.inscriptions.EventInscriptionAlreadyApprovedException;
import org.fs.rallyroundbackend.exception.event.inscriptions.EventInscriptionStateChangeException;
import org.fs.rallyroundbackend.exception.event.InvalidSelectedHourException;
import org.fs.rallyroundbackend.exception.event.inscriptions.EventStateException;
import org.fs.rallyroundbackend.repository.event.EventRepository;
import org.fs.rallyroundbackend.repository.user.participant.ParticipantRepository;
import org.fs.rallyroundbackend.service.EventInscriptionService;
import org.fs.rallyroundbackend.service.MPPaymentService;
import org.fs.rallyroundbackend.service.ParticipantNotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventInscriptionServiceImp implements EventInscriptionService {
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final MPPaymentService mpPaymentService;
    private final ParticipantNotificationService participantNotificationService;

    @Override
    @Transactional
    public CreatedEventInscriptionResultDto createEventInscription(UUID eventId, String userEmail) {
        ParticipantEntity joiningParticipant = this.participantRepository.findEnabledUserByEmail(userEmail).orElseThrow(
                () -> new EntityNotFoundException("User with email " + userEmail + " not found.")
        );

        EventEntity eventEntity = this.eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Event not found.")
        );

        if (eventEntity.getEventParticipants().stream().anyMatch(ep ->
                ep.getParticipant().getId().equals(joiningParticipant.getId()))) {
            throw new EventInscriptionAlreadyApprovedException();
        }

        validateEventStateForRegistration(eventEntity);

        EventInscriptionEntity inscriptionEntity = EventInscriptionEntity.builder()
                .id(UUID.randomUUID())
                .event(eventEntity)
                .status(EventInscriptionStatus.INCOMPLETE_MISSING_HOUR_VOTE)
                .participant(joiningParticipant)
                .createdAt(LocalDateTime.now())
                .build();

        if (joiningParticipant.getEventInscriptions() == null) {
            joiningParticipant.setEventInscriptions(new ArrayList<>());
        }
        joiningParticipant.getEventInscriptions().add(inscriptionEntity);

        CreatedEventInscriptionResultDto response = new CreatedEventInscriptionResultDto(
                false, "", eventId,
                EventInscriptionStatus.INCOMPLETE_MISSING_HOUR_VOTE);

        if (eventEntity.getInscriptionPrice().compareTo(BigDecimal.ZERO) > 0) {
            response.setRequiresPayment(true);
            inscriptionEntity
                    .setStatus(EventInscriptionStatus.INCOMPLETE_MISSING_PAYMENT_AND_HOUR_VOTE);
            response.setInscriptionStatus(EventInscriptionStatus
                    .INCOMPLETE_MISSING_PAYMENT_AND_HOUR_VOTE);
            try {
                String paymentLink = this.mpPaymentService.createPreferenceForAnEventInscription(inscriptionEntity,
                        userEmail);
                response.setPaymentLink(paymentLink);
                inscriptionEntity.setPaymentLink(paymentLink);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e.getCause());
            }
        }

        this.participantRepository.save(joiningParticipant);

        return response;
    }

    @Override
    @Transactional
    public EventInscriptionResultDto completeEventInscription(UUID eventId, String userEmail, LocalTime votedHour) {
        ParticipantEntity joiningParticipant = this.participantRepository.findEnabledUserByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + userEmail + " not found."));

        EventEntity eventEntity = this.eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found."));

        Optional<EventInscriptionEntity> eventInscriptionOptional = joiningParticipant.getEventInscriptions()
                .stream().filter(ei -> ei.getEvent().getId().equals(eventId)).findFirst();

        if (eventInscriptionOptional.isEmpty()) {
            throw new EntityNotFoundException("Event inscription doesn't founded.");
        }

        EventInscriptionEntity eventInscription = eventInscriptionOptional.get();

        if (!eventInscription.getStatus()
                .equals(EventInscriptionStatus.INCOMPLETE_MISSING_HOUR_VOTE)
                && !eventInscription.getStatus()
                .equals(EventInscriptionStatus.CANCELED)) {
            throw new EventInscriptionStateChangeException("Business rule violation. You can't complete an " +
                    "event inscription that is not in the state of INCOMPLETE_MISSING_HOUR_VOTE or CANCELED.");
        }


        try {
            validateEventStateForRegistration(eventEntity);
        } catch (EventStateException e) {
            //  TODO: Add logic to make the payment inscription refund when it is necessary.
            throw new EventStateException(e.getMessage());
        }

        // Update EventInscription.
        eventInscription.setStatus(EventInscriptionStatus.ACCEPTED);

        // Valid the joining participant selected hour.
        if (eventEntity.getEventSchedules()
                .stream()
                .map(es -> es.getSchedule().getStartingHour())
                .noneMatch(sh -> sh.equals(Time.valueOf(votedHour)))) {
            throw new InvalidSelectedHourException();
        }

        // Add user as participant of the given event.
        ScheduleVoteEntity scheduleVote = ScheduleVoteEntity.builder()
                .selectedHour(Time.valueOf(votedHour))
                .build();

        EventParticipantEntity eventParticipantEntity = EventParticipantEntity.builder()
                .isEventCreator(false)
                .participant(joiningParticipant)
                .event(eventEntity)
                .scheduleVote(scheduleVote)
                .build();

        scheduleVote.setEventParticipant(eventParticipantEntity);
        eventEntity.getEventParticipants().add(eventParticipantEntity);

        // Check if the participant limit was reached
        int eventParticipantsCount = eventEntity.isEventCreatorParticipant()
                ? eventEntity.getEventParticipants().size() : eventEntity.getEventParticipants().size() - 1;
        if (eventEntity.getParticipantsLimit() == eventParticipantsCount) {

            // If the participant limit was reached, calculate the event start
            // time based on the participant's time votes.
            HashMap<Time, Integer> timeVotes = new HashMap<>();

            eventEntity.getEventSchedules().forEach(es -> timeVotes.put(es.getSchedule().getStartingHour(), 0));

            eventEntity.getEventParticipants()
                    .forEach(ep -> {
                        Time selectedHour = ep.getScheduleVote().getSelectedHour();
                        timeVotes.put(selectedHour, timeVotes.get(selectedHour) + 1);
                    });

            Time mostSelectedStartTime = getMostVotedTime(eventEntity, timeVotes);

            for (EventSchedulesEntity eventSchedule : eventEntity.getEventSchedules()) {
                if (eventSchedule.getSchedule().getStartingHour().equals(mostSelectedStartTime)) {
                    eventSchedule.setSelected(true);
                }
            }

            // Update the state of the event
            eventEntity.setState(EventState.READY_TO_START);

            // Send notification to participants.
            ParticipantNotificationDto participantNotification = ParticipantNotificationDto
                    .builder()
                    .type(ParticipantNotificationType.EVENT_STATE_UPDATE)
                    .impliedResourceId(eventEntity.getId())
                    .title("Evento Listo para comenzar")
                    .message(String.format("El evento de %s organizado para el dia %s ha conseguido " +
                                    "todos los participantes necesarios",
                            eventEntity.getActivity().getName(), eventEntity.getDate()))
                    .build();

            eventEntity.getEventParticipants().forEach(ep -> {
                participantNotification.setParticipantEventCreated(ep.isEventCreator());

                this.participantNotificationService.sendNotification(participantNotification,
                        ep.getParticipant().getId());
            });

            // Update the event next state change date time
            eventEntity.setNextStateTransition(LocalDateTime.of(eventEntity.getDate(),
                    mostSelectedStartTime.toLocalTime()).minusHours(1));
        }

        this.eventRepository.save(eventEntity);
        this.participantRepository.save(joiningParticipant);

        return new EventInscriptionResultDto(eventId, EventInscriptionStatus.ACCEPTED);
    }

    @Override
    public EventInscriptionPaymentLinkDto getEventInscriptionPaymentLink(UUID eventId, String userEmail) {
        ParticipantEntity joiningParticipant = this.participantRepository.findEnabledUserByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + userEmail + " not found."));

        Optional<EventInscriptionEntity> eventInscriptionOptional = joiningParticipant.getEventInscriptions()
                .stream().filter(ei -> ei.getEvent().getId().equals(eventId)).findFirst();

        if (eventInscriptionOptional.isEmpty()) {
            throw new EntityNotFoundException("Event inscription doesn't founded.");
        }

        EventInscriptionEntity eventInscription = eventInscriptionOptional.get();

        String paymentLink = "";

        if (eventInscription.getStatus().equals(EventInscriptionStatus.INCOMPLETE_MISSING_PAYMENT_AND_HOUR_VOTE)) {
            paymentLink = eventInscription.getPaymentLink();
        }

        return new EventInscriptionPaymentLinkDto(paymentLink);
    }

    @Override
    @Transactional
    public EventInscriptionResultDto cancelEventInscription(UUID eventId, String userEmail) {
        ParticipantEntity joiningParticipant = this.participantRepository.findEnabledUserByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + userEmail + " not found."));

        Optional<EventInscriptionEntity> eventInscriptionOptional = joiningParticipant.getEventInscriptions()
                .stream().filter(ei -> ei.getEvent().getId().equals(eventId)).findFirst();

        if (eventInscriptionOptional.isEmpty()) {
            throw new EntityNotFoundException("Event inscription doesn't founded.");
        }

        EventInscriptionEntity eventInscription = eventInscriptionOptional.get();

        if (!eventInscription.getStatus()
                .equals(EventInscriptionStatus.INCOMPLETE_MISSING_HOUR_VOTE)
                && !eventInscription.getStatus()
                .equals(EventInscriptionStatus.INCOMPLETE_MISSING_PAYMENT_AND_HOUR_VOTE)) {
            throw new EventInscriptionStateChangeException("Business rule violation. You can't cancel an " +
                    "event inscription that is not in the state of INCOMPLETE_MISSING_HOUR_VOTE or " +
                    "INCOMPLETE_MISSING_PAYMENT_AND_HOUR_VOTE.");
        }

        eventInscription.setStatus(EventInscriptionStatus.CANCELED);

        this.participantRepository.save(joiningParticipant);

        return new EventInscriptionResultDto(eventId, EventInscriptionStatus.CANCELED);
    }

    private Time getMostVotedTime(EventEntity eventEntity, HashMap<Time, Integer> timeVotes) {
        Time mostSelectedStartTime = eventEntity.getEventSchedules().get(0).getSchedule().getStartingHour();
        Integer maxCount = 0;
        for (Map.Entry<Time, Integer> entry : timeVotes.entrySet()) {
            Time time = entry.getKey();
            Integer count = entry.getValue();

            if (count > maxCount) {
                mostSelectedStartTime = time;
                maxCount = count;
            } else if (count.equals(maxCount)) {
                // If there is a tie vote, randomly select one of the two times
                Random random = new Random();
                if (random.nextBoolean()) {
                    mostSelectedStartTime = time;
                }
            }
        }
        return mostSelectedStartTime;
    }

    private void validateEventStateForRegistration(EventEntity eventEntity) {
        if (eventEntity.getState().equals(EventState.READY_TO_START)
                || eventEntity.getState().equals(EventState.SOON_TO_START)) {
            throw new EventStateException("The event is already full and no more registrations are allowed.");
        }

        if (eventEntity.getState().equals(EventState.IN_PROCESS)) {
            throw new EventStateException("The event is already started. No registrations are allowed.");
        }

        if (eventEntity.getState().equals(EventState.FINALIZED)) {
            throw new EventStateException("The event is finalized. No registrations are allowed.");
        }

        if (eventEntity.getState().equals(EventState.CANCELED)) {
            throw new EventStateException("The event is canceled. No registrations are allowed.");
        }
    }
}

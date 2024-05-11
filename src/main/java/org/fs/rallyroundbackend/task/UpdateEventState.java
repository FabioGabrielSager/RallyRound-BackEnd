package org.fs.rallyroundbackend.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fs.rallyroundbackend.entity.events.DurationUnit;
import org.fs.rallyroundbackend.entity.events.EventEntity;
import org.fs.rallyroundbackend.entity.events.EventSchedulesEntity;
import org.fs.rallyroundbackend.entity.events.EventState;
import org.fs.rallyroundbackend.exception.event.MissingSelectedEventStartingTimeException;
import org.fs.rallyroundbackend.repository.event.EventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Component responsible for updating the state of events based on their schedules and current time.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateEventState {
    private final EventRepository eventRepository;
    private static final EventState[] TRACKED_EVENT_STATES = new EventState[]
            { EventState.WAITING_FOR_PARTICIPANTS, EventState.READY_TO_START, EventState.SOON_TO_START, EventState.IN_PROCESS };

    /**
     * Scheduled method to update the state of events periodically.
     * It checks events' states and updates them accordingly.
     */
    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = 1)
    @Transactional
    public void updateEventState() {
        log.info("Checking events states...");
        List<EventEntity> eventsToUpdate = this.eventRepository
                .findAllByStateInAndNextStateTransitionBefore(TRACKED_EVENT_STATES, LocalDateTime.now());

        for(EventEntity eventEntity : eventsToUpdate) {
            switch (eventEntity.getState()) {
                case WAITING_FOR_PARTICIPANTS -> eventEntity.setState(EventState.CANCELED);
                case READY_TO_START -> {
                    eventEntity.setState(EventState.SOON_TO_START);
                    eventEntity.setNextStateTransition(this
                            .getStartingLocalDateTimeFromEventEntity(eventEntity.getEventSchedules(),
                                    eventEntity.getDate()));
                    return;
                }
                case SOON_TO_START -> {
                    eventEntity.setState(EventState.IN_PROCESS);
                    LocalDateTime eventStartingDateTime = this
                            .getStartingLocalDateTimeFromEventEntity(eventEntity.getEventSchedules(),
                                    eventEntity.getDate());

                    LocalDateTime nextTransition = eventEntity.getDurationUnit().equals(DurationUnit.HOUR)
                            ? eventStartingDateTime.plusHours((long) eventEntity.getDuration())
                            : eventStartingDateTime.plusMinutes((long) eventEntity.getDuration());

                    eventEntity.setNextStateTransition(nextTransition);
                    return;
                }
                case IN_PROCESS -> eventEntity.setState(EventState.FINALIZED);
            }
        }

        this.eventRepository.saveAll(eventsToUpdate);
    }

    /**
     * Retrieves the starting local date and time of an event based on its schedules and date.
     *
     * @param eventSchedules The list of schedules associated with the event.
     * @param eventDate      The date of the event.
     * @return The starting local date and time of the event.
     * @throws MissingSelectedEventStartingTimeException if no selected starting time is found.
     */
    private LocalDateTime getStartingLocalDateTimeFromEventEntity(List<EventSchedulesEntity> eventSchedules,
                                                                  LocalDate eventDate) {
        LocalTime selectedHour = eventSchedules
                .stream()
                .filter(EventSchedulesEntity::isSelected)
                .map(es -> es.getSchedule().getStartingHour())
                .findFirst()
                .orElseThrow(MissingSelectedEventStartingTimeException::new)
                .toLocalTime();

       return LocalDateTime.of(eventDate, selectedHour);
    }
}

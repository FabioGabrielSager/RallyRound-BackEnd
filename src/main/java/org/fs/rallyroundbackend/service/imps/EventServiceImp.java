package org.fs.rallyroundbackend.service.imps;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.fs.rallyroundbackend.client.BingMaps.BingMapApiClient;
import org.fs.rallyroundbackend.dto.event.EventResponse;
import org.fs.rallyroundbackend.dto.event.EventDto;
import org.fs.rallyroundbackend.dto.event.EventParticipantResponse;
import org.fs.rallyroundbackend.dto.location.addresses.AddressDto;
import org.fs.rallyroundbackend.entity.events.ActivityEntity;
import org.fs.rallyroundbackend.entity.events.EventEntity;
import org.fs.rallyroundbackend.entity.events.EventParticipantEntity;
import org.fs.rallyroundbackend.entity.events.EventSchedulesEntity;
import org.fs.rallyroundbackend.entity.events.ScheduleEntity;
import org.fs.rallyroundbackend.entity.events.ScheduleVoteEntity;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;
import org.fs.rallyroundbackend.exception.event.InvalidSelectedHourException;
import org.fs.rallyroundbackend.exception.location.InvalidAddressException;
import org.fs.rallyroundbackend.repository.ActivityRepository;
import org.fs.rallyroundbackend.repository.event.EventRepository;
import org.fs.rallyroundbackend.repository.event.ScheduleRepository;
import org.fs.rallyroundbackend.repository.user.ParticipantRepository;
import org.fs.rallyroundbackend.service.EventService;
import org.fs.rallyroundbackend.service.LocationService;
import org.fs.rallyroundbackend.service.MPAuthService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;


/**
 * {@link EventService} implementation.
 */
@Service
@AllArgsConstructor
public class EventServiceImp implements EventService {
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final ActivityRepository activityRepository;
    private final ScheduleRepository scheduleRepository;
    private final ModelMapper modelMapper;
    private final BingMapApiClient bingMapApiClient;
    private final LocationService locationService;

    @Override
    public EventResponse createEvent(EventDto request, String creatorEmail) {
        ParticipantEntity creator = participantRepository.findEnabledUserByEmail(creatorEmail).orElseThrow(
                () -> new EntityNotFoundException("User with email " + creatorEmail + " not found")
        );

        EventEntity eventEntity = modelMapper.map(request, EventEntity.class);

        // Linked creator with the event
        EventParticipantEntity eventParticipantEntity = new EventParticipantEntity();
        eventParticipantEntity.setEvent(eventEntity);
        eventParticipantEntity.setParticipant(creator);
        eventParticipantEntity.setEventCreator(true);

        // If the event creator participates of the event, register its selected starting hour
        if(request.isEventCreatorIsParticipant()) {
            if(Arrays.stream(request.getStartHours()).noneMatch(h ->
                    h.equals(request.getEventCreatorSelectedStartHour()))) {
                throw new InvalidSelectedHourException();
            }
            ScheduleVoteEntity scheduleVote = new ScheduleVoteEntity();
            scheduleVote.setEventParticipant(eventParticipantEntity);
            scheduleVote.setSelectedHour(Time.valueOf(request.getEventCreatorSelectedStartHour()));
            eventParticipantEntity.setScheduleVote(scheduleVote);
        }

        eventEntity.setEventParticipants(new ArrayList<>(){{add(eventParticipantEntity);}});

        // Validating the address of the event
        AddressDto[] bingMapApiAutosuggestionResponse =
                this.bingMapApiClient.getAutosuggestionByAddress(
                                request.getAddress().getAddress().getAddressLine())
                        .block();

        Optional<AddressDto> filteredAddress = Arrays.stream(Objects.requireNonNull(bingMapApiAutosuggestionResponse))
                .filter(p -> p.equals(request.getAddress())).findFirst();

        if(filteredAddress.isEmpty()) {
            throw new InvalidAddressException();
        }

        eventEntity.setAddressEntity(this.locationService.getAddressEntityFromAddressDto(request.getAddress()));

        // Adding event street number
        eventEntity.setHouseNumber(request.getAddress().getAddress().getHouseNumber());

        // Adding event activity
        ActivityEntity activityEntity = new ActivityEntity();
        Optional<ActivityEntity> activityEntityOptional = this.activityRepository.findByName(request.getActivity());

        if (activityEntityOptional.isPresent()) {
            activityEntity = activityEntityOptional.get();
        } else {
            activityEntity.setName(request.getActivity());
        }

        eventEntity.setActivity(activityEntity);

        // Adding possible starting hours
        eventEntity.setEventSchedules(new ArrayList<>());

        for(LocalTime t : request.getStartHours()) {
            Optional<ScheduleEntity> scheduleEntity = scheduleRepository.findByStartingHour(Time.valueOf(t));
            EventSchedulesEntity eventSchedule = new EventSchedulesEntity();
            eventSchedule.setEvent(eventEntity);

            if (scheduleEntity.isPresent()) {
                eventSchedule.setSchedule(scheduleEntity.get());
            } else {
                ScheduleEntity schedule = new ScheduleEntity();
                schedule.setStartingHour(Time.valueOf(t));
                eventSchedule.setSchedule(schedule);
                eventSchedule.setSelected(false);
            }

            eventEntity.getEventSchedules().add(eventSchedule);
        }

        this.eventRepository.save(eventEntity);

        return new EventResponse(eventEntity.getId(), request,
                this.modelMapper.map(eventEntity.getEventParticipants(), EventParticipantResponse[].class));
    }
}

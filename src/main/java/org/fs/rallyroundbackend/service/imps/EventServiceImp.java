package org.fs.rallyroundbackend.service.imps;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.fs.rallyroundbackend.client.BingMaps.BingMapApiClient;
import org.fs.rallyroundbackend.dto.event.CreatedEventDto;
import org.fs.rallyroundbackend.dto.event.EventWithCreatorReputationDto;
import org.fs.rallyroundbackend.dto.event.EventDto;
import org.fs.rallyroundbackend.dto.event.EventParticipantDto;
import org.fs.rallyroundbackend.dto.event.EventResumeDto;
import org.fs.rallyroundbackend.dto.event.EventResumePageDto;
import org.fs.rallyroundbackend.dto.event.EventWithInscriptionStatusDto;
import org.fs.rallyroundbackend.dto.location.addresses.AddressDto;
import org.fs.rallyroundbackend.entity.events.ActivityEntity;
import org.fs.rallyroundbackend.entity.events.EventEntity;
import org.fs.rallyroundbackend.entity.events.EventParticipantEntity;
import org.fs.rallyroundbackend.entity.events.EventSchedulesEntity;
import org.fs.rallyroundbackend.entity.events.ScheduleEntity;
import org.fs.rallyroundbackend.entity.events.ScheduleVoteEntity;
import org.fs.rallyroundbackend.entity.users.participant.EventInscriptionEntity;
import org.fs.rallyroundbackend.entity.users.participant.EventInscriptionStatus;
import org.fs.rallyroundbackend.entity.users.participant.MPPaymentStatus;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantReputation;
import org.fs.rallyroundbackend.exception.event.InvalidSelectedHourException;
import org.fs.rallyroundbackend.exception.event.MissingEventCreatorException;
import org.fs.rallyroundbackend.exception.event.inscriptions.ParticipantNotInscribedException;
import org.fs.rallyroundbackend.exception.location.InvalidAddressException;
import org.fs.rallyroundbackend.repository.ActivityRepository;
import org.fs.rallyroundbackend.repository.event.EventInscriptionRepository;
import org.fs.rallyroundbackend.repository.event.EventRepository;
import org.fs.rallyroundbackend.repository.event.ScheduleRepository;
import org.fs.rallyroundbackend.repository.user.ParticipantRepository;
import org.fs.rallyroundbackend.service.EventService;
import org.fs.rallyroundbackend.service.LocationService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


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
    private final EventInscriptionRepository eventInscriptionRepository;

    @Override
    public CreatedEventDto createEvent(EventDto request, String creatorEmail) {
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
        if (request.isEventCreatorIsParticipant()) {
            if (Arrays.stream(request.getEventSchedules()).noneMatch(h ->
                    h.equals(request.getEventCreatorSelectedStartHour()))) {
                throw new InvalidSelectedHourException();
            }
            ScheduleVoteEntity scheduleVote = new ScheduleVoteEntity();
            scheduleVote.setEventParticipant(eventParticipantEntity);
            scheduleVote.setSelectedHour(Time.valueOf(request.getEventCreatorSelectedStartHour()));
            eventParticipantEntity.setScheduleVote(scheduleVote);
        }

        eventEntity.setEventParticipants(new ArrayList<>() {{
            add(eventParticipantEntity);
        }});

        // Validating the address of the event
        AddressDto[] bingMapApiAutosuggestionResponse =
                this.bingMapApiClient.getAutosuggestionByAddress(
                                request.getAddress().getAddress().getAddressLine())
                        .block();

        Optional<AddressDto> filteredAddress = Arrays.stream(Objects.requireNonNull(bingMapApiAutosuggestionResponse))
                .filter(p -> p.equals(request.getAddress())).findFirst();

        if (filteredAddress.isEmpty()) {
            throw new InvalidAddressException();
        }

        eventEntity.setAddress(this.locationService.getAddressEntityFromAddressDto(request.getAddress()));

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

        for (LocalTime t : request.getEventSchedules()) {
            EventSchedulesEntity eventSchedule = new EventSchedulesEntity();
            eventSchedule.setEvent(eventEntity);
            Optional<ScheduleEntity> scheduleEntity = scheduleRepository.findByStartingHour(Time.valueOf(t));

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

        return new CreatedEventDto(eventEntity.getId(), request,
                List.of(this.modelMapper.map(eventEntity.getEventParticipants(), EventParticipantDto[].class)));
    }

    @Override
    public EventResumePageDto getEvents(String userEmail, String activity, String neighborhood, String locality,
                                        String adminSubdistrict, String adminDistrict, LocalDate dateFrom,
                                        LocalDate dateTo, List<LocalTime> hours, Integer limit, Integer page) {

        ParticipantEntity userThatIsMakingTheRequest = participantRepository.findEnabledUserByEmail(userEmail)
                .orElseThrow(
                        () -> new EntityNotFoundException("User with email " + userEmail + " not found")
                );

        return this.fetchEventsWithPagination(null, userThatIsMakingTheRequest.getId(),
                null, activity, neighborhood, locality, adminSubdistrict, adminDistrict,
                dateFrom, dateTo, hours, limit, page);
    }

    @Override
    public EventResumePageDto getEventsByCreator(String creatorEmail, String activity, String neighborhood,
                                                 String locality, String adminSubdistrict, String adminDistrict,
                                                 LocalDate dateFrom, LocalDate dateTo, List<LocalTime> hours,
                                                 Integer limit, Integer page) {

        ParticipantEntity participant = this.participantRepository.findEnabledUserByEmail(creatorEmail).orElseThrow(
                () -> new EntityNotFoundException("User with email " + creatorEmail + " not found.")
        );

        return this.fetchEventsWithPagination(participant.getId(), null,
                null, activity, neighborhood, locality, adminSubdistrict, adminDistrict,
                dateFrom, dateTo, hours, limit, page);
    }

    @Override
    public EventWithCreatorReputationDto findEventById(UUID eventId) {
        EventEntity eventEntity = this.eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("event not found")
        );

        Optional<EventParticipantEntity> eventCreatorEntity = eventEntity.getEventParticipants()
                .stream()
                .filter(ep -> ep.isEventCreator())
                .findFirst();

        ParticipantReputation eventCreatorReputation = null;
        if (eventCreatorEntity.isPresent()) {
            ParticipantEntity creatorEntity = eventCreatorEntity.get().getParticipant();
            eventCreatorReputation = creatorEntity.getReputationAsEventCreator();
        }

        EventWithCreatorReputationDto eventCompleteWithCreatorDto = this.modelMapper.map(eventEntity,
                EventWithCreatorReputationDto.class);

        eventCompleteWithCreatorDto.setEventCreatorReputation(eventCreatorReputation);
        eventCompleteWithCreatorDto.getEvent().setActivity(eventEntity.getActivity().getName());
        eventCompleteWithCreatorDto.getEvent().setEventCreatorIsParticipant(eventEntity.isEventCreatorParticipant());

        return eventCompleteWithCreatorDto;
    }

    @Override
    public EventResumePageDto getEventsByParticipant(String participantEmail, LocalDateTime createdAt,
                                                     EventInscriptionStatus status, MPPaymentStatus paymentStatus,
                                                     String activity, String neighborhood, String locality,
                                                     String adminSubdistrict, String adminDistrict, LocalDate dateFrom,
                                                     LocalDate dateTo, List<LocalTime> hours,
                                                     Integer limit, Integer page) {

        ParticipantEntity participant = this.participantRepository.findEnabledUserByEmail(participantEmail).orElseThrow(
                () -> new EntityNotFoundException("User with email " + participantEmail + " not found.")
        );

        return this.fetchEventsWithPagination(null, null, participant.getId(), activity,
                neighborhood, locality, adminSubdistrict, adminDistrict, dateFrom, dateTo, hours, limit, page);
    }

    @Override
    public EventWithInscriptionStatusDto findParticipantSignedEventById(String userEmail, UUID eventId) {
        ParticipantEntity participant = this.participantRepository.findEnabledUserByEmail(userEmail)
                .orElseThrow(
                        () -> new EntityNotFoundException("User with email " + userEmail + " not found.")
                );

        EventEntity eventEntity = this.eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("event not found")
        );

        Optional<EventParticipantEntity> eventCreatorEntityOptional = eventEntity.getEventParticipants()
                .stream()
                .filter(ep -> ep.isEventCreator())
                .findFirst();

        EventParticipantEntity eventCreator;
        if (eventCreatorEntityOptional.isEmpty()) {
            throw new MissingEventCreatorException();
        }

        eventCreator = eventCreatorEntityOptional.get();
        ParticipantEntity creatorEntity = eventCreator.getParticipant();

        Optional<EventInscriptionEntity> eventInscriptionOptional = participant.getEventInscriptions()
                .stream()
                .filter(ei -> ei.getEvent().getId() == eventEntity.getId())
                .findFirst();

        if (eventInscriptionOptional.isEmpty()) {
            throw new ParticipantNotInscribedException();
        }

        EventInscriptionEntity eventInscription = eventInscriptionOptional.get();
        ParticipantReputation eventCreatorReputation = creatorEntity.getReputationAsEventCreator();

        EventWithInscriptionStatusDto response = this.modelMapper.map(eventEntity,
                EventWithInscriptionStatusDto.class);

        response.setEventCreatorReputation(eventCreatorReputation);
        response.getEvent().setActivity(eventEntity.getActivity().getName());
        response.getEvent().setEventCreatorIsParticipant(eventEntity.isEventCreatorParticipant());
        response.setEventInscriptionStatus(eventInscription.getStatus());

        return response;
    }

    private EventResumePageDto fetchEventsWithPagination(
            UUID creatorId, UUID participantToExcludeId, UUID participantToIncludeId, String activity,
            String neighborhood, String locality, String adminSubdistrict, String adminDistrict, LocalDate dateFrom,
            LocalDate dateTo, List<LocalTime> hours, Integer limit, Integer page) {

        LocalDate notNullDateFrom = dateFrom;
        if (notNullDateFrom == null) {
            notNullDateFrom = LocalDate.now();
        }

        LocalDate notNullDateTo = dateTo;
        if (notNullDateTo == null) {
            notNullDateTo = notNullDateFrom.plusDays(7);
        }

        Integer notNullLimit = limit;
        if (limit == null) {
            notNullLimit = 10;
        }

        Integer notNullPage = page;
        if (page == null) {
            notNullPage = 1;
        }

        List<EventEntity> eventEntities = this.eventRepository
                .findAll(
                        creatorId,
                        participantToExcludeId,
                        participantToIncludeId,
                        activity,
                        neighborhood,
                        locality,
                        adminSubdistrict,
                        adminDistrict,
                        notNullDateFrom,
                        notNullDateTo,
                        hours != null ? hours.stream().map(Time::valueOf).toList() : null,
                        notNullLimit, notNullLimit * (notNullPage - 1));


        Long totalElements = this.eventRepository
                .countAll(
                        creatorId,
                        participantToExcludeId,
                        participantToIncludeId,
                        activity,
                        neighborhood,
                        locality,
                        adminSubdistrict,
                        adminDistrict,
                        notNullDateFrom,
                        notNullDateTo,
                        hours != null ? hours.stream().map(Time::valueOf).toList() : null
                );

        List<EventResumeDto> eventResumeResponses = new ArrayList<>();

        for (EventEntity eventEntity : eventEntities) {
            EventResumeDto eventResumeResponse = EventResumeDto.builder()
                    .eventId(eventEntity.getId())
                    .activity(eventEntity.getActivity().getName())
                    .duration(eventEntity.getDuration())
                    .durationUnit(eventEntity.getDurationUnit())
                    .inscriptionPrice(eventEntity.getInscriptionPrice())
                    .date(eventEntity.getDate())
                    .address(this.modelMapper.map(eventEntity.getAddress(), AddressDto.class))
                    .participantsLimit(eventEntity.getParticipantsLimit())
                    .participantsCount(eventEntity.isEventCreatorParticipant()
                            ? eventEntity.getEventParticipants().size()
                            : eventEntity.getEventParticipants().size() - 1)
                    .eventSchedules(this.modelMapper.map(eventEntity.getEventSchedules(), LocalTime[].class))
                    .build();

            if (participantToIncludeId != null) {
                this.eventInscriptionRepository
                        .findByParticipantIdAndEvent(participantToIncludeId, eventEntity.getId())
                        .ifPresent(eventInscriptionEntity ->
                                eventResumeResponse.setInscriptionStatus(eventInscriptionEntity.getStatus()));
            }

            eventResumeResponses.add(eventResumeResponse);
        }

        return new EventResumePageDto(notNullPage, notNullLimit, totalElements, eventResumeResponses);
    }
}

package org.fs.rallyroundbackend.service.imps;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.fs.rallyroundbackend.client.BingMaps.BingMapApiClient;
import org.fs.rallyroundbackend.dto.event.*;
import org.fs.rallyroundbackend.dto.event.feedback.EventComment;
import org.fs.rallyroundbackend.dto.event.feedback.EventFeedbackRequest;
import org.fs.rallyroundbackend.dto.event.feedback.EventFeedbackResponse;
import org.fs.rallyroundbackend.dto.event.feedback.EventFeedbackStatistics;
import org.fs.rallyroundbackend.dto.location.addresses.AddressDto;
import org.fs.rallyroundbackend.dto.participant.ParticipantNotificationDto;
import org.fs.rallyroundbackend.dto.participant.ParticipantResume;
import org.fs.rallyroundbackend.entity.chats.ChatType;
import org.fs.rallyroundbackend.entity.chats.EventChatEntity;
import org.fs.rallyroundbackend.entity.events.ActivityEntity;
import org.fs.rallyroundbackend.entity.events.EventEntity;
import org.fs.rallyroundbackend.entity.events.EventFeedbackEntity;
import org.fs.rallyroundbackend.entity.events.EventParticipantEntity;
import org.fs.rallyroundbackend.entity.events.EventSchedulesEntity;
import org.fs.rallyroundbackend.entity.events.EventState;
import org.fs.rallyroundbackend.entity.events.ScheduleEntity;
import org.fs.rallyroundbackend.entity.events.ScheduleVoteEntity;
import org.fs.rallyroundbackend.entity.users.participant.EventInscriptionEntity;
import org.fs.rallyroundbackend.entity.users.participant.EventInscriptionStatus;
import org.fs.rallyroundbackend.entity.users.participant.MPPaymentStatus;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantNotificationType;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantReputation;
import org.fs.rallyroundbackend.exception.event.EventFeedbackAlreadyProvidedException;
import org.fs.rallyroundbackend.exception.event.InvalidEventStartingTimesException;
import org.fs.rallyroundbackend.exception.event.InvalidSelectedHourException;
import org.fs.rallyroundbackend.exception.event.MissingEventCreatorException;
import org.fs.rallyroundbackend.exception.event.inscriptions.EventStateException;
import org.fs.rallyroundbackend.exception.event.inscriptions.ParticipantNotInscribedException;
import org.fs.rallyroundbackend.exception.location.InvalidAddressException;
import org.fs.rallyroundbackend.repository.ActivityRepository;
import org.fs.rallyroundbackend.repository.event.EventInscriptionRepository;
import org.fs.rallyroundbackend.repository.event.EventParticipantRepository;
import org.fs.rallyroundbackend.repository.event.EventRepository;
import org.fs.rallyroundbackend.repository.event.EventScheduleRepository;
import org.fs.rallyroundbackend.repository.event.EventScheduleVoteRepository;
import org.fs.rallyroundbackend.repository.event.ScheduleRepository;
import org.fs.rallyroundbackend.repository.user.participant.ParticipantRepository;
import org.fs.rallyroundbackend.service.EventService;
import org.fs.rallyroundbackend.service.LocationService;
import org.fs.rallyroundbackend.service.ParticipantNotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final EventParticipantRepository eventParticipantRepository;
    private final EventScheduleVoteRepository eventScheduleVoteRepository;
    private final EventScheduleRepository eventScheduleRepository;
    private final ParticipantNotificationService participantNotificationService;

    @Override
    @Transactional
    public EventResponseForEventCreators createEvent(CreateEventRequest request, String creatorEmail) {
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

        // Creating and adding event chat
        EventChatEntity eventChatEntity = EventChatEntity.builder()
                .chatType(ChatType.EVENT_CHAT)
                .event(eventEntity)
                .build();

        eventEntity.setChat(eventChatEntity);

        // Init event state
        eventEntity.setState(EventState.WAITING_FOR_PARTICIPANTS);
        eventEntity.setNextStateTransition(
                LocalDateTime.of(request.getDate(),
                        Arrays.stream(request.getEventSchedules()).max(LocalTime::compareTo).get())
        );

        this.eventRepository.save(eventEntity);

        EventResponseForEventCreators eventResponse =
                this.modelMapper.map(eventEntity, EventResponseForEventCreators.class);
        eventResponse.setChatId(eventChatEntity.getChatId());
        eventResponse.setEventCreatorReputation(creator.getReputationAsEventCreator());
        eventResponse.setActivity(eventEntity.getActivity().getName());
        eventResponse.setEventCreatorIsParticipant(eventEntity.isEventCreatorParticipant());
        if (eventEntity.isEventCreatorParticipant()) {
            eventResponse.setSelectedStartingHour(request.getEventCreatorSelectedStartHour());
            if (eventEntity.getEventSchedules().size() > 1) {
                eventResponse.setStartingHoursTimesVoted(this.countVotesForStartingHour(eventEntity.getEventSchedules(),
                        eventEntity.getEventParticipants()));
            }
        }

        return eventResponse;
    }

    @Override
    @Transactional
    public EventResponseForEventCreators modifyEvent(EventModificationRequest request, String creatorEmail) {
        ParticipantEntity participant = this.participantRepository.findEnabledUserByEmail(creatorEmail).orElseThrow(
                () -> new EntityNotFoundException("User with email " + creatorEmail + " not found.")
        );

        EventEntity eventEntity = this.eventRepository
                .findEventByIdAndEventCreator(participant.getId(), request.getEventId())
                .orElseThrow(
                        () -> new EntityNotFoundException("event not found.")
                );

        // I can get without an if present check because i already know that the event creator
        // of this event exists, and it's the requester.
        EventParticipantEntity creatorEventParticipantEntity = this.eventParticipantRepository
                .findByParticipantIdAndEventId(participant.getId(), eventEntity.getId())
                .get();

        if (eventEntity.getState() != EventState.WAITING_FOR_PARTICIPANTS) {
            throw new EventStateException("Only events that are waiting for participants can be modified.");
        }

        if (request.getActivity() != null) {
            ActivityEntity activityEntity = new ActivityEntity();
            Optional<ActivityEntity> activityEntityOptional = this.activityRepository.findByName(request.getActivity());

            if (activityEntityOptional.isPresent()) {
                activityEntity = activityEntityOptional.get();
            } else {
                activityEntity.setName(request.getActivity());
            }

            eventEntity.setActivity(activityEntity);
        }

        if (request.getDescription() != null && !request.getDescription().isEmpty()) {
            eventEntity.setDescription(request.getDescription());
        }

        if (request.getDuration() != null && !request.getDuration().isEmpty()) {
            eventEntity.setDuration(Double.parseDouble(request.getDuration()));
        }

        if (request.getDurationUnit() != null) {
            eventEntity.setDurationUnit(request.getDurationUnit());
        }

        if (request.getInscriptionPrice() != null) {
            eventEntity.setInscriptionPrice(request.getInscriptionPrice());
        }

        if (request.getDate() != null) {
            eventEntity.setDate(request.getDate());
        }

        if (request.getAddress() != null) {
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
        }

        if (request.getParticipantsLimit() != null) {
            eventEntity.setParticipantsLimit(request.getParticipantsLimit());
        }

        if (request.getEventSchedules() != null) {
            if ((request.getEventCreatorIsParticipant() != null && request.getEventCreatorIsParticipant()
                    || eventEntity.isEventCreatorParticipant())
                    && request.getEventCreatorSelectedStartHour() == null) {
                if (Arrays.stream(request.getEventSchedules())
                        .noneMatch(es ->
                                es.equals(creatorEventParticipantEntity.getScheduleVote().getSelectedHour()
                                        .toLocalTime()))) {
                    throw new InvalidEventStartingTimesException("The new event's starting times do not match with " +
                            "event creator selected starting hour, you must update it also.");
                }
            }

            this.eventScheduleRepository.deleteAll(eventEntity.getEventSchedules());
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
        }

        if (request.getEventCreatorIsParticipant() != null) {
            eventEntity.setEventCreatorParticipant(request.getEventCreatorIsParticipant());
            if (!request.getEventCreatorIsParticipant()) {
                if (creatorEventParticipantEntity.getScheduleVote() != null) {
                    this.eventScheduleVoteRepository.delete(creatorEventParticipantEntity.getScheduleVote());
                    creatorEventParticipantEntity.setScheduleVote(null);
                }
            }
        }

        if(request.getEventCreatorSelectedStartHour() != null
                && ((request.getEventCreatorIsParticipant() == null && eventEntity.isEventCreatorParticipant())
                || Boolean.TRUE.equals(request.getEventCreatorIsParticipant())) ) {

            // If the event creator participates of the event, register its selected starting hour
            if (eventEntity.getEventSchedules()
                    .stream()
                    .noneMatch(es -> es.getSchedule().getStartingHour().toLocalTime()
                            .equals(request.getEventCreatorSelectedStartHour()))) {
                throw new InvalidSelectedHourException();
            }

            ScheduleVoteEntity scheduleVote = new ScheduleVoteEntity();

            if (creatorEventParticipantEntity.getScheduleVote() != null) {
                scheduleVote = creatorEventParticipantEntity.getScheduleVote();
            }

            scheduleVote.setEventParticipant(creatorEventParticipantEntity);
            scheduleVote.setSelectedHour(Time.valueOf(request.getEventCreatorSelectedStartHour()));
            creatorEventParticipantEntity.setScheduleVote(scheduleVote);
        }

        this.eventRepository.save(eventEntity);

        EventResponseForEventCreators eventResponse =
                this.modelMapper.map(eventEntity, EventResponseForEventCreators.class);

        eventResponse.setChatId(eventEntity.getChat().getChatId());
        eventResponse.setEventCreatorReputation(participant.getReputationAsEventCreator());
        eventResponse.setActivity(eventEntity.getActivity().getName());
        eventResponse.setEventCreatorIsParticipant(eventEntity.isEventCreatorParticipant());
        if (eventEntity.isEventCreatorParticipant()) {
            eventResponse.setSelectedStartingHour(creatorEventParticipantEntity
                    .getScheduleVote().getSelectedHour().toLocalTime());
            if (eventEntity.getEventSchedules().size() > 1) {
                eventResponse.setStartingHoursTimesVoted(this.countVotesForStartingHour(eventEntity.getEventSchedules(),
                        eventEntity.getEventParticipants()));
            }
        }

        return eventResponse;
    }

    @Override
    @Transactional
    public EventResumePageDto findEvents(String userEmail, String activity, boolean showOnlyAvailableEvents,
                                         String neighborhood, String locality, String adminSubdistrict,
                                         String adminDistrict, LocalDate dateFrom, LocalDate dateTo,
                                         List<LocalTime> hours, Integer limit, Integer page) {

        ParticipantEntity userThatIsMakingTheRequest = participantRepository.findEnabledUserByEmail(userEmail)
                .orElseThrow(
                        () -> new EntityNotFoundException("User with email " + userEmail + " not found")
                );

        return this.fetchEventsWithPagination(null, userThatIsMakingTheRequest.getId(),
                null, showOnlyAvailableEvents ? EventState.WAITING_FOR_PARTICIPANTS : null,
                activity, neighborhood, locality, adminSubdistrict, adminDistrict,
                dateFrom, dateTo, hours, limit, page);
    }

    @Override
    @Transactional
    public EventResumePageDto getEventsByCreator(String creatorEmail, String activity, String neighborhood,
                                                 String locality, String adminSubdistrict, String adminDistrict,
                                                 LocalDate dateFrom, LocalDate dateTo, List<LocalTime> hours,
                                                 Integer limit, Integer page) {

        ParticipantEntity participant = this.participantRepository.findEnabledUserByEmail(creatorEmail).orElseThrow(
                () -> new EntityNotFoundException("User with email " + creatorEmail + " not found.")
        );

        return this.fetchEventsWithPagination(participant.getId(), null,
                null, null, activity, neighborhood, locality, adminSubdistrict, adminDistrict,
                dateFrom, dateTo, hours, limit, page);
    }

    @Override
    @Transactional
    public EventResponse findEventById(UUID eventId) {
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

        EventResponse eventResponse =
                this.modelMapper.map(eventEntity, EventResponse.class);
        eventResponse.setEventCreatorReputation(eventCreatorReputation);
        eventResponse.setActivity(eventEntity.getActivity().getName());
        eventResponse.setEventCreatorIsParticipant(eventEntity.isEventCreatorParticipant());
        if (eventEntity.getEventSchedules().size() > 1) {
            eventResponse.setStartingHoursTimesVoted(this.countVotesForStartingHour(eventEntity.getEventSchedules(),
                    eventEntity.getEventParticipants()));
        }

        return eventResponse;
    }

    @Override
    @Transactional
    public EventResponseForEventCreators findParticipantCreatedEventById(String userEmail, UUID eventId) {
        this.participantRepository.findEnabledUserByEmail(userEmail)
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

        if (eventCreatorEntityOptional.isEmpty()) {
            throw new MissingEventCreatorException();
        }

        EventParticipantEntity eventCreator = eventCreatorEntityOptional.get();
        ParticipantEntity creatorEntity = eventCreator.getParticipant();

        if (!creatorEntity.getEmail().equals(userEmail)) {
            throw new ParticipantNotInscribedException("The given user is not the creator of the given event");
        }

        EventResponseForEventCreators eventResponse =
                this.modelMapper.map(eventEntity, EventResponseForEventCreators.class);
        eventResponse.setChatId(eventEntity.getChat().getChatId());
        eventResponse.setEventCreatorReputation(creatorEntity.getReputationAsEventCreator());
        eventResponse.setActivity(eventEntity.getActivity().getName());
        eventResponse.setEventCreatorIsParticipant(eventEntity.isEventCreatorParticipant());

        if (eventEntity.isEventCreatorParticipant()) {
            Optional<EventParticipantEntity> creatorEventParticipant = creatorEntity.getEventParticipants()
                    .stream()
                    .filter(ep -> ep.getEvent().getId() == eventEntity.getId())
                    .findFirst();

            creatorEventParticipant.ifPresent(eventParticipantEntity -> eventResponse
                    .setSelectedStartingHour(eventParticipantEntity
                            .getScheduleVote()
                            .getSelectedHour()
                            .toLocalTime()));
        }

        if (eventEntity.getEventSchedules().size() > 1) {
            eventResponse.setStartingHoursTimesVoted(this.countVotesForStartingHour(eventEntity.getEventSchedules(),
                    eventEntity.getEventParticipants()));
        }

        return eventResponse;
    }

    @Override
    public EventsInscriptionTrendByMonthAndYear getParticipantCreatedEventsInscriptionTrend(String userEmail, Integer month,
                                                                                            Integer year) {
        int validMonth = LocalDate.now().getMonth().getValue();
        if(month != null && month > 0 && month < 13) {
            validMonth = month;
        }

        int validYear = LocalDate.now().getYear();
        if(year != null) {
            validYear = year;
        }

        EventsInscriptionTrendByMonthAndYear result = new EventsInscriptionTrendByMonthAndYear();

        result.setMonth(validMonth);
        result.setYear(validYear);

        List<Object[]> eventsInscriptionTrends = this.eventRepository
                .getCreatorEventsInscriptionTrendByMonthAndYear(userEmail, validMonth, validYear);

        result.setResults(eventsInscriptionTrends.stream()
                .map(e -> new EventInscriptionTrendByEvent(
                        (String) e[0],
                        (LocalDate) e[1],
                        (Long) e[2],
                        (Long) e[3],
                        (Long) e[4],
                        (Long) e[5]
                )).toList());

        return result;
    }

    @Override
    @Transactional
    public EventResumePageDto getEventsByParticipant(String participantEmail, LocalDateTime createdAt,
                                                     EventInscriptionStatus status, MPPaymentStatus paymentStatus,
                                                     String activity, String neighborhood, String locality,
                                                     String adminSubdistrict, String adminDistrict, LocalDate dateFrom,
                                                     LocalDate dateTo, List<LocalTime> hours,
                                                     Integer limit, Integer page) {

        ParticipantEntity participant = this.participantRepository.findEnabledUserByEmail(participantEmail).orElseThrow(
                () -> new EntityNotFoundException("User with email " + participantEmail + " not found.")
        );

        return this.fetchEventsWithPagination(null, null, participant.getId(),
                null, activity, neighborhood, locality, adminSubdistrict, adminDistrict, dateFrom,
                dateTo, hours, limit, page);
    }

    @Override
    @Transactional
    public EventResponseForParticipants findParticipantSignedEventById(String userEmail, UUID eventId) {
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
                .filter(ei -> ei.getEvent().getId() == eventEntity.getId() 
                        && ei.getStatus() != EventInscriptionStatus.CANCELED
                        && ei.getStatus() != EventInscriptionStatus.CANCELED_DUE_TO_ABANDONMENT
                        && ei.getStatus() != EventInscriptionStatus.REJECTED)
                .findFirst();

        if (eventInscriptionOptional.isEmpty()) {
            throw new ParticipantNotInscribedException();
        }

        EventInscriptionEntity eventInscription = eventInscriptionOptional.get();

        EventResponseForParticipants eventResponse =
                this.modelMapper.map(eventEntity, EventResponseForParticipants.class);

        eventResponse.setEventInscriptionStatus(eventInscription.getStatus());
        eventResponse.setChatId(eventEntity.getChat().getChatId());
        eventResponse.setEventCreatorReputation(creatorEntity.getReputationAsEventCreator());
        eventResponse.setActivity(eventEntity.getActivity().getName());
        eventResponse.setEventCreatorIsParticipant(eventEntity.isEventCreatorParticipant());

        Optional<EventParticipantEntity> eventParticipant = participant.getEventParticipants()
                .stream()
                .filter(ep -> ep.getEvent().getId() == eventEntity.getId())
                .findFirst();

        eventParticipant.ifPresent(eventParticipantEntity -> {
            eventResponse
                    .setSelectedStartingHour(eventParticipantEntity
                            .getScheduleVote()
                            .getSelectedHour()
                            .toLocalTime());
            if (eventEntity.getState() == EventState.FINALIZED) {
                eventResponse.setHasAlreadySentEventFeedback(eventParticipantEntity.getFeedback() != null);
            }
        });

        if (eventEntity.getEventSchedules().size() > 1) {
            eventResponse.setStartingHoursTimesVoted(this.countVotesForStartingHour(eventEntity.getEventSchedules(),
                    eventEntity.getEventParticipants()));
        }

        return eventResponse;
    }

    @Override
    @Transactional
    public EventFeedbackResponse submitFeedback(EventFeedbackRequest feedbackRequest, String userEmail) {
        EventEntity event = this.eventRepository.findById(feedbackRequest.getEventId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found."));

        ParticipantEntity participant = this.participantRepository.findEnabledUserByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + userEmail + " not found."));

        EventParticipantEntity eventParticipantEntity = this.eventParticipantRepository
                .findByParticipantIdAndEventId(participant.getId(), event.getId())
                .orElseThrow(() -> new AccessDeniedException("User did not participate in this event."));

        if (!event.getState().equals(EventState.FINALIZED)) {
            throw new EventStateException("Cannot provide feedback for non-finalized events.");
        }

        if (eventParticipantEntity.isEventCreator()) {
            throw new AccessDeniedException("The event creator can not give feedback about their own event.");
        }

        if (eventParticipantEntity.getFeedback() != null) {
            throw new EventFeedbackAlreadyProvidedException();
        }

        EventFeedbackEntity feedbackEntity = this.modelMapper.map(feedbackRequest, EventFeedbackEntity.class);

        feedbackEntity.setId(null);
        feedbackEntity.setEventParticipant(eventParticipantEntity);

        eventParticipantEntity.setFeedback(feedbackEntity);

        this.eventParticipantRepository.save(eventParticipantEntity);

        return new EventFeedbackResponse(eventParticipantEntity.getFeedback().getId(),
                "Feedback submitted successfully");
    }

    @Override
    public EventFeedbackStatistics getEventFeedbackResume(UUID eventId, String creatorEmail) {
        UUID creatorId = this.participantRepository.findEnabledUserByEmail(creatorEmail)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + creatorEmail + " not found."))
                .getId();

        if(!this.eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event not found");
        }

        if(this.eventRepository.isUserTheEventCreator(creatorId, eventId)) {
            throw new AccessDeniedException("Only an event creator can requests the statistics of their events.");
        }

        List<Object[]> commentsData = this.eventRepository.getEventCommentData(eventId);

        List<EventComment> comments = commentsData.stream()
                .map(result -> {
                    String comment = (String) result[0];
                    UUID participantId = (UUID) result[1];
                    String participantName = (String) result[2];
                    byte[] profilePhoto = (byte[]) result[3];

                    ParticipantResume participantResume = new ParticipantResume(participantId, participantName, profilePhoto);
                    return new EventComment(comment, participantResume);
                }).toList();


        EventFeedbackStatistics statistics = this.eventRepository.getEventFeedbackStatistics(eventId)
                .orElse(new EventFeedbackStatistics());

        statistics.setComments(comments);

        return statistics;
    }

    @Override
    @Transactional
    public void cancelEvent(UUID eventId, String creatorEmail) {
        ParticipantEntity participant = this.participantRepository.findEnabledUserByEmail(creatorEmail).orElseThrow(
                () -> new EntityNotFoundException("User with email " + creatorEmail + " not found.")
        );

        EventEntity eventEntity = this.eventRepository.findEventByIdAndEventCreator(participant.getId(), eventId)
                .orElseThrow(
                        () -> new EntityNotFoundException("event not found")
                );

        eventEntity.setState(EventState.CANCELED);

        // TODO: When payments refund are implemented add logic to refund
        //  the inscriptions payment to the participants.

        ParticipantNotificationDto participantNotification = ParticipantNotificationDto
                .builder()
                .type(ParticipantNotificationType.EVENT_STATE_UPDATE)
                .impliedResourceId(eventEntity.getId())
                .title("Evento cancelado")
                .message(String.format("El evento de %s organizado para el dia %s fue cancelado",
                        eventEntity.getActivity().getName(), eventEntity.getDate()))
                .build();

        eventEntity.getEventParticipants().forEach(ep -> {
            if(!ep.isEventCreator()) {
                participantNotification.setParticipantEventCreated(ep.isEventCreator());

                this.participantNotificationService.sendNotification(participantNotification,
                        ep.getParticipant().getId());
            }
        });

        this.eventRepository.save(eventEntity);
    }

    @Override
    public EventFeeStatsDto getEventsFeeStatsByMonth(LocalDate dateFrom, LocalDate dateTo) {
        EventFeeStatsDto eventFeeStatsDto = new EventFeeStatsDto();
        eventFeeStatsDto.setDateFrom(dateFrom);
        eventFeeStatsDto.setDateTo(dateTo);
        eventFeeStatsDto
                .setPaidEventsCount(this.eventRepository.getPaidEventsCountBetweenDateFromAndDateTo(dateFrom, dateTo));
        eventFeeStatsDto
                .setUnpaidEventsCount(this.eventRepository.getUnPaidEventsCountBetweenDateFromAndDateTo(dateFrom, dateTo));

        return eventFeeStatsDto;
    }

    private EventResumePageDto fetchEventsWithPagination(
            UUID creatorId, UUID participantToExcludeId, UUID participantToIncludeId, EventState eventState, String activity,
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
                        eventState,
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
                        eventState,
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
                    .state(eventEntity.getState())
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

    private Map<LocalTime, Integer> countVotesForStartingHour(List<EventSchedulesEntity> eventSchedulesEntities,
                                                              List<EventParticipantEntity> eventParticipantEntities) {
        HashMap<LocalTime, Integer> computedVotes = new HashMap<>();

        for (EventSchedulesEntity eventSchedules : eventSchedulesEntities) {
            Time startingHour = eventSchedules.getSchedule().getStartingHour();
            computedVotes.put(startingHour.toLocalTime(), 0);

            Integer selectedTimes =
                    eventParticipantEntities
                            .stream()
                            .filter(ep -> ep.getScheduleVote() != null
                                    && ep.getScheduleVote().getSelectedHour().equals(startingHour))
                            .toList()
                            .size();

            computedVotes.put(startingHour.toLocalTime(), selectedTimes);
        }

        return computedVotes;
    }
}

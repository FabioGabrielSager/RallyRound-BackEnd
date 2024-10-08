package org.fs.rallyroundbackend.service.imps;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.client.BingMaps.BingMapApiClient;
import org.fs.rallyroundbackend.dto.auth.ParticipantFavoriteActivityDto;
import org.fs.rallyroundbackend.dto.location.places.PlaceDto;
import org.fs.rallyroundbackend.dto.participant.*;
import org.fs.rallyroundbackend.entity.events.ActivityEntity;
import org.fs.rallyroundbackend.entity.events.EventEntity;
import org.fs.rallyroundbackend.entity.events.EventParticipantEntity;
import org.fs.rallyroundbackend.entity.events.EventState;
import org.fs.rallyroundbackend.entity.users.participant.EventInscriptionEntity;
import org.fs.rallyroundbackend.entity.users.participant.EventInscriptionStatus;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantFavoriteActivityEntity;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantNotificationType;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantReputation;
import org.fs.rallyroundbackend.entity.users.participant.ReportEntity;
import org.fs.rallyroundbackend.exception.auth.AgeValidationException;
import org.fs.rallyroundbackend.exception.auth.IncorrectPasswordException;
import org.fs.rallyroundbackend.exception.event.inscriptions.EventStateException;
import org.fs.rallyroundbackend.exception.event.inscriptions.ParticipantNotInscribedException;
import org.fs.rallyroundbackend.exception.location.InvalidPlaceException;
import org.fs.rallyroundbackend.exception.report.ReportsLimitException;
import org.fs.rallyroundbackend.repository.ActivityRepository;
import org.fs.rallyroundbackend.repository.MPAuthTokenRepository;
import org.fs.rallyroundbackend.repository.chat.PrivateChatRepository;
import org.fs.rallyroundbackend.repository.event.EventInscriptionRepository;
import org.fs.rallyroundbackend.repository.event.EventParticipantRepository;
import org.fs.rallyroundbackend.repository.event.EventRepository;
import org.fs.rallyroundbackend.repository.user.participant.FavoriteActivityRepository;
import org.fs.rallyroundbackend.repository.user.participant.ParticipantRepository;
import org.fs.rallyroundbackend.repository.user.participant.ReportRepository;
import org.fs.rallyroundbackend.service.LocationService;
import org.fs.rallyroundbackend.service.ParticipantNotificationService;
import org.fs.rallyroundbackend.service.ParticipantService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.UUID;

/**
 * Implementation of ${@link ParticipantService} to manage participants.
 */
@Service
@RequiredArgsConstructor
public class ParticipantServiceImp implements ParticipantService {
    private final ModelMapper modelMapper;
    private final ParticipantRepository participantRepository;
    private final BingMapApiClient bingMapApiClient;
    private final LocationService locationService;
    private final ActivityRepository activityRepository;
    private final ReportRepository reportRepository;
    private final PrivateChatRepository privateChatRepository;
    private final MPAuthTokenRepository mpAuthTokenRepository;
    private final EventInscriptionRepository eventInscriptionRepository;
    private final FavoriteActivityRepository favoriteActivityRepository;
    private final PasswordEncoder passwordEncoder;
    private final EventParticipantRepository eventParticipantRepository;
    private final EventRepository eventRepository;
    private final ParticipantNotificationService participantNotificationService;

    @Override
    @Transactional
    public UserPublicDataDto getParticipantPublicData(UUID userId) {
        ParticipantEntity participantEntity =
                this.participantRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("Participant with id " + userId + " not found"));

        UserPublicDataDto result = this.modelMapper.map(participantEntity, UserPublicDataDto.class);

        result.setDeletedAccount(!participantEntity.isEnabled());

        if (participantEntity.getProfilePhoto() != null) {
            String participantEncodedProfilePhoto = Base64.getEncoder().encodeToString(participantEntity.getProfilePhoto());
            result.setProfilePhoto(participantEncodedProfilePhoto);
        }

        return result;
    }

    @Override
    @Transactional
    public ReportResponse registerParticipantReport(ReportRequest reportRequest, String reporterEmail) {
        ParticipantEntity reportedParticipant =
                this.participantRepository.findById(reportRequest.getReportedUserId())
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Participant with id " + reportRequest.getReportedUserId() + " not found"));

        ParticipantEntity reporter = this.participantRepository.findEnabledUserByEmail(reporterEmail)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Participant with email " + reporterEmail + " not found"));


        if (reportedParticipant.getReports() != null) {
            List<ReportEntity> reports = reportedParticipant
                    .getReports()
                    .stream()
                    .filter(r -> r.getReporterId().equals(reporter.getId()))
                    .toList();

            if (reports.size() > 2
                    || !reports.isEmpty()
                    && reports.stream().anyMatch(r -> r.isAsEventCreator() == reportRequest.isAsEventCreator())) {
                throw new ReportsLimitException("You have already reported this user.");
            }
        } else {
            reportedParticipant.setReports(new ArrayList<>());
        }


        ReportEntity reportEntity = ReportEntity.builder()
                .date(LocalDateTime.now())
                .number(reportedParticipant.getReports().isEmpty() ? 1 : reportedParticipant.getReports().size())
                .motive(reportRequest.getReportMotive())
                .description(reportRequest.getDescription())
                .reportedParticipant(reportedParticipant)
                .asEventCreator(reportRequest.isAsEventCreator())
                .reporterId(reporter.getId())
                .build();

        reportedParticipant.getReports().add(reportEntity);

        int reportedParticipantReportsCount = reportedParticipant.getReports().size();

        if (reportedParticipantReportsCount > 10 && reportedParticipantReportsCount < 20) {
            if (reportRequest.isAsEventCreator()) {
                reportedParticipant.setReputationAsEventCreator(ParticipantReputation.INTERMEDIATE);
            } else {
                reportedParticipant.setReputationAsParticipant(ParticipantReputation.INTERMEDIATE);
            }
        } else if (reportedParticipantReportsCount > 20) {
            if (reportRequest.isAsEventCreator()) {
                reportedParticipant.setReputationAsEventCreator(ParticipantReputation.BAD);
            } else {
                reportedParticipant.setReputationAsParticipant(ParticipantReputation.BAD);
            }
        }

        this.participantRepository.save(reportedParticipant);

        return ReportResponse.builder()
                .reportNumber(reportedParticipantReportsCount)
                .description(reportEntity.getDescription())
                .reportMotive(reportEntity.getMotive())
                .reportedUserId(reportedParticipant.getId())
                .asEventCreator(reportRequest.isAsEventCreator())
                .build();
    }

    @Override
    @Transactional
    public UserPersonalDataDto getPersonalData(String userEmail) {
        ParticipantEntity participant = this.participantRepository.findEnabledUserByEmail(userEmail)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Participant with email " + userEmail + " not found"));

        UserPersonalDataDto result = this.modelMapper.map(participant, UserPersonalDataDto.class);

        result.setDeletedAccount(false);

        if (participant.getProfilePhoto() != null) {
            String participantEncodedProfilePhoto = Base64.getEncoder().encodeToString(participant.getProfilePhoto());
            result.setProfilePhoto(participantEncodedProfilePhoto);
        }

        return result;
    }

    @Override
    @Transactional
    public UserPersonalDataDto modifyParticipantAccount(String userEmail, ParticipantAccountModificationRequest request,
                                                        MultipartFile profilePhoto) {
        ParticipantEntity participant = this.participantRepository.findEnabledUserByEmail(userEmail)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Participant with email " + userEmail + " not found"));

        if (request.getPlace() != null) {
            // Validating the place
            String bingMapQuery = request.getPlace().getAddress().getAddressLine() == null
                    ? request.getPlace().getAddress().getFormattedAddress()
                    : request.getPlace().getAddress().getAddressLine();

            PlaceDto[] bingMapApiAutosuggestionResponse =
                    this.bingMapApiClient.getAutosuggestionByPlace(bingMapQuery).block();

            Optional<PlaceDto> filteredPlace = Arrays.stream(Objects.requireNonNull(bingMapApiAutosuggestionResponse))
                    .filter(p -> p.equals(request.getPlace())).findFirst();

            if (filteredPlace.isEmpty()) {
                throw new InvalidPlaceException();
            }

            participant.setPlace(this.locationService.getPlaceEntityFromPlaceDto(request.getPlace()));
        }

        // If the user provides a profile photo, compress and set it to the ParticipantEntity.
        if (profilePhoto != null) {
            try {
                participant.setProfilePhoto(profilePhoto.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (request.getName() != null && !request.getName().isEmpty())
            participant.setName(request.getName());
        if (request.getLastName() != null && !request.getLastName().isEmpty())
            participant.setLastName(request.getLastName());
        if (request.getBirthdate() != null) {
            // Validate minimum age
            // Calculate the age based on the birthdate
            LocalDate currentDate = LocalDate.now();
            Period period = Period.between(request.getBirthdate(), currentDate);
            int age = period.getYears();
            if (age < 18) {
                throw new AgeValidationException("Person must be at least 18 years old.");
            }
            participant.setBirthdate(request.getBirthdate());
        }

        if (request.getFavoritesActivities() != null && request.getFavoritesActivities().length > 1) {
            // Map the new participant favorite activities.
            TreeSet<ParticipantFavoriteActivityEntity> participantFavoriteActivitiesEntities = new TreeSet<>();

            for (ParticipantFavoriteActivityDto fa : request.getFavoritesActivities()) {

                Optional<ActivityEntity> activityEntityOptional =
                        this.activityRepository.findByName(fa.getName());

                ParticipantFavoriteActivityEntity participantFavoriteActivitiesEntity =
                        ParticipantFavoriteActivityEntity.builder()
                                .participant(participant)
                                .favoriteOrder(fa.getOrder())
                                .build();

                if (activityEntityOptional.isEmpty()) {
                    ActivityEntity savedActivity =
                            this.activityRepository.save(ActivityEntity.builder().name(fa.getName()).build());

                    participantFavoriteActivitiesEntity.setActivity(savedActivity);
                } else {
                    participantFavoriteActivitiesEntity.setActivity(activityEntityOptional.get());
                }

                participantFavoriteActivitiesEntities.add(participantFavoriteActivitiesEntity);
            }

            // Set participant favorite activities
            participant.setFavoriteActivities(participantFavoriteActivitiesEntities);
        }

        this.participantRepository.save(participant);

        UserPersonalDataDto result = this.modelMapper.map(participant, UserPersonalDataDto.class);

        result.setDeletedAccount(false);

        if (participant.getProfilePhoto() != null) {
            String participantEncodedProfilePhoto = Base64.getEncoder().encodeToString(participant.getProfilePhoto());
            result.setProfilePhoto(participantEncodedProfilePhoto);
        }

        return result;
    }

    @Override
    @Transactional
    public void deleteParticipantAccount(String userEmail, String password) {
        ParticipantEntity participant = this.participantRepository.findEnabledUserByEmail(userEmail)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Participant with email " + userEmail + " not found"));

        if (!this.passwordEncoder.matches(password, participant.getPassword())) {
            throw new IncorrectPasswordException();
        }

        if (participant.getReports() != null && !participant.getReports().isEmpty()) {
            this.reportRepository.deleteAll(participant.getReports());
            participant.getReports().clear(); // Clear references
        }

        if (participant.getChats() != null && !participant.getChats().isEmpty()) {
            this.privateChatRepository.deleteAll(participant.getChats());
            participant.getChats().clear(); // Clear references
        }
        if (participant.getMpAuthToken() != null) {
            this.mpAuthTokenRepository.delete(participant.getMpAuthToken());
            participant.setMpAuthToken(null); // Prevent re-attaching deleted entity
        }

        if (participant.getFavoriteActivities() != null && !participant.getFavoriteActivities().isEmpty()) {
            this.favoriteActivityRepository.deleteAll(participant.getFavoriteActivities());
            participant.getFavoriteActivities().clear(); // Clear references
        }

        if (participant.getEventInscriptions() != null) {
            for (EventInscriptionEntity inscriptionEntity : participant.getEventInscriptions()) {
                if (inscriptionEntity.getStatus() != EventInscriptionStatus.ACCEPTED) {
                    this.eventInscriptionRepository.delete(inscriptionEntity);
                }
            }
        }

        participant.setPlace(null);
        participant.setProfilePhoto(null);
        participant.setLastName(null);
        participant.setEmail(null);
        participant.setBirthdate(null);
        participant.setPassword(null);
        participant.setEnabled(false);

        this.participantRepository.save(participant);
    }

    @Override
    @Transactional
    public void removeParticipantFromAnEvent(String participantEmail, UUID eventId) {
        ParticipantEntity participant = this.participantRepository.findEnabledUserByEmail(participantEmail)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Participant with email " + participantEmail + " not found"));

        EventEntity event = this.eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        if (event.getState() != EventState.READY_TO_START && event.getState() != EventState.WAITING_FOR_PARTICIPANTS) {
            throw new EventStateException("Business rule violation. You can't remove a participant from an event " +
                    "that is not in the state WAITING_FOR_PARTICIPANTS or READY_TO_START.");
        }

        EventInscriptionEntity eventInscription = participant.getEventInscriptions()
                .stream()
                .filter(ei -> ei.getEvent().getId().equals(eventId)
                        && ei.getStatus().equals(EventInscriptionStatus.ACCEPTED)).findFirst()
                .orElseThrow(ParticipantNotInscribedException::new);

        eventInscription.setStatus(EventInscriptionStatus.CANCELED_DUE_TO_ABANDONMENT);
        this.eventInscriptionRepository.save(eventInscription);

        EventParticipantEntity eventParticipant = this.eventParticipantRepository
                .findByParticipantIdAndEventId(participant.getId(), eventId)
                .orElseThrow(ParticipantNotInscribedException::new);

        event.getEventParticipants().remove(eventParticipant);
        this.eventParticipantRepository.delete(eventParticipant);

        this.participantNotificationService.removeEventsNotifications(eventId, participant.getId());

        if (event.getState() == EventState.READY_TO_START) {
            event.setState(EventState.WAITING_FOR_PARTICIPANTS);
            this.eventRepository.save(event);
        }

        ParticipantNotificationDto participantNotification = ParticipantNotificationDto
                .builder()
                .type(ParticipantNotificationType.EVENT_STATE_UPDATE)
                .impliedResourceId(event.getId())
                .title("Abandono de partcipante.")
                .message(String.format("Un participante ha abandonado el evento de %s organizado para el dia %s.",
                        event.getActivity().getName(), event.getDate()))
                .build();

        event.getEventParticipants().forEach(ep -> {
            if (!ep.getParticipant().getId().equals(participant.getId())) {
                participantNotification.setParticipantEventCreated(ep.isEventCreator());

                this.participantNotificationService.sendNotification(participantNotification,
                        ep.getParticipant().getId());
            }
        });
    }

    @Override
    public SearchedParticipantResult searchParticipant(String requesterEmail, String query, Integer page, Integer limit) {

        Pageable pageable = PageRequest.of(page == null ? 0 : page - 1, limit == null ? 5 : limit);

        List<ParticipantEntity> matchesPage = this.participantRepository
                .findByNameOrLastName(query, query, requesterEmail,
                        pageable);
        int totalMatchesCount = this.participantRepository
                .countALlByNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndEmailNot(query, query,
                        requesterEmail);

        return SearchedParticipantResult
                .builder()
                .matches(List.of(this.modelMapper.map(matchesPage, ParticipantSummary[].class)))
                .totalMatches(totalMatchesCount)
                .limit(limit == null ? 5 : limit)
                .page(page == null ? 0 : page)
                .build();
    }

    @Override
    @Transactional
    public void inviteUserToEvent(UUID eventId, UUID userId, String eventCreatorEmail) {
        ParticipantEntity participant = this.participantRepository.findEnabledUserByEmail(eventCreatorEmail)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Participant with email " + eventCreatorEmail + " not found"));

        this.eventRepository.findEventByIdAndEventCreator(participant.getId(), eventId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("The event creator with email %s doesn't have an event with id %s"
                                , eventCreatorEmail, eventId)));


        ParticipantNotificationDto invitation = ParticipantNotificationDto.builder()
                .type(ParticipantNotificationType.EVENT_INVITATION)
                .impliedResourceId(eventId)
                .title("Invitación a evento")
                .isParticipantEventCreated(false)
                .message(String.format("El usuario %s, %s te ha invitado a un evento",
                        participant.getLastName(),
                        participant.getName()))
                .build();

        this.participantNotificationService.sendEventInvitation(invitation, userId);
    }

    @Override
    public TopEventCreatorsResponse getEventCreatorsTop(short topSize, Byte month) {
        byte validMonth = (byte) LocalDate.now().getMonth().getValue();

        if (month != null && month < 13 && month > 0) {
            validMonth = month;
        }

        List<Object[]> creatorsTopData = this.participantRepository.getTopEventCreators(topSize, validMonth);

        TopEventCreatorsResponse top = new TopEventCreatorsResponse(topSize, new ArrayList<>());

        top.setEventCreators(
                creatorsTopData.stream().map(
                        result -> {
                            ParticipantSummary creator =
                                    new ParticipantSummary((UUID) result[0], (String) result[1], (byte[]) result[3]);
                            return new TopEventCreatorResponse(creator, (long) result[4]);
                        }
                ).toList()
        );

        return top;
    }

    @Override
    public ReportedParticipantsPage getReportedParticipants(Integer limit, Integer page) {
        int validLimit = 10;
        int validPage = 1;

        if (limit != null) {
            validLimit = limit;
        }

        if (page != null) {
            validPage = page;
        }

        Long reportedParticipantsCount = this.participantRepository.countReportedParticipants();

        ReportedParticipantsPage reportedParticipantsPage = new ReportedParticipantsPage();
        reportedParticipantsPage.setPage(validPage);
        reportedParticipantsPage.setPageSize(validLimit);
        reportedParticipantsPage.setTotalElements(reportedParticipantsCount != null ? reportedParticipantsCount : 0);
        reportedParticipantsPage.setReportedParticipants(new ArrayList<>());

        // TODO: Maybe encapsulate this mapping logic in the repository it's a better idea
        List<Object[]> reportedParticipantsQueryResult =
                this.participantRepository.getReportedParticipants(validLimit, validLimit * (validPage - 1));

        for (Object[] reportedParticipantFromQueryResult : reportedParticipantsQueryResult) {
            ParticipantReportsCount reportedParticipant = ParticipantReportsCount.builder()
                    .asParticipantReportsCount(reportedParticipantFromQueryResult[0] != null
                            ? (long) reportedParticipantFromQueryResult[0] : 0)
                    .asEventCreatorReportsCount(reportedParticipantFromQueryResult[1] != null
                            ? (long) reportedParticipantFromQueryResult[1] : 0)
                    .participant(
                            new ParticipantSummary(
                                    (UUID) reportedParticipantFromQueryResult[2],
                                    (String) reportedParticipantFromQueryResult[3],
                                    (byte[]) reportedParticipantFromQueryResult[4]
                            )
                    )
                    .build();

            reportedParticipantsPage.getReportedParticipants().add(reportedParticipant);
        }

        return reportedParticipantsPage;
    }

    @Override
    public ParticipantReportsPage getParticipantReports(UUID participantId, Integer limit, Integer page) {
        int validLimit = 10;
        int validPage = 1;

        if (limit != null) {
            validLimit = limit;
        }

        if (page != null) {
            validPage = page;
        }

        ParticipantReportsPage reportsPage = new ParticipantReportsPage();
        reportsPage.setPage(validPage);
        reportsPage.setPageSize(validLimit);

        Long reportsCount = this.reportRepository.countAllByParticipantId(participantId);

        reportsPage.setTotalElements(reportsCount != null ? reportsCount : 0);

        List<ReportEntity> participantReportsEntities = this.reportRepository
                .findAllByParticipantId(participantId, validLimit, validLimit * (validPage - 1));

        reportsPage.setReports(participantReportsEntities.stream().map(
                        re -> {
                            ReportResponse reportResponse = new ReportResponse();
                            reportResponse.setReportedUserId(re.getReportedParticipant().getId());
                            reportResponse.setReportMotive(re.getMotive());
                            reportResponse.setDescription(re.getDescription());
                            reportResponse.setAsEventCreator(re.isAsEventCreator());
                            reportResponse.setReportNumber(re.getNumber());
                            reportResponse.setReportDateTime(re.getDate());
                            reportResponse.setId(re.getId());
                            return reportResponse;
                        }
                ).toList()
        );

        return reportsPage;
    }

    @Override
    public void deleteParticipantReport(UUID reportId) {
        if(!this.reportRepository.existsById(reportId)) {
            throw new EntityNotFoundException("Report not found.");
        }

        this.reportRepository.deleteById(reportId);
    }

    @Override
    public ReportsByMotiveAndMonthAndYear getReportsCountByMotiveAndMonthAndYear(Integer year, Integer month) {
        int validYear = year == null ? LocalDate.now().getYear() : year;
        int validMonth = LocalDate.now().getMonth().getValue();

        if (month != null && month < 13 && month > 0) {
            validMonth = month;
        }

        ReportsByMotiveAndMonthAndYear reports =
                this.reportRepository.getReportsByMotiveAndMonthAndYear(validYear, validMonth);
        reports.setMonth(validMonth);
        reports.setYear(validYear);
        return reports;
    }
}

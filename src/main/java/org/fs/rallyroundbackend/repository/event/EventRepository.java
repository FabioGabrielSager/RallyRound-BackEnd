package org.fs.rallyroundbackend.repository.event;

import org.fs.rallyroundbackend.dto.event.EventFeeStatsDto;
import org.fs.rallyroundbackend.dto.event.EventsCountSummary;
import org.fs.rallyroundbackend.dto.event.feedback.EventFeedbackStatistics;
import org.fs.rallyroundbackend.entity.events.EventEntity;
import org.fs.rallyroundbackend.entity.events.EventState;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, UUID> {
    @Query("SELECT e FROM EventEntity AS e " +
            "JOIN e.eventParticipants as ep " +
            "JOIN ep.participant as p " +
            "JOIN e.activity AS act " +
            "JOIN e.address AS adds JOIN adds.street AS str JOIN str.neighborhood AS neigh " +
            "JOIN neigh.locality as loc JOIN loc.adminSubdistrict as adminsd JOIN adminsd.adminDistrict AS admind " +
            "JOIN e.eventSchedules AS es JOIN es.schedule as s " +
            "WHERE" +
            "(:creatorId IS NULL OR ep.isEventCreator AND p.id = :creatorId) " +
            "AND (:excludedParticipantId IS NULL OR NOT EXISTS " +
            "   (SELECT 1 FROM EventParticipantEntity ep2 WHERE ep2.event = e " +
            "       AND ep2.participant.id = :excludedParticipantId) AND NOT EXISTS " +
            "   (SELECT 1 FROM EventInscriptionEntity ei WHERE ei.event = e " +
            "       AND ei.participant.id = :excludedParticipantId " +
            "       AND ei.status != 'CANCELED' AND ei.status != 'REJECTED')) " +
            "AND (:participantId IS NULL OR EXISTS (select 1 FROM EventInscriptionEntity ei WHERE ei.event = e " +
            "       AND ei.participant.id = :participantId " +
            "       AND ei.status != 'CANCELED' AND ei.status != 'REJECTED'))  " +
            "AND (:eventState IS NULL OR e.state = :eventState) " +
            "AND (:activityName IS NULL OR act.name LIKE :activityName) " +
            "AND (:neighborhood IS NULL OR neigh.name LIKE :neighborhood)" +
            "AND (:locality IS NULL OR loc.name LIKE :locality)" +
            "AND (:adminSubdistrict IS NULL OR adminsd.name LIKE :adminSubdistrict) " +
            "AND (:adminDistrict IS NULL OR admind.name LIKE :adminDistrict) " +
            "AND (e.date BETWEEN :dateFrom AND :dateTo) " +
            "AND (:hours IS NULL OR s.startingHour in :hours) " +
            "AND e.id IN (SELECT ae.id from EventEntity as ae ORDER BY ae.id LIMIT :limit OFFSET :offset) " +
            "ORDER BY e.date DESC")
    List<EventEntity> findAll(
            @Param("creatorId") UUID creatorId,
            @Param("excludedParticipantId") UUID excludedParticipantId,
            @Param("participantId") UUID participantId,
            @Param("eventState") EventState eventState,
            @Param("activityName") String activityName,
            @Param("neighborhood") String neighborhood,
            @Param("locality") String locality,
            @Param("adminSubdistrict") String adminSubdistrict,
            @Param("adminDistrict") String adminDistrict,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("hours") List<Time> hours,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query("SELECT COUNT(DISTINCT e) FROM EventEntity AS e " +
            "JOIN e.eventParticipants as ep " +
            "JOIN ep.participant as p " +
            "JOIN e.activity AS act " +
            "JOIN e.address AS adds JOIN adds.street AS str JOIN str.neighborhood AS neigh " +
            "JOIN neigh.locality as loc JOIN loc.adminSubdistrict as adminsd JOIN adminsd.adminDistrict AS admind " +
            "JOIN e.eventSchedules AS es JOIN es.schedule as s " +
            "WHERE " +
            "(:creatorId IS NULL OR ep.isEventCreator AND p.id = :creatorId) " +
            "AND (:excludedParticipantId IS NULL OR NOT EXISTS " +
            "   (SELECT 1 FROM EventParticipantEntity ep2 WHERE ep2.event = e " +
            "       AND ep2.participant.id = :excludedParticipantId) AND NOT EXISTS " +
            "   (SELECT 1 FROM EventInscriptionEntity ei WHERE ei.event = e " +
            "       AND ei.participant.id = :excludedParticipantId " +
            "       AND ei.status != 'CANCELED' AND ei.status != 'REJECTED')) " +
            "AND (:activityName IS NULL OR act.name LIKE :activityName) " +
            "AND (:participantId IS NULL OR EXISTS (select 1 FROM EventInscriptionEntity ei WHERE ei.event = e " +
            "       AND ei.participant.id = :participantId" +
            "       AND ei.status != 'CANCELED' AND ei.status != 'REJECTED'))  " +
            "AND (:eventState IS NULL OR e.state = :eventState) " +
            "AND (:neighborhood IS NULL OR neigh.name LIKE :neighborhood)" +
            "AND (:locality IS NULL OR loc.name LIKE :locality)" +
            "AND (:adminSubdistrict IS NULL OR adminsd.name LIKE :adminSubdistrict) " +
            "AND (:adminDistrict IS NULL OR admind.name LIKE :adminDistrict) " +
            "AND (e.date BETWEEN :dateFrom AND :dateTo) " +
            "AND (:hours IS NULL OR s.startingHour in :hours)")
    Long countAll(
            @Param("creatorId") UUID creatorId,
            @Param("excludedParticipantId") UUID excludedParticipantId,
            @Param("participantId") UUID participantId,
            @Param("eventState") EventState eventState,
            @Param("activityName") String activityName,
            @Param("neighborhood") String neighborhood,
            @Param("locality") String locality,
            @Param("adminSubdistrict") String adminSubdistrict,
            @Param("adminDistrict") String adminDistrict,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("hours") List<Time> hours
    );
    List<EventEntity> findAllByStateInAndNextStateTransitionBefore(EventState[] state, LocalDateTime nextStateTransition);

    @Query("SELECT e FROM EventEntity AS e " +
            "JOIN EventParticipantEntity AS ep ON e=ep.event " +
            "JOIN ParticipantEntity AS p ON p=ep.participant " +
            "WHERE e.id = :eventId AND ep.isEventCreator=true AND p.id = :eventCreatorId")
    Optional<EventEntity> findEventByIdAndEventCreator(UUID eventCreatorId, UUID eventId);

    @Query(
            "SELECT new org.fs.rallyroundbackend.dto.event.feedback.EventFeedbackStatistics(" +
                    ":eventId, " +
                    "COUNT(ef), " +
                    "COALESCE(AVG(ef.overallSatisfaction), 0), " +
                    "COALESCE(AVG(ef.organizationRating), 0), " +
                    "COALESCE(AVG(ef.contentQualityRating), 0), " +
                    "COALESCE(AVG(ef.venueRating), 0), " +
                    "COALESCE(AVG(ef.coordinatorsRating), 0), " +
                    "COALESCE(AVG(ef.valueForMoneyRating), 0)) " +
                    "FROM EventEntity e " +
                    "JOIN EventParticipantEntity ep ON e.id = ep.event.id " +
                    "JOIN EventFeedbackEntity ef ON ep.feedback.id = ef.id " +
                    "WHERE e.id = :eventId"
    )
    Optional<EventFeedbackStatistics> getEventFeedbackStatistics(UUID eventId);

    @Query(
            "SELECT ef.comments, ep.participant.id, ep.participant.name, ep.participant.profilePhoto " +
                    "FROM EventEntity e " +
                    "JOIN EventParticipantEntity ep ON e.id = ep.event.id " +
                    "JOIN EventFeedbackEntity ef ON ep.feedback.id = ef.id " +
                    "WHERE e.id = :eventId"
    )
    List<Object[]> getEventCommentData(UUID eventId);

    // TODO: Find out why this query works the opposite way it should work
    /**
     * Yes, the case in this query is the opposite of it should be, but it works fine, i don't understand why.
     * The original query is:
     * SELECT case when count(event_id) > 0 then true else false end FROM events e
     *     join events_participants ep on e.id=ep.event_id
     *     join participants p on ep.participant_id=p.id
     *     WHERE event_id='event_id
     *       AND p.id='event_id'
     *       AND ep.is_event_creator IS TRUE
     * */
    @Query(
            "SELECT CASE WHEN COUNT(e) > 0 THEN FALSE ELSE TRUE END " +
                    "FROM EventEntity e JOIN EventParticipantEntity as ep ON e=ep.event " +
                    "JOIN ParticipantEntity p ON ep.participant=p " +
                    "WHERE e.id=:eventId AND ep.isEventCreator IS TRUE AND p.id=:userId"
    )
    boolean isUserTheEventCreator(UUID userId, UUID eventId);

    @Query(
            "SELECT new org.fs.rallyroundbackend.dto.event.EventsCountSummary(" +
                    "COUNT(DISTINCT e), " +
                    "SUM(CASE WHEN e.state = 'FINALIZED' THEN 1 ELSE 0 END), " +
                    "SUM(CASE WHEN e.state = 'CANCELED' THEN 1 ELSE 0 END), " +
                    "SUM(CASE WHEN e.state != 'FINALIZED' AND e.state != 'CANCELED' THEN 1 ELSE 0 END))" +
                    "FROM EventEntity e " +
                    "WHERE e.date BETWEEN :dateFrom AND :dateTo AND e.inscriptionPrice > 0"
    )
    EventsCountSummary getPaidEventsCountBetweenDateFromAndDateTo(LocalDate dateFrom, LocalDate dateTo);

    @Query(
            "SELECT new org.fs.rallyroundbackend.dto.event.EventsCountSummary(" +
                    "COUNT(DISTINCT e), " +
                    "SUM(CASE WHEN e.state = 'FINALIZED' THEN 1 ELSE 0 END), " +
                    "SUM(CASE WHEN e.state = 'CANCELED' THEN 1 ELSE 0 END), " +
                    "SUM(CASE WHEN e.state != 'FINALIZED' AND e.state != 'CANCELED' THEN 1 ELSE 0 END))" +
                    "FROM EventEntity e " +
                    "WHERE e.date BETWEEN :dateFrom AND :dateTo AND e.inscriptionPrice = 0"
    )
    EventsCountSummary getUnPaidEventsCountBetweenDateFromAndDateTo(LocalDate dateFrom, LocalDate dateTo);
}

package org.fs.rallyroundbackend.repository.event;

import org.fs.rallyroundbackend.entity.events.EventEntity;
import org.fs.rallyroundbackend.entity.events.EventState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
            "       AND ei.participant.id = :excludedParticipantId)) " +
            "AND :participantId IS NULL OR EXISTS (select 1 FROM EventInscriptionEntity ei WHERE ei.event = e " +
            "       AND ei.participant.id = :participantId)  " +
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
            "       AND ei.participant.id = :excludedParticipantId)) " +
            "AND (:activityName IS NULL OR act.name LIKE :activityName) " +
            "AND :participantId IS NULL OR EXISTS (select 1 FROM EventInscriptionEntity ei WHERE ei.event = e " +
            "       AND ei.participant.id = :participantId)  " +
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
            @Param("activityName") String activityName,
            @Param("neighborhood") String neighborhood,
            @Param("locality") String locality,
            @Param("adminSubdistrict") String adminSubdistrict,
            @Param("adminDistrict") String adminDistrict,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("hours") List<Time> hours
    );

    List<EventEntity> findAllByStateInAndNextStateTransitionBefore(EventState[] states, LocalDateTime dateTime);
}

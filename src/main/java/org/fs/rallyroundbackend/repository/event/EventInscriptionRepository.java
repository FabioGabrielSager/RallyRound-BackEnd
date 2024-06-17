package org.fs.rallyroundbackend.repository.event;

import org.fs.rallyroundbackend.entity.users.participant.EventInscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventInscriptionRepository extends JpaRepository<EventInscriptionEntity, UUID> {
    @Query("SELECT ei FROM EventInscriptionEntity as ei " +
            "WHERE ei.participant.id = :participantId AND ei.event.id = :eventId " +
            "AND ei.status != 'CANCELED' AND ei.status != 'REJECTED' AND ei.status != 'CANCELED_DUE_TO_ABANDONMENT'")
    Optional<EventInscriptionEntity> findByParticipantIdAndEvent(
            @Param("participantId") UUID participantId,
            @Param("eventId") UUID eventId
    );
}

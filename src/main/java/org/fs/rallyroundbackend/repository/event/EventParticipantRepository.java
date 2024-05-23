package org.fs.rallyroundbackend.repository.event;

import org.fs.rallyroundbackend.entity.events.EventParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EventParticipantRepository extends JpaRepository<EventParticipantEntity, UUID> {
    Optional<EventParticipantEntity> findByParticipantIdAndEventId(UUID participantId, UUID eventId);
}

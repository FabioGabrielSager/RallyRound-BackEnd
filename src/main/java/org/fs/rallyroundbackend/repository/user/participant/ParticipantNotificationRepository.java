package org.fs.rallyroundbackend.repository.user.participant;

import org.fs.rallyroundbackend.entity.users.participant.ParticipantNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParticipantNotificationRepository extends JpaRepository<ParticipantNotificationEntity, UUID> {
    @Query("SELECT n FROM ParticipantNotificationEntity AS n " +
            "JOIN ParticipantEntity AS p ON n.participant=p " +
            "WHERE p.email = :participantEmail AND n.viewed = false " +
            "ORDER BY n.timestamp DESC")
    List<ParticipantNotificationEntity> findNotViewedParticipantNotifications(String participantEmail);

    @Query("SELECT n FROM ParticipantNotificationEntity AS n " +
            "JOIN ParticipantEntity AS p ON n.participant=p " +
            "WHERE p.email = :participantEmail AND n.id = :notificationId")
    Optional<ParticipantNotificationEntity> findParticipantNotification(UUID notificationId, String participantEmail);
}

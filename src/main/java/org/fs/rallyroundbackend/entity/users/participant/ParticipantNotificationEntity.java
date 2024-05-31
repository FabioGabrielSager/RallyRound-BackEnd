package org.fs.rallyroundbackend.entity.users.participant;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "participant_notifications")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ParticipantNotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private ParticipantNotificationType type;

    private boolean viewed;

    private UUID impliedResourceId;

    private String title;

    private String message;

    private LocalDateTime timestamp;

    private boolean isParticipantEventCreated;

    @ManyToOne
    private ParticipantEntity participant;
}

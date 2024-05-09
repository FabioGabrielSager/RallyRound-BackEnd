package org.fs.rallyroundbackend.entity.users.participant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.entity.events.EventEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
@Entity
@Table(name = "event_inscription")
public class EventInscriptionEntity {
    @Id
    private UUID id;

    @ManyToOne
    private EventEntity event;

    @ManyToOne
    private ParticipantEntity participant;

    @Column(name = "payment_link")
    private String paymentLink;

    @Enumerated(EnumType.STRING)
    private EventInscriptionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private MPPaymentStatus paymentStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}


package org.fs.rallyroundbackend.entity.users.participant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.entity.events.EventEntity;
import org.fs.rallyroundbackend.entity.mercadopago.MPPaymentEntity;
import org.fs.rallyroundbackend.entity.mercadopago.MPPaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
@Entity
@Table(name = "event_inscriptions")
public class EventInscriptionEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private EventEntity event;

    @ManyToOne(fetch = FetchType.LAZY)
    private ParticipantEntity participant;

    @Column(name = "payment_link")
    private String paymentLink;

    @Enumerated(EnumType.STRING)
    private EventInscriptionStatus status;

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private MPPaymentEntity payment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}


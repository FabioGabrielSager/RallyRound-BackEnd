package org.fs.rallyroundbackend.entity.mercadopago;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "mp_payment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MPPaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private MPPaymentStatus status;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "appoved_at")
    private OffsetDateTime approvedAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "money_release_at")
    private OffsetDateTime moneyReleaseAt;

    @Column(name = "currency_id")
    private String currencyId;
}

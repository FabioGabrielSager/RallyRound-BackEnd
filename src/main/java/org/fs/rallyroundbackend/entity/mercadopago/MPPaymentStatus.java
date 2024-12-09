package org.fs.rallyroundbackend.entity.mercadopago;

public enum MPPaymentStatus {
    pending,
    approved,
    authorized,
    in_process,
    in_mediation,
    rejected,
    cancelled,
    refunded,
    charged_back
}

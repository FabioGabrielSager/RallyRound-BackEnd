package org.fs.rallyroundbackend.dto.event.inscription;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.entity.users.participant.EventInscriptionStatus;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class CreatedEventInscriptionResultDto extends EventInscriptionResultDto {
    private boolean requiresPayment;
    private String paymentLink;

    public CreatedEventInscriptionResultDto(boolean requiresPayment, String paymentLink, UUID eventId,
                                            EventInscriptionStatus eventInscriptionStatus) {
        super(eventId, eventInscriptionStatus);
        this.requiresPayment = requiresPayment;
        this.paymentLink = paymentLink;
    }
}

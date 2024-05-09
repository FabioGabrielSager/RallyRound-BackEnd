package org.fs.rallyroundbackend.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.entity.users.participant.EventInscriptionStatus;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EventWithInscriptionStatusDto extends EventWithCreatorReputationDto {
    private EventInscriptionStatus eventInscriptionStatus;
}

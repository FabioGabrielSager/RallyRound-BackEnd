package org.fs.rallyroundbackend.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantReputation;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EventWithCreatorReputationDto extends CreatedEventDto {
    protected ParticipantReputation eventCreatorReputation;
}

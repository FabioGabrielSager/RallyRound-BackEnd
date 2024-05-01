package org.fs.rallyroundbackend.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.dto.participant.EventCreatorResume;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantReputation;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EventCompleteWithCreatorReputationDto extends EventCompleteDto {
    private ParticipantReputation eventCreatorReputation;
}

package org.fs.rallyroundbackend.dto.participant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantReputation;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventCreatorResume extends ParticipantResume {
    private ParticipantReputation reputationAsEventCreator;
}

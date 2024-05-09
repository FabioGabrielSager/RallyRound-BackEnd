package org.fs.rallyroundbackend.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.dto.participant.ParticipantResume;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventParticipantDto {
    private ParticipantResume participant;
    private boolean isEventCreator;
}

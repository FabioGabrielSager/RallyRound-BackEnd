package org.fs.rallyroundbackend.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.dto.participant.ParticipantSummary;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventParticipantDto {
    private ParticipantSummary participant;
    private boolean isEventCreator;
}

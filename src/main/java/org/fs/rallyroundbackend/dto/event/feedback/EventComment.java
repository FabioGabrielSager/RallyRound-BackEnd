package org.fs.rallyroundbackend.dto.event.feedback;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.dto.participant.ParticipantSummary;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventComment {
    private String comment;
    private ParticipantSummary participantResume;
}

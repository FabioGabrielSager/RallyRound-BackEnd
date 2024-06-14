package org.fs.rallyroundbackend.dto.event.feedback;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.dto.participant.ParticipantResume;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventComment {
    private String comment;
    private ParticipantResume participantResume;
}

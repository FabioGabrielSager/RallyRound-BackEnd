package org.fs.rallyroundbackend.dto.participant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ParticipantReportsCount {
    private long asParticipantReportsCount;
    private long asEventCreatorReportsCount;
    private ParticipantSummary participant;
}

package org.fs.rallyroundbackend.dto.participant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.dto.PagedResponse;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReportedParticipantsPage extends PagedResponse {
    private List<ParticipantReportsCount> reportedParticipants;
}

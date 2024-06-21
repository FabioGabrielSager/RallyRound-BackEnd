package org.fs.rallyroundbackend.dto.participant;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonPropertyOrder({"totalMatches", "limit", "page", "matches"})
public class SearchedParticipantResult {
    private List<ParticipantSummary> matches;
    private int totalMatches;
    private int page;
    private int limit;
}

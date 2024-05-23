package org.fs.rallyroundbackend.dto.event;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventFeedbackRequest {
    @NotNull
    private UUID eventId;
    @Min(1) @Max(5)
    private byte overallSatisfaction; // e.g., 1 to 5 stars
    private String comments;
    @Min(1) @Max(5)
    private byte organizationRating; // e.g., 1 to 5 stars
    @Min(1) @Max(5)
    private byte contentQualityRating; // e.g., 1 to 5 stars
    @Min(1) @Max(5)
    private byte venueRating; // e.g., 1 to 5 stars
    @Min(1) @Max(5)
    private byte coordinatorsRating; // e.g., 1 to 5 stars
    @Min(1) @Max(5)
    private byte valueForMoneyRating; // e.g., 1 to 5 stars
}

package org.fs.rallyroundbackend.dto.event.feedback;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class EventFeedbackStatistics {
    private UUID eventId;
    private long feedbackCount;
    private double overallSatisfaction;
    private double organizationRating;
    private double contentQualityRating;
    private double venueRating;
    private double coordinatorsRating;
    private double valueForMoneyRating;
    private List<EventComment> comments;

    public EventFeedbackStatistics(UUID eventId, long feedbackCount, double overallSatisfaction,
                                   double organizationRating, double contentQualityRating, double venueRating,
                                   double coordinatorsRating, double valueForMoneyRating) {
        this.eventId = eventId;
        this.feedbackCount = feedbackCount;
        this.overallSatisfaction = overallSatisfaction;
        this.organizationRating = organizationRating;
        this.contentQualityRating = contentQualityRating;
        this.venueRating = venueRating;
        this.coordinatorsRating = coordinatorsRating;
        this.valueForMoneyRating = valueForMoneyRating;
    }
}

package org.fs.rallyroundbackend.entity.events;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "events_feedback")
@AllArgsConstructor
@NoArgsConstructor
@Setter @Getter
public class EventFeedbackEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "overall_satisfaction")
    private byte overallSatisfaction; // e.g., 1 to 5 stars
    private String comments;
    @Column(name = "organization_rating")
    private byte organizationRating; // e.g., 1 to 5 stars
    @Column(name = "content_quality_rating")
    private byte contentQualityRating; // e.g., 1 to 5 stars
    @Column(name = "venue_rating")
    private byte venueRating; // e.g., 1 to 5 stars
    @Column(name = "coordinators_rating")
    private byte coordinatorsRating; // e.g., 1 to 5 stars
    @Column(name = "value_for_money_rating")
    private byte valueForMoneyRating;

    @OneToOne
    private EventParticipantEntity eventParticipant;
}

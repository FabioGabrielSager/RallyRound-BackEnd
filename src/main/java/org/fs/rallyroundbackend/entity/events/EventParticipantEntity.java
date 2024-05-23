package org.fs.rallyroundbackend.entity.events;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.UUID;

@Entity
@Table(name = "events_participants")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EventParticipantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private ParticipantEntity participant;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private EventEntity event;

    @Column(name = "is_event_creator", nullable = false)
    private boolean isEventCreator;

    @OneToOne(mappedBy = "eventParticipant")
    @Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
    private ScheduleVoteEntity scheduleVote;

    @OneToOne
    @Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
    private EventFeedbackEntity feedback;
}

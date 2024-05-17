package org.fs.rallyroundbackend.entity.events;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.entity.chats.EventChatEntity;
import org.fs.rallyroundbackend.entity.location.AddressEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "events")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String description;

    @Column(nullable = false)
    private double duration;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration_unit", nullable = false)
    private DurationUnit durationUnit;

    @Column(name = "inscription_price", nullable = false)
    private BigDecimal inscriptionPrice;

    @Column(name = "participants_limit", nullable = false)
    private int participantsLimit;

    @Column(name = "is_event_creator_participant", nullable = false)
    private boolean isEventCreatorParticipant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventState state;

    @Column(name = "next_state_transition")
    private LocalDateTime nextStateTransition;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    @Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
    private ActivityEntity activity;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    @OneToMany(mappedBy = "event")
    @Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
    private List<EventSchedulesEntity> eventSchedules;

    @OneToMany(mappedBy = "event")
    @Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
    private List<EventParticipantEntity> eventParticipants;

    @OneToOne(mappedBy = "event")
    @Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
    private EventChatEntity chat;
}

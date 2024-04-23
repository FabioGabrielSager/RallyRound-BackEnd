package org.fs.rallyroundbackend.entity.events;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.entity.chats.ChatEntity;
import org.fs.rallyroundbackend.entity.location.AddressEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Column(name = "house_number", nullable = false)
    private String houseNumber;

    @Column(nullable = false)
    private int duration;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration_unit", nullable = false)
    private DurationUnit durationUnit;

    @Column(name = "inscription_price", nullable = false)
    private BigDecimal inscriptionPrice;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    @Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
    private ActivityEntity activity;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private AddressEntity addressEntity;

    @OneToMany(mappedBy = "event")
    private List<EventSchedulesEntity> eventSchedules;

    @OneToMany(mappedBy = "event")
    private List<EventParticipantEntity> eventParticipants;

    @OneToOne(mappedBy = "event")
    private ChatEntity chat;
}

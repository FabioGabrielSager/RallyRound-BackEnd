package org.fs.rallyroundbackend.entity.events;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.entity.chats.ChatEntity;
import org.fs.rallyroundbackend.entity.location.AddressEntity;

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
    private int houseNumber;

    @ManyToOne
    @JoinColumn(name = "activity_id")
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

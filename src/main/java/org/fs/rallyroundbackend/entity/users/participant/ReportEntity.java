package org.fs.rallyroundbackend.entity.users.participant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "reports")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String description;

    @Enumerated(EnumType.STRING)
    private ReportMotive motive;

    @Column(name = "as_event_creator")
    private boolean asEventCreator;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private ParticipantEntity reportedParticipant;

    private UUID reporterId;
}

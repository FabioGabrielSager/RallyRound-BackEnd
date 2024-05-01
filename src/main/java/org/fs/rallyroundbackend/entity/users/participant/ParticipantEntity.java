package org.fs.rallyroundbackend.entity.users.participant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.fs.rallyroundbackend.entity.chats.ChatEntity;
import org.fs.rallyroundbackend.entity.chats.ChatMessageEntity;
import org.fs.rallyroundbackend.entity.events.EventParticipantEntity;
import org.fs.rallyroundbackend.entity.location.PlaceEntity;
import org.fs.rallyroundbackend.entity.users.UserEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "participants")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ParticipantEntity extends UserEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "reputation_as_participant", nullable = false)
    private ParticipantReputation reputationAsParticipant = ParticipantReputation.BUENA;

    @Enumerated(EnumType.STRING)
    @Column(name = "reputation_as_event_creator", nullable = false)
    private ParticipantReputation reputationAsEventCreator = ParticipantReputation.BUENA;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private PlaceEntity place;

    @OneToOne
    private MPAuthTokenEntity mpAuthToken;

    @OneToMany
    private List<ReportEntity> reports;

    @Column(name = "profile_photo", columnDefinition = "bytea")
    private byte[] profilePhoto;

    @OneToMany(mappedBy = "participant")
    private List<EventParticipantEntity> eventParticipants;

    @ManyToMany
    private List<ChatEntity> chats;

    @OneToMany(mappedBy = "sender")
    private List<ChatMessageEntity> sentMessages;

    @OneToMany(mappedBy = "participant")
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    private Set<ParticipantFavoriteActivitiesEntity> favoriteActivities;
}

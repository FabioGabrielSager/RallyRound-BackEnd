package org.fs.rallyroundbackend.entity.users;

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

import java.util.List;

@Entity
@Table(name = "participants")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ParticipantEntity extends UserEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipantReputation reputation = ParticipantReputation.BUENA;

    @OneToOne
    @JoinColumn(name = "place_id")
    protected PlaceEntity place;

    @OneToOne
    private MPAuthTokenEntity mpAuthToken;

    @OneToMany
    private List<ReportEntity> reports;

    @Lob
    @Column(name = "profile_photo", columnDefinition = "bytea")
    private byte[] profilePhoto;

    @OneToMany(mappedBy = "participant")
    private List<EventParticipantEntity> eventParticipants;

    @ManyToMany
    private List<ChatEntity> chats;

    @OneToMany(mappedBy = "sender")
    private List<ChatMessageEntity> sentMessages;
}

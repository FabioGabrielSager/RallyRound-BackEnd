package org.example.rallyroundbackend.entity.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.example.rallyroundbackend.entity.chats.ChatEntity;
import org.example.rallyroundbackend.entity.chats.ChatMessageEntity;
import org.example.rallyroundbackend.entity.events.EventParticipantEntity;
import org.example.rallyroundbackend.entity.location.PlaceEntity;

import java.util.List;

@Entity
@Table(name = "participants")
@Getter @Setter
public class ParticipantEntity extends UserEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipantReputation reputation;

    @OneToOne
    @JoinColumn(name = "place_id")
    protected PlaceEntity place;

    @OneToOne
    private MPAuthTokenEntity mpAuthToken;

    @OneToMany
    private List<ReportEntity> reports;

    @OneToMany(mappedBy = "participant")
    private List<EventParticipantEntity> eventParticipants;

    @ManyToMany
    private List<ChatEntity> chats;

    @OneToMany(mappedBy = "sender")
    private List<ChatMessageEntity> sentMessages;
}

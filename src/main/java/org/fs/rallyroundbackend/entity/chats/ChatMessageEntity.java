package org.fs.rallyroundbackend.entity.chats;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_messages")
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Builder
public class ChatMessageEntity {
    @Id
    private UUID id;

    @Column(length = 300)
    private String message;

    private ZonedDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private ParticipantEntity sender;
}

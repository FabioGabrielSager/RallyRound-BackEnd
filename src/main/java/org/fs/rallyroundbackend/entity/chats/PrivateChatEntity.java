package org.fs.rallyroundbackend.entity.chats;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "private_chats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PrivateChatEntity extends ChatEntity {
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
            name = "participants_chats",
            joinColumns = @JoinColumn(name = "chat_id", referencedColumnName = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id", referencedColumnName = "id")
    )
    private List<ParticipantEntity> participants;
}

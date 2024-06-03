package org.fs.rallyroundbackend.entity.chats;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chats")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ChatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "chat_id")
    protected UUID chatId;

    @Enumerated(EnumType.STRING)
    @Column(name = "chat_type", nullable = false)
    protected ChatType chatType;

    @OneToMany(orphanRemoval = true, mappedBy = "chat")
    @Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    protected List<ChatMessageEntity> messages;
}

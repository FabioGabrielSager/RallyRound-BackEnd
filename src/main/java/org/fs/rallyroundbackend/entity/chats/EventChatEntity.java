package org.fs.rallyroundbackend.entity.chats;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.fs.rallyroundbackend.entity.events.EventEntity;

@Entity
@Table(name = "events_chats")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class EventChatEntity extends ChatEntity {
    @OneToOne
    @JoinColumn(name = "event_id")
    private EventEntity event;
}

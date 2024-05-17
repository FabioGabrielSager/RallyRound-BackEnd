package org.fs.rallyroundbackend.repository.chat;

import org.fs.rallyroundbackend.entity.chats.EventChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventChatRepository extends JpaRepository<EventChatEntity, UUID> {
}

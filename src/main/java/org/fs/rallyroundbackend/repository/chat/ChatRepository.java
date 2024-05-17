package org.fs.rallyroundbackend.repository.chat;

import org.fs.rallyroundbackend.entity.chats.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, UUID> {
}

package org.fs.rallyroundbackend.repository.chat;

import org.fs.rallyroundbackend.entity.chats.PrivateChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PrivateChatRepository extends JpaRepository<PrivateChatEntity, UUID> {
}

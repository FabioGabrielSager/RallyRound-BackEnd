package org.fs.rallyroundbackend.repository.chat;

import org.fs.rallyroundbackend.entity.chats.PrivateChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PrivateChatRepository extends JpaRepository<PrivateChatEntity, UUID> {
//    @Query("SELECT p FROM PrivateChatEntity p WHERE p.chatType = 'PRIVATE_CHAT' " +
//            "AND :sender in p.participants AND  :recipient in p.participants")
//    Optional<PrivateChatEntity> findBySenderAndRecipient(ParticipantEntity sender, ParticipantEntity recipient);
}

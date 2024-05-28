package org.fs.rallyroundbackend.repository.user.participant;

import org.fs.rallyroundbackend.entity.users.participant.EmailVerificationTokenEntity;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationTokenEntity, UUID> {
    Optional<EmailVerificationTokenEntity> findByUser(ParticipantEntity user);
}

package org.fs.rallyroundbackend.repository;

import org.fs.rallyroundbackend.entity.users.participant.MPAuthTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MPAuthTokenRepository extends JpaRepository<MPAuthTokenEntity, UUID> {
    Optional<MPAuthTokenEntity> findByUserId(Long userId);
}

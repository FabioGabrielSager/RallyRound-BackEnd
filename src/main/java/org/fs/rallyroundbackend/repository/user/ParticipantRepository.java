package org.fs.rallyroundbackend.repository.user;

import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParticipantRepository extends JpaRepository<ParticipantEntity, UUID> {
    @Query("SELECT p FROM ParticipantEntity AS p WHERE p.email = :email AND p.enabled = true")
    Optional<ParticipantEntity> findEnabledUserByEmail(String email);
}

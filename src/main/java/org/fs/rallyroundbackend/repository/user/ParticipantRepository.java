package org.fs.rallyroundbackend.repository.user;

import org.fs.rallyroundbackend.entity.users.ParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ParticipantRepository extends JpaRepository<ParticipantEntity, UUID> {
}

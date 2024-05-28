package org.fs.rallyroundbackend.repository.user.participant;

import org.fs.rallyroundbackend.entity.users.participant.ParticipantFavoriteActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FavoriteActivityRepository extends JpaRepository<ParticipantFavoriteActivityEntity, UUID> {
}

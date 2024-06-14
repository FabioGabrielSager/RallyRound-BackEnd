package org.fs.rallyroundbackend.repository.user.participant;

import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParticipantRepository extends JpaRepository<ParticipantEntity, UUID> {
    @Query("SELECT p FROM ParticipantEntity AS p WHERE p.email = :email AND p.enabled = true")
    Optional<ParticipantEntity> findEnabledUserByEmail(String email);

    @Query("SELECT p FROM ParticipantEntity AS p WHERE p.id = :id AND p.enabled = true")
    Optional<ParticipantEntity> findEnabledUserById(UUID id);

    @Query("SELECT p FROM ParticipantEntity AS p " +
            "WHERE (p.name LIKE %:name% OR p.lastName LIKE %:lastName%) AND p.email != :email")
    List<ParticipantEntity> findByNameOrLastName(String name, String lastName, String email,
                                                                            Pageable pageable);

    @Query("SELECT COUNT(distinct p) FROM ParticipantEntity AS p " +
            "WHERE (p.name LIKE %:name% OR p.lastName LIKE %:lastName%) AND p.email != :email")
    int countALlByNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndEmailNot(String name, String lastName,
                                                                                    String email);

    @Query(
            "SELECT p.id, p.name, p.lastName, p.profilePhoto, COUNT(DISTINCT e) finalized_events_count FROM ParticipantEntity p " +
                    "JOIN EventParticipantEntity ep ON p=ep.participant " +
                    "JOIN EventEntity e ON ep.event=e " +
                    "WHERE ep.isEventCreator IS TRUE AND e.state = 'FINALIZED' " +
                    "   AND (:month IS NOT NULL AND MONTH(e.date) = :month OR :month IS NULL AND MONTH(e.date) = MONTH(current_date)-1) " +
                    "   AND YEAR(e.date) = YEAR(current_date) " +
                    "GROUP BY p.id, p.name, p.lastName, p.profilePhoto " +
                    "ORDER BY finalized_events_count DESC " +
                    "LIMIT :topSize"
    )
    List<Object[]> getTopEventCreators(short topSize, int month);
}

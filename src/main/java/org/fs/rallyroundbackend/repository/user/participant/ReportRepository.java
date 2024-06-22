package org.fs.rallyroundbackend.repository.user.participant;

import org.fs.rallyroundbackend.entity.users.participant.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, UUID> {
    @Query(
            "SELECT r FROM ReportEntity r JOIN ParticipantEntity p ON r.reportedParticipant=p " +
                    "WHERE p.id=:participantId " +
                    "ORDER BY r.date DESC " +
                    "LIMIT :limit OFFSET :offset"
    )
    List<ReportEntity> findAllByParticipantId(UUID participantId, int limit, int offset);

    @Query(
            "SELECT COUNT(r) FROM ReportEntity r JOIN ParticipantEntity p ON r.reportedParticipant=p " +
                    "WHERE p.id=:participantId "
    )
    Long countAllByParticipantId(UUID participantId);
}

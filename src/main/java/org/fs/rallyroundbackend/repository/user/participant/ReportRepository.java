package org.fs.rallyroundbackend.repository.user.participant;

import org.fs.rallyroundbackend.dto.participant.ReportsByMotiveAndMonthAndYear;
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

    @Query(
            "SELECT new org.fs.rallyroundbackend.dto.participant.ReportsByMotiveAndMonthAndYear(" +
                    "SUM(CASE WHEN r.motive='INAPPROPRIATE_BEHAVIOR' THEN 1 ELSE 0 END)," +
                    "SUM(CASE WHEN r.motive='SPAMMING' THEN 1 ELSE 0 END)," +
                    "SUM(CASE WHEN r.motive='HARASSMENT' THEN 1 ELSE 0 END)," +
                    "SUM(CASE WHEN r.motive='OFFENSIVE_LANGUAGE' THEN 1 ELSE 0 END)," +
                    "SUM(CASE WHEN r.motive='FRAUD' THEN 1 ELSE 0 END)," +
                    "SUM(CASE WHEN r.motive='IMPERSONATION' THEN 1 ELSE 0 END)," +
                    "SUM(CASE WHEN r.motive='INAPPROPRIATE_CONTENT' THEN 1 ELSE 0 END)," +
                    "SUM(CASE WHEN r.motive='ABSENTEEISM' THEN 1 ELSE 0 END)," +
                    "SUM(CASE WHEN r.motive='OTHER' THEN 1 ELSE 0 END))" +
                    "FROM ReportEntity r " +
                    "WHERE YEAR(r.date)=:year AND MONTH(r.date)=:month"
    )
    ReportsByMotiveAndMonthAndYear getReportsByMotiveAndMonthAndYear(int year, int month);
}

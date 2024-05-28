package org.fs.rallyroundbackend.repository.user.admin;

import org.fs.rallyroundbackend.entity.users.AdminActivityLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdminActivityLogRepository extends JpaRepository<AdminActivityLogEntity, UUID> {
    @Query("SELECT MAX(a.actionCount) FROM AdminActivityLogEntity a WHERE a.admin.id = :adminId")
    Integer findMaxActionCountByAdminId(@Param("adminId") UUID adminId);
}

package org.fs.rallyroundbackend.service.imps;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.entity.users.AdminActivityLogEntity;
import org.fs.rallyroundbackend.entity.users.AdminEntity;
import org.fs.rallyroundbackend.repository.user.admin.AdminActivityLogRepository;
import org.fs.rallyroundbackend.repository.user.admin.AdminRepository;
import org.fs.rallyroundbackend.service.AdminActivityLoggerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminActivityLoggerServiceImp implements AdminActivityLoggerService {
    private final AdminActivityLogRepository adminActivityLogRepository;
    private final AdminRepository adminRepository;

    @Override
    @Transactional
    public UUID saveLog(String actionType, String actionDescription, String afectedResourceId,
                        String afectedResourceIdType, String adminEmail) {
        AdminEntity adminEntity = this.adminRepository.findEnabledUserByEmail(adminEmail)
                .orElseThrow(() -> new EntityNotFoundException("Admin with email " + adminEmail + " not found"));

        Integer adminLogCount = this.adminActivityLogRepository.findMaxActionCountByAdminId(adminEntity.getId());

        AdminActivityLogEntity activityLogEntity = AdminActivityLogEntity.builder()
                .actionType(actionType)
                .actionDescription(actionDescription)
                .afectedResourceId(afectedResourceId)
                .afectedResourceIdType(afectedResourceIdType)
                .admin(adminEntity)
                .timestamp(LocalDateTime.now())
                .actionCount(adminLogCount != null ? adminLogCount : 0)
                .build();

        try {
            activityLogEntity = this.adminActivityLogRepository.save(activityLogEntity);
        } catch (Exception e) {
            throw new RuntimeException("Error while saving admin activity log.");
        }

        return activityLogEntity.getId();
    }
}

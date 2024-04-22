package org.fs.rallyroundbackend.repository.location;

import org.fs.rallyroundbackend.entity.location.AdminDistrictEntity;
import org.fs.rallyroundbackend.entity.location.AdminSubdistrictEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminSubdistricRepository extends JpaRepository<AdminSubdistrictEntity, UUID> {
    Optional<AdminSubdistrictEntity> findByNameAndAdminDistrict(String name, AdminDistrictEntity adminDistrict);
}

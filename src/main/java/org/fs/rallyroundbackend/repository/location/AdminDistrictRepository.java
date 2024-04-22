package org.fs.rallyroundbackend.repository.location;

import org.fs.rallyroundbackend.entity.location.AdminDistrictEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Optional;

public interface AdminDistrictRepository extends JpaRepository<AdminDistrictEntity, UUID> {
    Optional<AdminDistrictEntity> findByName(String name);
}

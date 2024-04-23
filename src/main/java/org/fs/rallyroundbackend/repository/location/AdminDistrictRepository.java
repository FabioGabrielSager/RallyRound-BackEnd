package org.fs.rallyroundbackend.repository.location;

import org.fs.rallyroundbackend.entity.location.AdminDistrictEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface AdminDistrictRepository extends JpaRepository<AdminDistrictEntity, UUID> {
    Optional<AdminDistrictEntity> findByName(String name);
}

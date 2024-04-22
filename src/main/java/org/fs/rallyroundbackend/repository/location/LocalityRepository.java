package org.fs.rallyroundbackend.repository.location;

import org.fs.rallyroundbackend.entity.location.AdminSubdistrictEntity;
import org.fs.rallyroundbackend.entity.location.LocalityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LocalityRepository extends JpaRepository<LocalityEntity, UUID> {
    Optional<LocalityEntity> findByNameAndAdminSubdistrict(String name, AdminSubdistrictEntity adminSubdistrictEntity);
}

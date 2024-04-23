package org.fs.rallyroundbackend.repository.location;

import org.fs.rallyroundbackend.entity.location.AdminSubdistrictEntity;
import org.fs.rallyroundbackend.entity.location.LocalityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocalityRepository extends JpaRepository<LocalityEntity, UUID> {
    Optional<LocalityEntity> findByNameAndAdminSubdistrict(String name, AdminSubdistrictEntity adminSubdistrictEntity);
}

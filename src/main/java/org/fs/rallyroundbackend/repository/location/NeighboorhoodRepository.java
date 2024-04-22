package org.fs.rallyroundbackend.repository.location;

import org.fs.rallyroundbackend.entity.location.LocalityEntity;
import org.fs.rallyroundbackend.entity.location.NeighborhoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NeighboorhoodRepository extends JpaRepository<NeighborhoodEntity, UUID> {
    Optional<NeighborhoodEntity> findByNameAndLocality(String name, LocalityEntity locality);
}

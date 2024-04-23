package org.fs.rallyroundbackend.repository.location;

import org.fs.rallyroundbackend.entity.location.LocalityEntity;
import org.fs.rallyroundbackend.entity.location.NeighborhoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NeighboorhoodRepository extends JpaRepository<NeighborhoodEntity, UUID> {
    Optional<NeighborhoodEntity> findByNameAndLocality(String name, LocalityEntity locality);
}

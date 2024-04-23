package org.fs.rallyroundbackend.repository.location;

import org.fs.rallyroundbackend.entity.location.NeighborhoodEntity;
import org.fs.rallyroundbackend.entity.location.StreetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StreetRepository extends JpaRepository<StreetEntity, UUID> {
    Optional<StreetEntity> findByNameAndNeighborhood(String name, NeighborhoodEntity neighborhood);
}

package org.fs.rallyroundbackend.repository.location;

import org.fs.rallyroundbackend.entity.location.AddressLineEntity;
import org.fs.rallyroundbackend.entity.location.EntityType;
import org.fs.rallyroundbackend.entity.location.FormattedAddressEntity;
import org.fs.rallyroundbackend.entity.location.NeighborhoodEntity;
import org.fs.rallyroundbackend.entity.location.PlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlaceRepository extends JpaRepository<PlaceEntity, UUID> {
    Optional<PlaceEntity>  findByNeighborhoodAndPostalCodeAndFormattedAddressAndAddressLineAndEntityTypeAndName(
            NeighborhoodEntity neighborhoodEntity, String postalCode,
            FormattedAddressEntity formattedAddressEntity,
            AddressLineEntity addressLineEntity, EntityType entityType, String name
    );
}

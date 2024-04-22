package org.fs.rallyroundbackend.repository.location;

import org.fs.rallyroundbackend.entity.location.AddressLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AddressLineRepository extends JpaRepository<AddressLineEntity, UUID> {
    Optional<AddressLineEntity> findByAddressLine(String addressLine);
}

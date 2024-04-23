package org.fs.rallyroundbackend.repository.location;

import org.fs.rallyroundbackend.entity.location.AddressLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressLineRepository extends JpaRepository<AddressLineEntity, UUID> {
    Optional<AddressLineEntity> findByAddressLine(String addressLine);
}

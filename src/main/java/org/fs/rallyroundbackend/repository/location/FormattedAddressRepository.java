package org.fs.rallyroundbackend.repository.location;

import org.fs.rallyroundbackend.entity.location.FormattedAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FormattedAddressRepository extends JpaRepository<FormattedAddressEntity, UUID> {
    Optional<FormattedAddressEntity> findByFormattedAddress(String formattedAddress);
}

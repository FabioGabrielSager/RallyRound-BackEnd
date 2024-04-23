package org.fs.rallyroundbackend.repository.location;

import org.fs.rallyroundbackend.entity.location.FormattedAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FormattedAddressRepository extends JpaRepository<FormattedAddressEntity, UUID> {
    Optional<FormattedAddressEntity> findByFormattedAddress(String formattedAddress);
}

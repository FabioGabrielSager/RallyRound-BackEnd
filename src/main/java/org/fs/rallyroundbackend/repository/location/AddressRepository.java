package org.fs.rallyroundbackend.repository.location;

import org.fs.rallyroundbackend.entity.location.AddressEntity;
import org.fs.rallyroundbackend.entity.location.AddressLineEntity;
import org.fs.rallyroundbackend.entity.location.FormattedAddressEntity;
import org.fs.rallyroundbackend.entity.location.StreetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
    Optional<AddressEntity> findByStreetAndAddressLineAndFormattedAddressAndPostalCode(
            StreetEntity streetEntity, AddressLineEntity addressLineEntity,
            FormattedAddressEntity formattedAddress, String postalCode
    );
}

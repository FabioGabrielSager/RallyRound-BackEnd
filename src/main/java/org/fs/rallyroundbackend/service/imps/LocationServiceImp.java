package org.fs.rallyroundbackend.service.imps;

import lombok.AllArgsConstructor;
import org.fs.rallyroundbackend.dto.location.places.PlaceAddressDto;
import org.fs.rallyroundbackend.dto.location.places.PlaceDto;
import org.fs.rallyroundbackend.entity.location.AddressLineEntity;
import org.fs.rallyroundbackend.entity.location.AdminDistrictEntity;
import org.fs.rallyroundbackend.entity.location.AdminSubdistrictEntity;
import org.fs.rallyroundbackend.entity.location.FormattedAddressEntity;
import org.fs.rallyroundbackend.entity.location.LocalityEntity;
import org.fs.rallyroundbackend.entity.location.NeighborhoodEntity;
import org.fs.rallyroundbackend.entity.location.PlaceEntity;
import org.fs.rallyroundbackend.repository.location.AddressLineRepository;
import org.fs.rallyroundbackend.repository.location.AdminDistrictRepository;
import org.fs.rallyroundbackend.repository.location.AdminSubdistricRepository;
import org.fs.rallyroundbackend.repository.location.FormattedAddressRepository;
import org.fs.rallyroundbackend.repository.location.LocalityRepository;
import org.fs.rallyroundbackend.repository.location.NeighboorhoodRepository;
import org.fs.rallyroundbackend.repository.location.PlaceRepository;
import org.fs.rallyroundbackend.service.LocationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Implementation of the {@link LocationService} interface that provides methods for managing location-related entities.
 */
@Service
@AllArgsConstructor
public class LocationServiceImp implements LocationService {
    private AdminDistrictRepository adminDistrictRepository;
    private AdminSubdistricRepository adminSubdistricRepository;
    private LocalityRepository localityRepository;
    private NeighboorhoodRepository neighboorhoodRepository;
    private FormattedAddressRepository formattedAddressRepository;
    private AddressLineRepository addressLineRepository;
    private PlaceRepository placeRepository;

    @Transactional
    @Override
    public PlaceEntity getPlaceEntityFromPlaceDto(PlaceDto placeDto) {
        PlaceAddressDto address = placeDto.getAddress();

        NeighborhoodEntity neighborhoodEntity = findOrCreateNeighbourhood(address);

        FormattedAddressEntity formattedAddressEntity =
                findOrCreateFormattedAddressEntity(address.getFormattedAddress());

        AddressLineEntity addressLineEntity = findOrCreateAddressLineEntity(address.getAddressLine());

        PlaceEntity placeEntity = new PlaceEntity();

        Optional<PlaceEntity> placeEntityOptional = this.placeRepository
                .findByNeighborhoodAndPostalCodeAndFormattedAddressAndAddressLineAndEntityTypeAndName(
                        neighborhoodEntity, address.getPostalCode(), formattedAddressEntity, addressLineEntity,
                        placeDto.getEntityType(), placeDto.getName());

        if(placeEntityOptional.isPresent()) {
            placeEntity = placeEntityOptional.get();
        } else {
            placeEntity.setNeighborhood(neighborhoodEntity);
            placeEntity.setPostalCode(address.getPostalCode());
            placeEntity.setFormattedAddress(formattedAddressEntity);
            placeEntity.setEntityType(placeDto.getEntityType());
            placeEntity.setAddressLine(addressLineEntity);
            placeEntity.setName(placeDto.getName());

            this.placeRepository.save(placeEntity);
        }

        return placeEntity;
    }

    @Transactional
    @Override
    public AddressLineEntity findOrCreateAddressLineEntity(String addressLine) {
        AddressLineEntity addressLineEntity = new AddressLineEntity();

        Optional<AddressLineEntity> addressLineEntityOptional = this.addressLineRepository
                .findByAddressLine(addressLine);
        if(addressLineEntityOptional.isPresent()) {
            addressLineEntity = addressLineEntityOptional.get();
        } else {
            addressLineEntity.setAddressLine(addressLine);
            this.addressLineRepository.save(addressLineEntity);
        }

        return addressLineEntity;
    }

    @Transactional
    @Override
    public FormattedAddressEntity findOrCreateFormattedAddressEntity(String formattedAddress) {
        FormattedAddressEntity formattedAddressEntity = new FormattedAddressEntity();

        Optional<FormattedAddressEntity> formattedAddressEntityOptional = this.formattedAddressRepository
                .findByFormattedAddress(formattedAddress);

        if(formattedAddressEntityOptional.isPresent()) {
            formattedAddressEntity = formattedAddressEntityOptional.get();
        } else {
            formattedAddressEntity.setFormattedAddress(formattedAddress);
            this.formattedAddressRepository.save(formattedAddressEntity);
        }

        return formattedAddressEntity;
    }

    @Transactional
    @Override
    public NeighborhoodEntity findOrCreateNeighbourhood(PlaceAddressDto address) {
        LocalityEntity localityEntity = findOrCreateLocality(address.getLocality(), address.getAdminDistrict2(),
                address.getAdminDistrict());

        NeighborhoodEntity neighborhoodEntity = new NeighborhoodEntity();

        Optional<NeighborhoodEntity> neighborhoodEntityOptional = this.neighboorhoodRepository
                .findByNameAndLocality(address.getNeighborhood(), localityEntity);

        if (neighborhoodEntityOptional.isPresent()) {
            neighborhoodEntity = neighborhoodEntityOptional.get();
        } else {
            neighborhoodEntity.setName(address.getNeighborhood());
            localityEntity.getNeighborhoods().add(neighborhoodEntity);
            neighborhoodEntity.setLocality(localityEntity);

            // With save the associated localityEntity is also updated due to cascading entities configuration
            this.neighboorhoodRepository.save(neighborhoodEntity);
        }

        return neighborhoodEntity;
    }

    @Transactional
    @Override
    public LocalityEntity findOrCreateLocality(String localityName, String adminSubdistrictName,
                                               String adminDistrictName) {
        AdminSubdistrictEntity adminSubdistrictEntity = findOrCreateAdminSubdistrict(adminSubdistrictName,
                adminDistrictName);
        LocalityEntity localityEntity = new LocalityEntity();
        Optional<LocalityEntity> localityEntityOptional = this.localityRepository
                .findByNameAndAdminSubdistrict(localityName, adminSubdistrictEntity);

        if (localityEntityOptional.isPresent()) {
            localityEntity = localityEntityOptional.get();
        } else {
            localityEntity.setName(localityName);
            localityEntity.setNeighborhoods(new ArrayList<>());
            localityEntity.setAdminSubdistrict(adminSubdistrictEntity);

            adminSubdistrictEntity.getLocalities().add(localityEntity);

            // With save the associated adminSubdistrictEntity is also updated due to cascading entities configuration
            this.localityRepository.save(localityEntity);
        }

        return localityEntity;
    }

    @Transactional
    @Override
    public AdminSubdistrictEntity findOrCreateAdminSubdistrict(String adminSubdistrictName,
                                                               String adminDistrictName) {
        AdminDistrictEntity adminDistrict = findOrCreateAdminDistrict(adminDistrictName);
        AdminSubdistrictEntity adminSubdistrictEntity = new AdminSubdistrictEntity();

        Optional<AdminSubdistrictEntity> adminSubdistrictEntityOptional = this.adminSubdistricRepository
                .findByNameAndAdminDistrict(adminSubdistrictName, adminDistrict);

        if(adminSubdistrictEntityOptional.isPresent()) {
            adminSubdistrictEntity = adminSubdistrictEntityOptional.get();
        } else {
            adminSubdistrictEntity.setName(adminSubdistrictName);
            adminSubdistrictEntity.setLocalities(new ArrayList<>());
            adminSubdistrictEntity.setAdminDistrict(adminDistrict);

            adminDistrict.getAdminSubdistricts().add(adminSubdistrictEntity);

            // With save the associated adminDistrict is also updated due to cascading entities configuration
            this.adminSubdistricRepository.save(adminSubdistrictEntity);
        }

        return adminSubdistrictEntity;
    }

    @Transactional
    @Override
    public AdminDistrictEntity findOrCreateAdminDistrict(String name) {
        AdminDistrictEntity adminDistrictEntity = new AdminDistrictEntity();
        adminDistrictEntity.setName(name);
        adminDistrictEntity.setAdminSubdistricts(new ArrayList<>());

        Optional<AdminDistrictEntity> adminDistrictEntityOptional =
                this.adminDistrictRepository.findByName(name);

        if(adminDistrictEntityOptional.isPresent()) {
            adminDistrictEntity = adminDistrictEntityOptional.get();
        } else {
            this.adminDistrictRepository.save(adminDistrictEntity);
        }

        return adminDistrictEntity;
    }
}

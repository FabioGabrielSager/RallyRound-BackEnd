package org.fs.rallyroundbackend.service;

import org.fs.rallyroundbackend.dto.location.addresses.AddressDto;
import org.fs.rallyroundbackend.dto.location.addresses.SpecificAddressDto;
import org.fs.rallyroundbackend.dto.location.places.PlaceAddressDto;
import org.fs.rallyroundbackend.dto.location.places.PlaceDto;
import org.fs.rallyroundbackend.entity.location.AddressEntity;
import org.fs.rallyroundbackend.entity.location.AddressLineEntity;
import org.fs.rallyroundbackend.entity.location.AdminDistrictEntity;
import org.fs.rallyroundbackend.entity.location.AdminSubdistrictEntity;
import org.fs.rallyroundbackend.entity.location.FormattedAddressEntity;
import org.fs.rallyroundbackend.entity.location.LocalityEntity;
import org.fs.rallyroundbackend.entity.location.NeighborhoodEntity;
import org.fs.rallyroundbackend.entity.location.PlaceEntity;
import org.fs.rallyroundbackend.entity.location.StreetEntity;

/**
 * Service interface for managing location-related entities.
 */
public interface LocationService {

    /**
     * Finds places consulting to the bing maps autosuggest API based on the specified query.
     *
     * @param query the query to search for places
     * @return an array of PlaceDto objects matching the query
     */
    PlaceDto[] findPlacesByQuery(String query);

    /**
     * Finds addresses consulting to the bing maps autosuggest based on the specified query.
     *
     * @param query the query to search for addresses
     * @return an array of AddressDto objects matching the query
     */
    AddressDto[] findAddressesByQuery(String query);

    /**
     * Retrieves or creates a {@link AddressEntity} based on the provided {@link AddressDto}.
     * If a matching place entity already exists in the database, it is retrieved; otherwise,
     * a new entity is created, saved and retrieved.
     *
     * @param addressDto The address DTO to convert to a place entity.
     * @return The address entity corresponding to the given address DTO.
     */
    AddressEntity getAddressEntityFromAddressDto(AddressDto addressDto);

    /**
     * Retrieves or creates a {@link PlaceEntity} based on the provided {@link PlaceDto}.
     * If a matching place entity already exists in the database, it is retrieved; otherwise,
     * a new entity is created, saved and retrieved.
     *
     * @param placeDto The place DTO to convert to a place entity.
     * @return The place entity corresponding to the given place DTO.
     */
    PlaceEntity getPlaceEntityFromPlaceDto(PlaceDto placeDto);

    /**
     * Retrieves or creates an {@link AddressLineEntity} based on the provided address line string.
     * If a matching address line entity already exists in the database, it is retrieved; otherwise,
     * a new entity is created, saved and retrieved.
     *
     * @param addressLine The address line string.
     * @return The address line entity corresponding to the given address line string.
     */
    AddressLineEntity findOrCreateAddressLineEntity(String addressLine);

    /**
     * Retrieves or creates a {@link FormattedAddressEntity} based on the provided formatted address string.
     * If a matching formatted address entity already exists in the database, it is retrieved;
     * otherwise, a new entity is created, saved and retrieved.
     *
     * @param formattedAddress The formatted address string.
     * @return The formatted address entity corresponding to the given formatted address string.
     */
    FormattedAddressEntity findOrCreateFormattedAddressEntity(String formattedAddress);

    /**
     * Retrieves or creates a {@link StreetEntity} based on the provided {@link SpecificAddressDto}.
     * If a matching street entity already exists in the database, it is retrieved;
     * otherwise, a new entity is created, saved and retrieved.
     *
     * @param address The specific address DTO containing street information.
     * @return The street entity corresponding to the given place address DTO.
     */
    StreetEntity findOrCreateStreet(SpecificAddressDto address);

    /**
     * Retrieves or creates a {@link NeighborhoodEntity} based on the provided {@link PlaceAddressDto}.
     * If a matching neighborhood entity already exists in the database, it is retrieved;
     * otherwise, a new entity is created, saved and retrieved.
     *
     * @param address The place address DTO containing neighborhood information.
     * @return The neighborhood entity corresponding to the given place address DTO.
     */
    NeighborhoodEntity findOrCreateNeighbourhood(PlaceAddressDto address);

    /**
     * Retrieves or creates a {@link LocalityEntity} based on the provided locality, admin subdistrict, and
     * admin district names.
     * If a matching locality entity already exists in the database, it is retrieved;
     * otherwise, a new entity is created, saved and retrieved.
     *
     * @param localityName         The name of the locality.
     * @param adminSubdistrictName The name of the admin subdistrict.
     * @param adminDistrictName    The name of the admin district.
     * @return The locality entity corresponding to the given locality, admin subdistrict, and admin district names.
     */
    LocalityEntity findOrCreateLocality(String localityName, String adminSubdistrictName, String adminDistrictName);

    /**
     * Retrieves or creates an {@link AdminSubdistrictEntity} based on the provided admin subdistrict and admin
     * district names. If a matching admin subdistrict entity already exists in the database, it is retrieved;
     * otherwise, a new entity is created, saved and retrieved.
     *
     * @param adminSubdistrictName The name of the admin subdistrict.
     * @param adminDistrictName    The name of the admin district.
     * @return The admin subdistrict entity corresponding to the given admin subdistrict and admin district names.
     */
    AdminSubdistrictEntity findOrCreateAdminSubdistrict(String adminSubdistrictName, String adminDistrictName);

    /**
     * Retrieves or creates an {@link AdminDistrictEntity} based on the provided admin district name. If a matching
     * admin district entity already exists in the database, it is retrieved; otherwise, a new entity is created,
     * saved and retrieved.
     *
     * @param name The name of the admin district.
     * @return The admin district entity corresponding to the given admin district name.
     */
    AdminDistrictEntity findOrCreateAdminDistrict(String name);
}

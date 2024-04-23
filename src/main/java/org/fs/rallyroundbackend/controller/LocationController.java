package org.fs.rallyroundbackend.controller;

import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.location.addresses.AddressDto;
import org.fs.rallyroundbackend.dto.location.places.PlaceDto;
import org.fs.rallyroundbackend.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rr/api/v1/location")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @GetMapping("/autosuggest/places/{query}")
    public ResponseEntity<PlaceDto[]> getPlaceDto(@PathVariable String query) {
        return ResponseEntity.ok(this.locationService.findPlacesByQuery(query));
    }

    @GetMapping("/autosuggest/addresses/{query}")
    public ResponseEntity<AddressDto[]> getAddressDto(@PathVariable String query) {
        return ResponseEntity.ok(this.locationService.findAddressesByQuery(query));
    }
}

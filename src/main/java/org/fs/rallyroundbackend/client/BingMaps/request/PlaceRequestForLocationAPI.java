package org.fs.rallyroundbackend.client.BingMaps.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PlaceRequestForLocationAPI represents a request object used for retrieving location information from the Bing Maps API.
 * It contains details of a place such as administrative district, locality, address line, and postal code.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PlaceRequestForLocationAPI {
    @NotBlank @NotNull
    private String adminDistrict;
    @NotBlank @NotNull
    private String locality;
    private String addressLine;
    private String postalCode;
}

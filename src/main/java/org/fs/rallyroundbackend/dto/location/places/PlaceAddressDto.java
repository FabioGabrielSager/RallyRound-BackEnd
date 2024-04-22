package org.fs.rallyroundbackend.dto.location.places;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaceAddressDto {
    @NotBlank
    private String adminDistrict;
    @NotBlank
    private String adminDistrict2;
    @NotBlank
    private String locality;
    @NotBlank
    private String countryRegion;
    @NotBlank
    private String formattedAddress;
    private String addressLine;
    private String postalCode;
    private String neighborhood;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlaceAddressDto) {
            PlaceAddressDto address = (PlaceAddressDto) obj;

            return Objects.equals(this.adminDistrict, address.adminDistrict)
                    && Objects.equals(this.adminDistrict2, address.adminDistrict2)
                    && Objects.equals(this.locality, address.locality)
                    && Objects.equals(this.countryRegion, address.countryRegion)
                    && Objects.equals(this.formattedAddress, address.formattedAddress)
                    && Objects.equals(this.addressLine, address.addressLine)
                    && Objects.equals(this.postalCode, address.postalCode)
                    && Objects.equals(this.neighborhood, address.neighborhood);
        }
        return false;
    }
}

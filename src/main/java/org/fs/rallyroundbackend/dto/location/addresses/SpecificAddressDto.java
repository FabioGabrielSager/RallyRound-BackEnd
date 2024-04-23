package org.fs.rallyroundbackend.dto.location.addresses;

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
public class SpecificAddressDto {
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
    private String streetName;
    private String houseNumber;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SpecificAddressDto) {
            SpecificAddressDto address = (SpecificAddressDto) obj;

            return Objects.equals(this.adminDistrict, address.adminDistrict)
                    && Objects.equals(this.adminDistrict2, address.adminDistrict2)
                    && Objects.equals(this.locality, address.locality)
                    && Objects.equals(this.countryRegion, address.countryRegion)
                    && Objects.equals(this.formattedAddress, address.formattedAddress)
                    && Objects.equals(this.addressLine, address.addressLine)
                    && Objects.equals(this.postalCode, address.postalCode)
                    && Objects.equals(this.neighborhood, address.neighborhood)
                    && Objects.equals(this.streetName, address.streetName);
        }
        return false;
    }
}
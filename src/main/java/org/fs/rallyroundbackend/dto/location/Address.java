package org.fs.rallyroundbackend.dto.location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Address {
    @NotBlank
    @NotNull
    private String adminDistrict;
    @NotBlank @NotNull
    private String adminDistrict2;
    @NotBlank @NotNull
    private String locality;
    @NotBlank @NotNull
    private String countryRegion;
    @NotBlank @NotNull
    private String formattedAddress;
    private String addressLine;
    private String postalCode;
    private String neighborhood;
}

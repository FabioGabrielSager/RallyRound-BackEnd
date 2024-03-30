package org.fs.rallyroundbackend.client.BingMaps.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter
public class Address {
    private String adminDistrict;
    private String adminDistrict2;
    private String locality;
    private String countryRegion;
    private String formattedAddress;
    private String addressLine;
    private String postalCode;
    private String neighborhood;
}

package org.fs.rallyroundbackend.dto.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceRequest {
    @JsonProperty("__type")
    @NotBlank @NotNull
    private String EntityType;
    @NotNull
    private Address address;
    private String name;
}

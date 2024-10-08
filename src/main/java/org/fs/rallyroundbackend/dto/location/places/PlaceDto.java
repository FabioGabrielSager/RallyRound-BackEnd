package org.fs.rallyroundbackend.dto.location.places;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.entity.location.EntityType;

import java.util.Objects;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceDto {
    @JsonProperty("__type")
    @NotBlank @NotNull
    private EntityType entityType;
    @NotNull
    private PlaceAddressDto address;
    private String name;

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PlaceDto) {

            PlaceDto placeDto = (PlaceDto) obj;

            return Objects.equals(this.entityType, placeDto.entityType)
                    && Objects.equals(this.name, placeDto.name)
                    && Objects.equals(this.address, placeDto.address);
        }

        return false;
    }
}

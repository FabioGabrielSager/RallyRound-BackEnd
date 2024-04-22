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

    public String getFormattedName() {
        String formattedName = "";

        if(this.address != null)  {
            if(this.address.getAdminDistrict() != null && !this.address.getAdminDistrict().isEmpty()) {
                formattedName += this.address.getAdminDistrict() + ", ";
            }
            if(this.address.getAdminDistrict2() != null && !this.address.getAdminDistrict2().isEmpty()) {
                formattedName += this.address.getAdminDistrict2() + ", ";
            }
            if(this.address.getLocality() != null && !this.address.getLocality().isEmpty()) {
                formattedName += this.address.getLocality() + ", ";
            }
            if(this.address.getNeighborhood() != null && !this.address.getNeighborhood().isEmpty()) {
                formattedName += this.address.getNeighborhood() + ", ";
            }
            if(this.name != null && !this.name.isEmpty()) {
                formattedName += this.name;
            }
        }

        return formattedName.endsWith(", ") ? formattedName.substring(0, formattedName.lastIndexOf(", "))
                : formattedName;
    }

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

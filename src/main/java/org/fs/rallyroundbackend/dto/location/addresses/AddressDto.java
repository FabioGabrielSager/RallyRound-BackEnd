package org.fs.rallyroundbackend.dto.location.addresses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressDto {
    @JsonProperty("__type")
    @NotBlank
    @NotNull
    private EntityType entityType;
    @NotNull
    private SpecificAddressDto address;

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof AddressDto) {

            AddressDto addressDto = (AddressDto) obj;

            return Objects.equals(this.entityType, addressDto.entityType)
                    && Objects.equals(this.address, addressDto.address);
        }

        return false;
    }
}

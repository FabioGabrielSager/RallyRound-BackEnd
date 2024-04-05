package org.fs.rallyroundbackend.client.BingMaps.response.autosuggestion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.dto.location.PlaceDto;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Resource {
    private PlaceDto[] value;
}

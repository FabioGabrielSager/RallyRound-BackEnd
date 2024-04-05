package org.fs.rallyroundbackend.client.BingMaps.response.autosuggestion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter @Getter
public class BingMapApiAutosuggestionResponse {
    private AutosuggestionResponseResourceSet[] resourceSets;
}

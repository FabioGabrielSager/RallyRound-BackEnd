package org.fs.rallyroundbackend.client.BingMaps.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * BingMapApiLocationResponse is used to map the response object retrieved by a request for location information to the Bing Maps API.
 * <p><strong>It only maps the resourceSets field of the response.</strong></p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter
public class BingMapApiLocationResponse {
    private List<ResourceSet> resourceSets;
}

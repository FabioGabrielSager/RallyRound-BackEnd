package org.fs.rallyroundbackend.client.BingMaps.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * ResourceSet represents the resourceSet field object of a response object retrieved by a request for location
 * information to the Bing Maps API.
 * <p><strong>It only maps the resources field of the resourceSet object.</strong></p>
 * @see BingMapApiLocationResponse
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter
public class ResourceSet {
    private List<Location> resources;
}

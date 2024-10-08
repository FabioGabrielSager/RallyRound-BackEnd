package org.fs.rallyroundbackend.client.BingMaps.response.location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.fs.rallyroundbackend.client.BingMaps.response.Address;
import org.fs.rallyroundbackend.client.BingMaps.response.Point;

/**
 * Location maps the address and point field object of a resource from resourceSet field of a response object
 * retrieved by a request for location information to the Bing Maps API.
 * <p><strong>It only maps the address and point field of the resource object.</strong></p>
 * @see LocationResponseResourceSet
 * @see BingMapApiLocationResponse
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Location {
    private Address address;
    private Point point;
}

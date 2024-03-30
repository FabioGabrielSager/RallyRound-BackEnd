package org.fs.rallyroundbackend.client.BingMaps;

import jakarta.validation.Valid;
import org.fs.rallyroundbackend.client.BingMaps.request.PlaceRequestForLocationAPI;
import org.fs.rallyroundbackend.client.BingMaps.response.BingMapApiLocationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * BingMapApiClient is a service class that interacts with the Bing Maps API to retrieve location information.
 * It provides methods to set the base URL for API requests and to retrieve location information by place.
 */
@Service
public class BingMapApiClient {
    private WebClient webClient;
    @Value("${bingmaps.api.key}")
    private String BING_MAPS_API_KEY;
    private String baseUrl = "https://dev.virtualearth.net/REST/v1";

    /**
     * Constructs a new BingMapApiClient with the specified WebClient.
     *
     * @param webClient the WebClient used to make HTTP requests to the Bing Maps API
     */
    public BingMapApiClient(WebClient webClient) {
        this.webClient = webClient.mutate().baseUrl(this.baseUrl).build();
    }

    /**
     * Sets the base URL for API requests.
     * This getter is generally used for testing purposes.
     *
     * @param baseUrl the base URL for API requests
     */
    public void setBaseUrl(String baseUrl) {
        this.webClient = this.webClient.mutate().baseUrl(baseUrl).build();
        this.baseUrl = baseUrl;
    }

    /**
     * Retrieves location information from the Bing Maps API based on the provided place request.
     *
     * @param placeRequest the request object containing details of the place to search for
     * @return a Mono containing the response object with location information
     */
    public Mono<BingMapApiLocationResponse> getLocationByPlace(@Valid PlaceRequestForLocationAPI placeRequest) {
        return this.webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/Locations")
                        .queryParam("adminDistrict", placeRequest.getAdminDistrict())
                        .queryParam("locality", placeRequest.getLocality())
                        .queryParamIfPresent("postalCode", Optional.ofNullable(placeRequest.getPostalCode()))
                        .queryParamIfPresent("addressLine", Optional.ofNullable(placeRequest.getAddressLine()))
                        .queryParam("countryRegion", "ar")
                        .queryParam("key", this.BING_MAPS_API_KEY)
                        .build())
                .retrieve()
                .bodyToMono(BingMapApiLocationResponse.class);
    }
}

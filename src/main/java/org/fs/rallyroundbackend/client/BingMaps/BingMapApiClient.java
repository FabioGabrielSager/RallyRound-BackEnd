package org.fs.rallyroundbackend.client.BingMaps;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.fs.rallyroundbackend.client.BingMaps.request.PlaceRequestForLocationAPI;
import org.fs.rallyroundbackend.client.BingMaps.response.autosuggestion.BingMapApiAutosuggestionResponse;
import org.fs.rallyroundbackend.client.BingMaps.response.location.BingMapApiLocationResponse;
import org.fs.rallyroundbackend.dto.location.addresses.AddressDto;
import org.fs.rallyroundbackend.dto.location.places.PlaceDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
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
    private ModelMapper modelMapper;
    @Value("${bingmaps.api.key}")
    private String BING_MAPS_API_KEY;
    private String baseUrl = "https://dev.virtualearth.net/REST/v1";

    /**
     * Constructs a new BingMapApiClient with the specified WebClient.
     *
     * @param webClient the WebClient used to make HTTP requests to the Bing Maps API
     */
    public BingMapApiClient(WebClient webClient, ModelMapper modelMapper) {
        this.webClient = webClient.mutate().baseUrl(this.baseUrl).build();
        this.modelMapper = modelMapper;
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
                        .queryParam("countryRegion", "AR")
                        .queryParam("includeNeighborhood", 1)
                        .queryParam("strictMatch", 1)
                        .queryParam("key", this.BING_MAPS_API_KEY)
                        .build())
                .retrieve()
                .bodyToMono(BingMapApiLocationResponse.class);
    }

    public Mono<PlaceDto[]> getAutosuggestionByPlace(@Validated @NotBlank String query) {
        return this.webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/Autosuggest")
                        .queryParam("includeEntityTypes", "Place")
                        .queryParam("culture", "es-AR")
                        .queryParam("userRegion", "AR")
                        .queryParam("countryFilter", "AR")
                        .queryParam("query", query)
                        .queryParam("key", this.BING_MAPS_API_KEY)
                        .build())
                .retrieve()
                .bodyToMono(BingMapApiAutosuggestionResponse.class)
                .flatMap(response -> {
                    if (response != null
                            && response.getResourceSets() != null
                            && response.getResourceSets().length > 0
                            && response.getResourceSets()[0] != null
                            && response.getResourceSets()[0].getResources() != null
                            && response.getResourceSets()[0].getResources().length > 0) {
                        return Mono.just(this.modelMapper.map(response.getResourceSets()[0]
                                        .getResources()[0].getValue(),
                                PlaceDto[].class));
                    } else {
                        return Mono.just(new PlaceDto[]{});
                    }
                });
    }

    public Mono<AddressDto[]> getAutosuggestionByAddress(@Validated @NotBlank String query) {
        return this.webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/Autosuggest")
                        .queryParam("includeEntityTypes", "Address")
                        .queryParam("culture", "es-AR")
                        .queryParam("userRegion", "AR")
                        .queryParam("countryFilter", "AR")
                        .queryParam("query", query)
                        .queryParam("key", this.BING_MAPS_API_KEY)
                        .build())
                .retrieve()
                .bodyToMono(BingMapApiAutosuggestionResponse.class)
                .flatMap(response -> {
                    if (response != null
                            && response.getResourceSets() != null
                            && response.getResourceSets().length > 0
                            && response.getResourceSets()[0] != null
                            && response.getResourceSets()[0].getResources() != null
                            && response.getResourceSets()[0].getResources().length > 0) {
                        return Mono.just(response.getResourceSets()[0].getResources()[0].getValue());
                    } else {
                        return Mono.just(new AddressDto[]{});
                    }
                });
    }
}

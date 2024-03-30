package org.example.rallyroundbackend.client;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.fs.rallyroundbackend.client.BingMaps.BingMapApiClient;
import org.fs.rallyroundbackend.client.BingMaps.request.PlaceRequestForLocationAPI;
import org.fs.rallyroundbackend.client.BingMaps.response.BingMapApiLocationResponse;
import org.fs.rallyroundbackend.client.BingMaps.response.Location;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BingMapsApiClientTest {
    private static MockWebServer mockWebServer;
    private static BingMapApiClient underTest;

    @BeforeAll
    public static void setUp() {
        mockWebServer = new MockWebServer();
        WebClient mockedWebClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/test").toString())
                .build();
        underTest = new BingMapApiClient(mockedWebClient);
        underTest.setBaseUrl(mockWebServer.url("/test").toString());
    }

    @AfterAll
    public static void tearDown() throws IOException {
        mockWebServer.close();
    }

    @Test
    public void getLocation_WhenAddressLineAndPostaAreNotGiven() {
        PlaceRequestForLocationAPI request = PlaceRequestForLocationAPI.builder()
                .adminDistrict("Córdoba")
                .locality("Estación General Paz")
                .build();

        // Given
        String mockedResponse =
                "{\"authenticationResultCode\":\"ValidCredentials\",\"brandLogoUri\":" +
                        "\"https://dev.virtualearth.net/Branding/logo_powered_by.png\"," +
                        "\"copyright\":\"Copyright © 2024 Microsoft and its suppliers. " +
                        "All rights reserved. This API cannot be accessed and the content " +
                        "and any results may not be used, reproduced or transmitted in any manner " +
                        "without express written permission from Microsoft Corporation.\",\"resourceSets" +
                        "\":[{\"estimatedTotal\":1,\"resources\":[{\"__type\":" +
                        "\"Location:http://schemas.microsoft.com/search/local/ws/rest/v1\"," +
                        "\"bbox\":[-31.163447998349,-64.1852046929724,-31.1055072323639,-64.0949771917933]," +
                        "\"name\":\"Estación General Paz, Argentina\",\"point\":{\"type\":\"Point\"," +
                        "\"coordinates\":[-31.13447762,-64.14009094]},\"address\":{\"adminDistrict\":\"CBA\"," +
                        "\"adminDistrict2\":\"Departamento Colón\",\"countryRegion\":\"Argentina\",\"formattedAddress\":" +
                        "\"Estación General Paz, Argentina\",\"locality\":\"Estación General Paz\"},\"confidence\":\"High\"," +
                        "\"entityType\":\"PopulatedPlace\",\"geocodePoints\":[{\"type\":\"Point\",\"coordinates\":[-31.13447762,-64.14009094]," +
                        "\"calculationMethod\":\"Rooftop\",\"usageTypes\":[\"Display\"]}],\"matchCodes\":[\"Good\"]}]}],\"statusCode\":200," +
                        "\"statusDescription\":\"OK\",\"traceId\":\"60cf3e51f6d93a2bc4c66db68cc12e4d|BN00006C35|0.0.0.1|Ref A: " +
                        "C7E8FC16D8B94E2CA741E4B0CFDE12FB Ref B: BN3EDGE1113 Ref C: 2024-03-30T00:36:13Z\"}";

        // When
        mockWebServer.enqueue(
                new MockResponse().setResponseCode(HttpStatus.OK.value())
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(mockedResponse)
        );

        Mono<BingMapApiLocationResponse> result = underTest.getLocationByPlace(request);


        // Then
        StepVerifier.create(result)
                .consumeNextWith(r -> {
                    assertEquals(1, r.getResourceSets().size());
                    assertNotNull(r.getResourceSets().get(0).getResources());
                    assertNotNull(r.getResourceSets().get(0).getResources().get(0));

                    Location location = r.getResourceSets().get(0).getResources().get(0);

                    assertNotNull(location.getAddress());
                    assertNotNull(location.getPoint());

                    // Address
                    assertEquals("Estación General Paz", location.getAddress().getLocality());
                    assertEquals("CBA", location.getAddress().getAdminDistrict());
                    assertEquals("Departamento Colón", location.getAddress().getAdminDistrict2());
                    assertEquals("Argentina", location.getAddress().getCountryRegion());
                    assertEquals("Estación General Paz, Argentina", location.getAddress()
                            .getFormattedAddress());

                    // Point
                    assertEquals(-31.13447762, location.getPoint().getCoordinates()[0], 0.2);
                    assertEquals(-64.14009094, location.getPoint().getCoordinates()[1], 0.2);
                })
                .verifyComplete();
    }

    @Test
    public void getLocation_WhenAddressLineAndPostalAreGiven() {
        PlaceRequestForLocationAPI request = PlaceRequestForLocationAPI.builder()
                .adminDistrict("Córdoba")
                .addressLine("Córdoba")
                .postalCode("X5105AFC")
                .locality("Villa Allende")
                .build();

        // Given
        String mockedResponse =
                "{\"authenticationResultCode\":\"ValidCredentials\",\"brandLogoUri\":\"https:\\/\\" +
                        "/dev.virtualearth.net\\/Branding\\/logo_powered_by.png\",\"copyright\":\"Copyright © " +
                        "2024 Microsoft and its suppliers. All rights reserved. This API cannot be accessed and " +
                        "the content and any results may not be used, reproduced or transmitted in any manner " +
                        "without express written permission from Microsoft Corporation.\",\"resourceSets\":" +
                        "[{\"estimatedTotal\":1,\"resources\":[{\"__type\":\"Location:http:\\/\\" +
                        "/schemas.microsoft.com\\/search\\/local\\/ws\\/rest\\/v1\",\"" +
                        "bbox\":[-31.307943680705442,-64.287536959603273,-31.300218245564089,-64.275481839224852]," +
                        "\"name\":\"X5105AFC, Argentina\",\"point\":{\"type\":\"Point\",\"" +
                        "coordinates\":[-31.30408096,-64.2815094]},\"address\":{\"adminDistrict\":\"CBA\"," +
                        "\"adminDistrict2\":\"Departamento Colón\",\"countryRegion\":\"Argentina\",\"formattedAddress\":" +
                        "\"X5105AFC, Argentina\",\"locality\":\"Villa Allende\",\"postalCode\":\"X5105AFC\"},\"confidence\":" +
                        "\"High\",\"entityType\":\"Postcode1\",\"geocodePoints\":[{\"type\":\"Point\"," +
                        "\"coordinates\":[-31.30408096,-64.2815094],\"calculationMethod\":\"Rooftop\",\"usageTypes\":[\"Display\"]}]," +
                        "\"matchCodes\":[\"Good\"]}]}],\"statusCode\":200,\"statusDescription\":\"OK\",\"traceId\":" +
                        "\"89a37038bd9c66bdd4e87b452cd6bd75|BN00006C79|0.0.0.1|Ref A: 1C5B46FB1BF849E78C782BD76986C802 Ref B: " +
                        "BN3EDGE0211 Ref C: 2024-03-30T19:37:22Z\"}";

        // When
        mockWebServer.enqueue(
                new MockResponse().setResponseCode(HttpStatus.OK.value())
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(mockedResponse)
        );

        Mono<BingMapApiLocationResponse> result = underTest.getLocationByPlace(request);


        // Then
        StepVerifier.create(result)
                .consumeNextWith(r -> {
                    assertEquals(1, r.getResourceSets().size());
                    assertNotNull(r.getResourceSets().get(0).getResources());
                    assertNotNull(r.getResourceSets().get(0).getResources().get(0));

                    Location location = r.getResourceSets().get(0).getResources().get(0);

                    assertNotNull(location.getAddress());
                    assertNotNull(location.getPoint());

                    // Address
                    assertEquals("Villa Allende", location.getAddress().getLocality());
                    assertEquals("CBA", location.getAddress().getAdminDistrict());
                    assertEquals("Departamento Colón", location.getAddress().getAdminDistrict2());
                    assertEquals("Argentina", location.getAddress().getCountryRegion());
                    assertEquals("X5105AFC, Argentina", location.getAddress()
                            .getFormattedAddress());

                    // Point
                    assertEquals(-31.30408096, location.getPoint().getCoordinates()[0], 0.4);
                    assertEquals(-64.2815094, location.getPoint().getCoordinates()[1], 0.4);
                })
                .verifyComplete();
    }
}

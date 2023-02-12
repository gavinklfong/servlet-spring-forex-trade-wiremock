package space.gavinklfong.forex.apiclients;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import space.gavinklfong.forex.dto.ForexRateApiResp;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test of forex rate API client.
 * 
 * Mock: external API is mocked using Mock API server which is initialized 
 * with OpenAPI definition. Example response is the mock response from external API
 * 
 * @author Gavin Fong
 *
 */
@Slf4j
@WireMockTest
@Tag("UnitTest")
class ForexRateApiClientTest {

    @Test
    void getLatestRate(WireMockRuntimeInfo wmRuntimeInfo) {

        stubFor(get("/rates/GBP").willReturn(
                aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("getLatestRatesMockResponse.json")));

        // Initialize API client and trigger request
        ForexRateApiClient forexRateApiClient = new ForexRateApiClient(wmRuntimeInfo.getHttpBaseUrl());
        ForexRateApiResp response = forexRateApiClient.fetchLatestRates("GBP");

        // Assert response
        assertThat(response.getBase()).isEqualTo("GBP");

        Map<String, Double> rates = response.getRates();
        assertThat(rates)
                .hasSize(4)
                .containsKeys("USD", "EUR", "CAD", "JPY");
	}

    @Test
    void getUSDRate(WireMockRuntimeInfo wmRuntimeInfo) {

        stubFor(get("/rates/GBP-USD").willReturn(
                aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("getLatestUSDRateMockResponse.json")));

        // Initialize API client and trigger request
        ForexRateApiClient forexRateApiClient = new ForexRateApiClient(wmRuntimeInfo.getHttpBaseUrl());
        ForexRateApiResp response = forexRateApiClient.fetchLatestRate("GBP", "USD");

        // Assert response
        assertThat(response.getBase()).isEqualTo("GBP");

        Map<String, Double> rates = response.getRates();
        assertThat(rates)
                .hasSize(1)
                .containsKeys("USD")
                .containsEntry("USD", 1.3923701653);
	}
}

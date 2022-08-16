package space.gavinklfong.forex.apiclients;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import space.gavinklfong.forex.dto.ForexRateApiResp;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
@ExtendWith(SpringExtension.class)
@Tag("UnitTest")
public class ForexRateApiClientTest {

    @Test
    public void getLatestRate(WireMockRuntimeInfo wmRuntimeInfo) {

        stubFor(get("/rates/GBP").willReturn(
                aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("getLatestRatesMockResponse.json")));

        // Initialize API client and trigger request
        ForexRateApiClient forexRateApiClient = new ForexRateApiClient(wmRuntimeInfo.getHttpBaseUrl());
        ForexRateApiResp response = forexRateApiClient.fetchLatestRates("GBP");

        // Assert response
        ForexRateApiResp rawData = response;
        assertEquals(rawData.getBase(), "GBP");

        Map<String, Double> rates = rawData.getRates();
		assertEquals(4, rates.size());
		
		assertTrue(rates.containsKey("USD"));
		assertTrue(rates.containsKey("EUR"));
		assertTrue(rates.containsKey("CAD"));
		assertTrue(rates.containsKey("JPY"));

	}

    @Test
    public void getUSDRate(WireMockRuntimeInfo wmRuntimeInfo) {

        stubFor(get("/rates/GBP-USD").willReturn(
                aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("getLatestUSDRateMockResponse.json")));

        // Initialize API client and trigger request
        ForexRateApiClient forexRateApiClient = new ForexRateApiClient(wmRuntimeInfo.getHttpBaseUrl());
        ForexRateApiResp response = forexRateApiClient.fetchLatestRate("GBP", "USD");

        // Assert response
        ForexRateApiResp rawData = response;
        assertEquals(rawData.getBase(), "GBP");

        Map<String, Double> rates = rawData.getRates();
		assertEquals(1, rates.size());
		
		assertTrue(rates.containsKey("USD"));
		assertEquals(1.3923701653, rates.get("USD"));

	}
}

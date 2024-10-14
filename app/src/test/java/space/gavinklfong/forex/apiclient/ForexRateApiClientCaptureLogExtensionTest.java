package space.gavinklfong.forex.apiclient;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import space.gavinklfong.forex.apiclient.dto.ForexRateApiResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
@ExtendWith(OutputCaptureExtension.class)
@Tag("UnitTest")
class ForexRateApiClientCaptureLogExtensionTest {

    private ForexRateApiClient forexRateApiClient;

    @BeforeEach
    void setup(WireMockRuntimeInfo wireMockRuntimeInfo) {
        forexRateApiClient = new ForexRateApiClient(wireMockRuntimeInfo.getHttpBaseUrl());
    }

    @Test
    void getLatestRate() {

        stubFor(get("/rates/GBP").willReturn(
                aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("getLatestRatesMockResponse.json")));

        // Initialize API client and trigger request
        ForexRateApiResponse response = forexRateApiClient.fetchLatestRates("GBP");

        // Assert response
        assertThat(response).returns("GBP", ForexRateApiResponse::getBase);
        assertThat(response.getRates()).hasSize(4)
                .containsOnlyKeys("USD", "EUR", "CAD", "JPY");
	}

    @Test
    void givenGBPUSD_whenGetRateByCurrencyPair_thenReturnRate(CapturedOutput output, WireMockRuntimeInfo wireMockRuntimeInfo) {

        // Setup stub using wiremock
        stubFor(get("/rates/GBP-USD").willReturn(
                aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("getLatestUSDRateMockResponse.json")));

        // Trigger request
        ForexRateApiResponse response = forexRateApiClient.fetchLatestRate("GBP", "USD");

        // Assert response
        assertThat(response).returns("GBP", ForexRateApiResponse::getBase);
        assertThat(response.getRates()).hasSize(1)
                .containsEntry("USD", 1.3923701653);

        assertThat(output.getOut())
                .contains("INFO")
                .contains(String.format("Fetch the latest rate from %s with uri /rates/GBP-USD",
                        wireMockRuntimeInfo.getHttpBaseUrl()));
	}

    @Test
    void givenEmptyBaseCurrency_whenGetRateByCurrencyPair_thenThrowException(CapturedOutput output, WireMockRuntimeInfo wireMockRuntimeInfo) {

        stubFor(get("/rates/GBP-USD").willReturn(
                aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("getLatestUSDRateMockResponse.json")));

        // Initialize API client and trigger request
        assertThatThrownBy(() -> forexRateApiClient.fetchLatestRate("", "USD"))
                .isInstanceOf(IllegalArgumentException.class);

        // Assert response
        assertThat(output.getOut())
                .contains("ERROR")
                .contains("Failed to fetch the latest rate. Either base currency or counter currency is empty");
    }


    @Test
    void givenRateApiReturn500_whenGetUSDRate_thenThrowException(CapturedOutput output) {

        stubFor(get("/rates/GBP-USD").willReturn(
                aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        // Initialize API client and trigger request
        assertThatThrownBy(() ->forexRateApiClient.fetchLatestRate("GBP", "USD"))
                .isInstanceOf(WebClientResponseException.class);
    }
}

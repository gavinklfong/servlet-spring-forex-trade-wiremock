package space.gavinklfong.forex.api.controller;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.api.dto.ApiTradeAction;
import space.gavinklfong.forex.api.dto.ForexRateBookingApiRequest;
import space.gavinklfong.forex.apiclient.ForexRateApiClient;
import space.gavinklfong.forex.domain.model.ForexRateBooking;
import space.gavinklfong.forex.exception.UnknownCustomerException;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Slf4j
@WireMockTest(httpPort = Constants.WIRE_MOCK_PORT)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
@Tag("IntegrationTest")
class ForexRateRestControllerIntegrationTest {

	@Test
	void getLatestRates() throws Exception {

		stubFor(get("/rates/GBP").willReturn(
				aResponse().withStatus(HttpStatus.OK.value())
						.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.withBodyFile("getLatestRatesMockResponse.json")));

		// fire request to retrieve the latest rates and verify the response
		webTestClient.get()
				.uri("/rates/latest/GBP")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$").isArray()
				.jsonPath("$[0].baseCurrency").isEqualTo("GBP")
				.jsonPath("$[0].counterCurrency").isNotEmpty()
				.jsonPath("$[0].buyRate").isNumber()
				.jsonPath("$[0].sellRate").isNumber()
				.jsonPath("$[1].baseCurrency").isEqualTo("GBP")
				.jsonPath("$[1].counterCurrency").isNotEmpty()
				.jsonPath("$[1].buyRate").isNumber()
				.jsonPath("$[1].sellRate").isNumber()
				.jsonPath("$[2].baseCurrency").isEqualTo("GBP")
				.jsonPath("$[2].counterCurrency").isNotEmpty()
				.jsonPath("$[2].buyRate").isNumber()
				.jsonPath("$[2].sellRate").isNumber();

		verify(getRequestedFor(urlEqualTo("/rates/GBP")));
	}

	WebTestClient webTestClient;

	@LocalServerPort
	private int port;

	@BeforeEach
	void setUp() {
		webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
	}

	@Test
	void bookRate() throws UnknownCustomerException {

		stubFor(get("/rates/GBP-USD").willReturn(
				aResponse().withStatus(HttpStatus.OK.value())
						.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.withBodyFile("getLatestUSDRateMockResponse.json")));

		// fire request to book rate and verify the response
		ForexRateBookingApiRequest req = new ForexRateBookingApiRequest()
				.customerId(1L)
				.tradeAction(ApiTradeAction.BUY)
				.baseCurrency("GBP")
				.counterCurrency("USD")
				.baseCurrencyAmount(BigDecimal.valueOf(1000));

		webTestClient.post()
				.uri("/rates/book")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(req), ForexRateBookingApiRequest.class)
				.exchange()
				.expectStatus().isOk()
				.expectBody(ForexRateBooking.class);

		verify(getRequestedFor(urlEqualTo("/rates/GBP-USD")));
	}

	// Configure forex rate api client to point to mock api server
	@TestConfiguration
	static class TestContextConfiguration {
		@Bean
		@Primary
		public ForexRateApiClient initializeForexRateApiClient() {
			return new ForexRateApiClient(String.format("http://localhost:%d", Constants.WIRE_MOCK_PORT));
		}
	}

}

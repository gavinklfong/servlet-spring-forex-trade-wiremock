package space.gavinklfong.forex.api.controller;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.api.dto.*;
import space.gavinklfong.forex.apiclient.ForexRateApiClient;
import space.gavinklfong.forex.exception.ErrorBody;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.lang.String.format;

@Slf4j
@WireMockTest(httpPort = Constants.WIRE_MOCK_PORT)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
@Tag("IntegrationTest")
class ForexTradeDealRestControllerIntegrationTest {
	
	@DisplayName("submitDeal - Success case")
	@Test
	void submitDeal() throws Exception {

		stubFor(get("/rates/GBP-USD").willReturn(
				aResponse().withStatus(HttpStatus.OK.value())
						.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.withBodyFile("getLatestUSDRateMockResponse.json")));

		// fire request to book rate and verify the response
		ForexRateBookingApiRequest bookingReq = new ForexRateBookingApiRequest()
				.customerId(1L)
				.tradeAction(ApiTradeAction.BUY)
				.baseCurrency("GBP")
				.counterCurrency("USD")
				.baseCurrencyAmount(BigDecimal.valueOf(1000));

		EntityExchangeResult<ForexRateBookingApiResponse> result = webTestClient.post()
				.uri("/rates/book")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(bookingReq), ForexRateBookingApiRequest.class)
				.exchange()
				.expectStatus().isOk()
				.expectBody(ForexRateBookingApiResponse.class)
				.returnResult();

		ForexRateBookingApiResponse rateBooking = result.getResponseBody();

		// construct and trigger trade deal request
		ForexTradeDealApiRequest dealReq = new ForexTradeDealApiRequest()
				.tradeAction(rateBooking.getTradeAction())
				.baseCurrency(rateBooking.getBaseCurrency())
				.counterCurrency(rateBooking.getCounterCurrency())
				.rate(rateBooking.getRate())
				.baseCurrencyAmount(rateBooking.getBaseCurrencyAmount())
				.customerId(rateBooking.getCustomerId())
				.rateBookingRef(rateBooking.getBookingRef());

		webTestClient.post()
				.uri("/deals")
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(dealReq), ForexTradeDealApiRequest.class)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody(ForexTradeDealApiResponse.class);

		verify(getRequestedFor(urlEqualTo("/rates/GBP-USD")));
	}

	WebTestClient webTestClient;

	@LocalServerPort
	private int port;

	@BeforeEach
	void setUp() {
		webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
	}

	// Configure forex rate api client to point to mock api server
	@TestConfiguration
	static class TestContextConfiguration {
		@Bean
		@Primary
		ForexRateApiClient initializeForexRateApiClient() {
			return new ForexRateApiClient(String.format("http://localhost:%d", Constants.WIRE_MOCK_PORT));
		}
	}

	@DisplayName("submitDeal - Invalid Req")
	@Test
	void submitDeal_invalidReq() throws Exception {

		// send an empty request
		ForexTradeDealApiRequest req = new ForexTradeDealApiRequest();

		webTestClient.post()
				.uri("/deals")
				.contentType(MediaType.APPLICATION_JSON)
		.body(Mono.just(req), ForexTradeDealApiRequest.class)
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().is4xxClientError()
		.expectBody(ErrorBody.class);
	}
	
	@DisplayName("getDeal - Success case")
	@Test
	void getDeals() throws Exception {

		
		webTestClient.get()
		.uri(uriBuilder -> uriBuilder
				.path("/deals")
				.queryParam("customerId", 1)
				.build()
				)
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk();
	}
	
}

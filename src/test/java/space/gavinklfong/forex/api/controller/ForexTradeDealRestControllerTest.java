package space.gavinklfong.forex.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.api.dto.ApiTradeAction;
import space.gavinklfong.forex.api.dto.ForexTradeDealApiRequest;
import space.gavinklfong.forex.mapper.ApiModelAdapter;
import space.gavinklfong.forex.domain.model.TradeAction;
import space.gavinklfong.forex.exception.ErrorBody;
import space.gavinklfong.forex.domain.model.ForexTradeDeal;
import space.gavinklfong.forex.domain.service.ForexTradeService;
import space.gavinklfong.forex.setup.StubSetup;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@WebMvcTest(ForexTradeDealRestController.class)
@Tag("UnitTest")
class ForexTradeDealRestControllerTest {

	@MockBean
	private ForexTradeService tradeService;

	@Autowired
	WebApplicationContext wac;

	WebTestClient webTestClient;

	private final ApiModelAdapter mapper = Mappers.getMapper(ApiModelAdapter.class);

	@BeforeEach
	void setUp() {
		webTestClient = MockMvcWebTestClient.bindToApplicationContext(this.wac).build();
	}

	@DisplayName("submitDeal - Success case")
	@Test
	void submitDeal() {

		StubSetup.stubForSubmitDeal(tradeService);

		ForexTradeDealApiRequest req = new ForexTradeDealApiRequest()
				.tradeAction(ApiTradeAction.BUY)
				.baseCurrency("GBP")
				.counterCurrency("USD")
				.rate(0.25)
				.baseCurrencyAmount(BigDecimal.valueOf(10000))
				.customerId(1L)
				.rateBookingRef("ABC");
		
		webTestClient.post()
		.uri("/deals")
		.contentType(MediaType.APPLICATION_JSON)
		.body(Mono.just(req), ForexTradeDealApiRequest.class)
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectBody(ForexTradeDeal.class);
	}
	
	@DisplayName("submitDeal - Invalid Req")
	// TODO: disabled this test at the moment before the response content type issue is fixed
//	@Test
	void submitDeal_invalidReq() throws Exception {

		StubSetup.stubForSubmitDeal(tradeService);

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

		ForexTradeDeal deal1 =
				ForexTradeDeal.builder()
						.id(1L).dealRef(UUID.randomUUID().toString())
						.timestamp(Instant.now())
						.baseCurrency("GBP").counterCurrency("USD")
						.rate(Math.random()).baseCurrencyAmount(BigDecimal.valueOf(1000)).customerId(1L)
						.tradeAction(TradeAction.BUY)
						.build();

		ForexTradeDeal deal2 =
				ForexTradeDeal.builder()
						.id(1L).dealRef(UUID.randomUUID().toString())
						.timestamp(Instant.now())
						.baseCurrency("GBP").counterCurrency("USD")
						.rate(Math.random()).baseCurrencyAmount(BigDecimal.valueOf(1000)).customerId(1L)
						.tradeAction(TradeAction.BUY)
						.build();

		ForexTradeDeal deal3 =
				ForexTradeDeal.builder()
						.id(1L).dealRef(UUID.randomUUID().toString())
						.timestamp(Instant.now())
						.baseCurrency("GBP").counterCurrency("USD")
						.rate(Math.random()).baseCurrencyAmount(BigDecimal.valueOf(1000)).customerId(1l)
						.tradeAction(TradeAction.BUY)
						.build();
				
		when(tradeService.retrieveTradeDealByCustomer((anyLong())))
		.thenReturn(Arrays.asList(deal1, deal2, deal3));
		
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

package space.gavinklfong.forex.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;
import reactor.core.publisher.Mono;
import space.gavinklfong.forex.api.dto.ApiResponseErrorBody;
import space.gavinklfong.forex.domain.dto.ForexRateBookingReq;
import space.gavinklfong.forex.domain.model.ForexRateBooking;
import space.gavinklfong.forex.domain.model.TradeAction;
import space.gavinklfong.forex.domain.service.ForexPricingService;
import space.gavinklfong.forex.domain.service.ForexRateService;
import space.gavinklfong.forex.exception.UnknownCustomerException;
import space.gavinklfong.forex.setup.StubSetup;

import java.math.BigDecimal;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

/**
 * Unit test for Rate Rest Controller
 */
@Slf4j
@WebMvcTest(ForexRateRestController.class)
@Tag("UnitTest")
class ForexRateRestControllerTest {

    @MockBean
    ForexRateService rateService;

    @MockBean
    ForexPricingService pricingService;

    @Autowired
    WebApplicationContext wac;

    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = MockMvcWebTestClient.bindToApplicationContext(this.wac).build();
    }

    @Test
    void getLatestRates() {

        // Mock return data of rate service
        StubSetup.stubForGetForexRates(rateService);

        // trigger API request to rate controller
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
    }


    @Test
    void bookRate() throws UnknownCustomerException {

        StubSetup.stubForBookRate(rateService);

        ForexRateBookingReq req = ForexRateBookingReq.builder()
                .customerId(1L)
                .tradeAction(TradeAction.BUY)
                .baseCurrency("GBP")
                .counterCurrency("USD")
                .baseCurrencyAmount(BigDecimal.valueOf(1000))
                .build();

        webTestClient.post()
                .uri("/rates/book")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(req), ForexRateBookingReq.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ForexRateBooking.class);
    }


//    @Test
    // TODO: disabled this test at the moment before the response content type issue is fixed
    public void bookRate_missingParam() throws UnknownCustomerException {

        StubSetup.stubForBookRate(rateService);

        ForexRateBookingReq req = ForexRateBookingReq.builder()
                .customerId(1L)
                .tradeAction(TradeAction.BUY)
//                .baseCurrency("GBP")
                .counterCurrency("USD")
                .baseCurrencyAmount(BigDecimal.valueOf(1000))
                .build();

        webTestClient.post()
                .uri("/rates/book")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(req), ForexRateBookingReq.class)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ApiResponseErrorBody.class);

    }

    @Test
    void bookRate_unknownCustomer() throws UnknownCustomerException {

        when(rateService.obtainBooking((any(ForexRateBookingReq.class))))
                .thenThrow(new UnknownCustomerException());

        ForexRateBookingReq req = ForexRateBookingReq.builder()
                .customerId(1L)
                .tradeAction(TradeAction.BUY)
                .baseCurrency("GBP")
                .counterCurrency("USD")
                .baseCurrencyAmount(BigDecimal.valueOf(1000))
                .build();

        webTestClient.post()
                .uri("/rates/book")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(req), ForexRateBookingReq.class)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ApiResponseErrorBody.class);
    }

}

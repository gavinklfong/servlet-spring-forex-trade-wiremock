package space.gavinklfong.forex.controllers;

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
import space.gavinklfong.forex.dto.ForexRateBookingReq;
import space.gavinklfong.forex.dto.TradeAction;
import space.gavinklfong.forex.exceptions.ErrorBody;
import space.gavinklfong.forex.exceptions.UnknownCustomerException;
import space.gavinklfong.forex.models.ForexRateBooking;
import space.gavinklfong.forex.services.ForexPricingService;
import space.gavinklfong.forex.services.ForexRateService;
import space.gavinklfong.forex.setup.StubSetup;

import java.math.BigDecimal;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

/**
 * Unit test for Rate Rest Controller
 */
@Slf4j
@WebMvcTest(ForexRateBookingRestController.class)
@Tag("UnitTest")
class ForexRateBookingRestControllerTest {

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
        void bookRate() throws UnknownCustomerException {

                StubSetup.stubForBookRate(rateService);

                ForexRateBookingReq req = ForexRateBookingReq.builder()
                                .customerId(1l)
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

        // @Test
        // TODO: disabled this test at the moment before the response content type issue
        // is fixed
        public void bookRate_missingParam() throws UnknownCustomerException {

                StubSetup.stubForBookRate(rateService);

                ForexRateBookingReq req = ForexRateBookingReq.builder()
                                .customerId(1l)
                                .tradeAction(TradeAction.BUY)
                                // .baseCurrency("GBP")
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
                                .expectBody(ErrorBody.class);

        }

        @Test
        public void bookRate_unknownCustomer() throws UnknownCustomerException {

                when(rateService.obtainBooking((any(ForexRateBookingReq.class))))
                                .thenThrow(new UnknownCustomerException());

                ForexRateBookingReq req = ForexRateBookingReq.builder()
                                .customerId(1l)
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
                                .expectBody(ErrorBody.class);
        }

}

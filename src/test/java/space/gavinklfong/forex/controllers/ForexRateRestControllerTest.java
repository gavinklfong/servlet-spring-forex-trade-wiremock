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
        void getLatestRates() throws Exception {

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

}

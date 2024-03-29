package space.gavinklfong.forex.api.controller;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import space.gavinklfong.forex.domain.service.ForexPricingService;
import space.gavinklfong.forex.domain.service.ForexRateService;
import space.gavinklfong.forex.domain.service.ForexTradeService;
import space.gavinklfong.forex.setup.StubSetup;

@WebMvcTest(controllers = {ForexRateRestController.class, ForexTradeDealRestController.class})
@ActiveProfiles("pact")
@Provider("ForexTradeProvider")
@PactFolder("pacts")
class ForexRestApiPactVerificatonTest {

    @MockBean
    ForexRateService rateService;

    @MockBean
    ForexPricingService pricingService;

    @MockBean
    private ForexTradeService tradeService;

    @Autowired
    private MockMvc mockMvc;

    @TestTemplate
    @ExtendWith(PactVerificationSpringProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @BeforeEach
    void beforeEach(PactVerificationContext context) {
        context.setTarget(new MockMvcTestTarget(mockMvc));
    }

    @State("Get Forex Rate")
    void getForexRate() {
        StubSetup.stubForGetForexRate(rateService);
    }

    @State("Get Forex Rates")
    void getForexRates() {
        StubSetup.stubForGetForexRates(rateService);
    }

    @State("Book Forex Rate")
    void bookForexRate() {
        StubSetup.stubForBookRate(rateService);
    }

    @State("Submit Forex Trade Deal")
    void submitForexDeal() {
        StubSetup.stubForSubmitDeal(tradeService);
    }

}

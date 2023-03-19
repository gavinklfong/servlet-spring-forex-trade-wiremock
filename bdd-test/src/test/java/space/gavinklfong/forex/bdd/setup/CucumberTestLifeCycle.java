package space.gavinklfong.forex.bdd.setup;


import com.github.tomakehurst.wiremock.client.WireMock;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import lombok.RequiredArgsConstructor;
import space.gavinklfong.forex.bdd.service.CustomerRepo;
import space.gavinklfong.forex.bdd.service.ForexTradeDealRepo;

import java.net.MalformedURLException;
import java.net.URL;

@RequiredArgsConstructor
public class CucumberTestLifeCycle {

    private final EnvSetup envSetup;

    private final CustomerRepo customerRepo;
    private final ForexTradeDealRepo forexTradeDealRepo;

    @Before
    public void beforeEachScenario() throws MalformedURLException {
        connectToWireMock();
        customerRepo.deleteAll();
        forexTradeDealRepo.deleteAll();
    }

    @After
    public void cleanUp() {
        WireMock.reset();
    }

    private void connectToWireMock() throws MalformedURLException {
        URL wireMockUrl = new URL(envSetup.getWireMockUrl());
        WireMock.configureFor(wireMockUrl.getHost(), wireMockUrl.getPort());
    }
}

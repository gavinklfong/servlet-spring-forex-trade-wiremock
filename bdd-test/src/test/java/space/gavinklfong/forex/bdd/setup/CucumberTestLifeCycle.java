package space.gavinklfong.forex.bdd.setup;


import com.github.tomakehurst.wiremock.client.WireMock;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import lombok.RequiredArgsConstructor;
import space.gavinklfong.forex.bdd.dto.Customer;
import space.gavinklfong.forex.bdd.service.CustomerRepo;

import java.net.MalformedURLException;
import java.net.URL;

@RequiredArgsConstructor
public class CucumberTestLifeCycle {

    private final EnvSetup envSetup;

    private final CustomerRepo customerRepo;

    @BeforeAll
    void setup() throws MalformedURLException {
//        MockApiSetup.start();
        connectToWireMock();
    }

    @Before
    void beforeEachScenario() {
        customerRepo.deleteAll();
    }

    @After
    void cleanUp() {
        WireMock.reset();
    }

    private void connectToWireMock() throws MalformedURLException {
        URL wireMockUrl = new URL(envSetup.getWireMockUrl());
        WireMock.configureFor(wireMockUrl.getHost(), wireMockUrl.getPort());
    }
}

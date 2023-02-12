package space.gavinklfong.forex.bdd;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class MockApiSetup {

    private static final WireMockServer wireMockServer = new WireMockServer(options().port(3000));

    public static void start() {
        wireMockServer.start();
    }

    public static void stop() {
        wireMockServer.stop();
    }

    public static void reset() {
        wireMockServer.resetAll();
    }

    public static String getBaseUrl() {
        return wireMockServer.baseUrl();
    }

}

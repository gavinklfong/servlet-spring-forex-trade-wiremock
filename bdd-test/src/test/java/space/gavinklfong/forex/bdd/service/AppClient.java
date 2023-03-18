package space.gavinklfong.forex.bdd.service;

import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import space.gavinklfong.forex.bdd.setup.EnvSetup;
import space.gavinklfong.forex.bdd.setup.TestContext;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@TestComponent
public class AppClient {

    private final String appUrl;


    public AppClient(EnvSetup envSetup,
                     TestContext testContext) {
        this.appUrl = envSetup.getAppUrl();
    }

    @SneakyThrows
    public HttpResponse<String> getForexRates(String baseCurrency) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(new URI(appUrl + "/rates/latest/" + baseCurrency))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @SneakyThrows
    public HttpResponse<String> bookForexRate(JSONObject requestBody) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(new URI(appUrl + "/rates/book"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }


}

package space.gavinklfong.forex.apiclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import space.gavinklfong.forex.apiclient.dto.ForexRateApiResponse;

@Slf4j
@Component
public class ForexRateApiClient {

    private WebClient webClient;

    private String forexApiUrl;

    @Autowired
    public ForexRateApiClient(@Value("${app.forex-rate-api-url}") String forexApiUrl) {
        this.forexApiUrl = forexApiUrl;
        this.webClient = WebClient.builder().baseUrl(forexApiUrl)
//        		.filter(WebClientFilter.logResponse())
                .build();
    }

    public ForexRateApiResponse fetchLatestRates(String baseCurrency) {

        log.debug("fetchLatestRates() - baseUrl = " + forexApiUrl);

        return webClient.get().uri("/rates/{base}", baseCurrency)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ForexRateApiResponse.class)
                .block();
    }

    public ForexRateApiResponse fetchLatestRate(String baseCurrency, String counterCurrency) {

        log.debug("fetchLatestRates() - baseUrl = " + forexApiUrl);


        return webClient.get().uri("/rates/{base}-{counter}", baseCurrency, counterCurrency)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ForexRateApiResponse.class)
                .block();
    }

}

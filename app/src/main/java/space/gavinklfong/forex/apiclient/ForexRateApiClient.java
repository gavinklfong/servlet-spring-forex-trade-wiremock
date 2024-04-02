package space.gavinklfong.forex.apiclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import space.gavinklfong.forex.apiclient.dto.ForexRateApiResponse;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Slf4j
@Component
public class ForexRateApiClient {
    private final WebClient webClient;
    private final String forexApiUrl;

    @Autowired
    public ForexRateApiClient(@Value("${app.forex-rate-api-url}") String forexApiUrl) {
        this.forexApiUrl = forexApiUrl;
        this.webClient = WebClient.builder().baseUrl(forexApiUrl)
//        		.filter(WebClientFilter.logResponse())
                .build();
    }

    public ForexRateApiResponse fetchLatestRates(String baseCurrency) {

        String uri = String.format("/rates/%s", baseCurrency);

        log.info("fetch latest rate from {} with uri {}", forexApiUrl, uri);

        return webClient.get().uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ForexRateApiResponse.class)
                .block();
    }

    public ForexRateApiResponse fetchLatestRate(String baseCurrency, String counterCurrency) {

        if (isEmpty(baseCurrency) || isEmpty(counterCurrency)) {
            log.error("Failed to fetch the latest rate. Either base currency or counter currency is empty");
            throw new IllegalArgumentException("Invalid currency");
        }

        String uri = String.format("/rates/%s-%s", baseCurrency, counterCurrency);

        log.info("Fetch the latest rate from {} with uri {}", forexApiUrl, uri);

        return webClient.get().uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ForexRateApiResponse.class)
                .block();
    }

}

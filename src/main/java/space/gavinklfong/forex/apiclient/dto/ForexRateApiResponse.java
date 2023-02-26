package space.gavinklfong.forex.apiclient.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class ForexRateApiResponse {

    private String id;

    private Map<String, Double> rates;

    private String base;

    private LocalDate date;

    public ForexRateApiResponse() {
        super();
    }

    public ForexRateApiResponse(String base, LocalDate date, Map<String, Double> rates) {
        super();
        this.base = base;
        this.date = date;
        this.rates = rates;
    }


}

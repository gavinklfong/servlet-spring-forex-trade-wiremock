package space.gavinklfong.forex.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import space.gavinklfong.forex.api.RateApi;
import space.gavinklfong.forex.api.dto.ForexRateApiResponse;
import space.gavinklfong.forex.api.dto.ForexRateBookingApiRequest;
import space.gavinklfong.forex.api.dto.ForexRateBookingApiResponse;
import space.gavinklfong.forex.domain.service.ForexPricingService;
import space.gavinklfong.forex.domain.service.ForexRateService;
import space.gavinklfong.forex.exception.InvalidRequestException;
import space.gavinklfong.forex.mapper.ApiModelAdapter;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
public class ForexRateRestController implements RateApi {

	@Value("${app.default-base-currency}")
	private String defaultBaseCurrency;

	@Autowired
	private ForexRateService rateService;

	@Autowired
	private ForexPricingService pricingService;

	private final ApiModelAdapter mapper = Mappers.getMapper(ApiModelAdapter.class);

	@GetMapping(path = {"base-currencies", "base-currencies/"})
	public ResponseEntity<List<String>> getBaseCurrencies() {
		return ResponseEntity.ok().body(pricingService.findAllBaseCurrencies());
	}

	@Override
	public ResponseEntity<ForexRateApiResponse> getLatestRates(String baseCurrency, String counterCurrency) {
		return ResponseEntity.ok().body(
				mapper.mapModelToApiResponse(rateService.fetchLatestRate(baseCurrency, counterCurrency))
		);
	}

	@Override
	public ResponseEntity<List<ForexRateApiResponse>> getLatestRatesByBaseCurrency(String baseCurrency) {
		return ResponseEntity.ok().body(
				mapper.mapModelToForexRateApiResponse(rateService.fetchLatestRates(baseCurrency))
		);
	}

	@Override
	public ResponseEntity<List<ForexRateApiResponse>> getLatestRatesByDefaultBaseCurrency() throws InvalidRequestException {
		return ResponseEntity.ok().body(
				mapper.mapModelToForexRateApiResponse(rateService.fetchLatestRates(defaultBaseCurrency))
		);
	}

	@Override
	public ResponseEntity<ForexRateBookingApiResponse> bookRate(ForexRateBookingApiRequest req) throws InvalidRequestException {
		return ResponseEntity.ok().body(
				mapper.mapModelToApiResponse(
						rateService.obtainBooking(mapper.mapApiRequestToDto(req))
				)
		);
	}
}

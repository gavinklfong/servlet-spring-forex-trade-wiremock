package space.gavinklfong.forex.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

	private final String defaultBaseCurrency;
	private final ForexRateService rateService;
	private final ForexPricingService pricingService;

	private static final ApiModelAdapter mapper = ApiModelAdapter.INSTANCE;

	public ForexRateRestController(@Value("${app.default-base-currency}") String defaultBaseCurrency,
								   ForexRateService forexRateService,
								   ForexPricingService forexPricingService) {
		this.defaultBaseCurrency = defaultBaseCurrency;
		this.rateService = forexRateService;
		this.pricingService = forexPricingService;
	}

	@Override
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

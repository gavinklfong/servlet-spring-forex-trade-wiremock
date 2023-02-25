package space.gavinklfong.forex.controllers;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import space.gavinklfong.forex.adapters.ApiModelAdapter;
import space.gavinklfong.forex.api.RateApiApi;
import space.gavinklfong.forex.api.dto.ForexRate;
import space.gavinklfong.forex.api.dto.ForexRateBooking;
import space.gavinklfong.forex.api.dto.ForexRateBookingReq;
import space.gavinklfong.forex.exceptions.InvalidRequestException;
import space.gavinklfong.forex.services.ForexPricingService;
import space.gavinklfong.forex.services.ForexRateService;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
public class ForexRateRestController implements RateApiApi {

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
	public ResponseEntity<ForexRate> getLatestRates(String baseCurrency, String counterCurrency) {
		return ResponseEntity.ok().body(
				mapper.mapModelToDto(rateService.fetchLatestRate(baseCurrency, counterCurrency))
		);
	}

	@Override
	public ResponseEntity<List<ForexRate>> getLatestRatesByBaseCurrency(String baseCurrency) {
		return ResponseEntity.ok().body(
				mapper.mapModelToForexRateDtoList(rateService.fetchLatestRates(baseCurrency))
		);
	}

	@Override
	public ResponseEntity<List<ForexRate>> getLatestRatesByDefaultBaseCurrency() throws InvalidRequestException {
		return ResponseEntity.ok().body(
				mapper.mapModelToForexRateDtoList(rateService.fetchLatestRates(defaultBaseCurrency))
		);
	}

	@Override
	public ResponseEntity<ForexRateBooking> bookRate(ForexRateBookingReq req) throws InvalidRequestException {
		return ResponseEntity.ok().body(
				mapper.mapModelToDto(
						rateService.obtainBooking(mapper.mapApiDtoToDto(req))
				)
		);
	}
}

package space.gavinklfong.forex.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import space.gavinklfong.forex.dto.ForexRate;
import space.gavinklfong.forex.dto.ForexRateBookingReq;
import space.gavinklfong.forex.exceptions.InvalidRequestException;
import space.gavinklfong.forex.models.ForexRateBooking;
import space.gavinklfong.forex.services.ForexPricingService;
import space.gavinklfong.forex.services.ForexRateService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/rates")
@Tag(name = "Rate API", description = "Forex rate retrieval and booking")
public class ForexRateRestController {

	@Value("${app.default-base-currency}")
	private String defaultBaseCurrency;

	@Autowired
	private ForexRateService rateService;

	@Autowired
	private ForexPricingService pricingService;

	@GetMapping(path = { "base-currencies" })
	public List<String> getBaseCurrencies() {

		return pricingService.findAllBaseCurrencies();

	}

	@Operation(summary = "Get the latest rates for the specified base currency")
	@Parameters({
			@Parameter(name = "baseCurrency", example = "GBP")
	})
	@GetMapping(path = { "latest", "latest/{optionalBaseCurrency}" }, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ForexRate> getLatestRates(@PathVariable Optional<String> optionalBaseCurrency)
			throws InvalidRequestException {

		String baseCurrency = optionalBaseCurrency.orElse(defaultBaseCurrency);

		return rateService.fetchLatestRates(baseCurrency);

	}

	@Operation(summary = "Get the latest rate for the specified base currency and counter currency")
	@Parameters({
			@Parameter(name = "baseCurrency", example = "GBP"),
			@Parameter(name = "counterCurrency", example = "USD")
	})
	@ApiResponse(responseCode = "200", description = "Exchange Rate", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = ForexRate.class), examples = {
					@ExampleObject(name = "GBP/USD rate response", value = "{"
							+ "\"timestamp\": \"2022-08-28T21:07:07.908Z\", "
							+ "\"baseCurrency\": \"GBP\", "
							+ "\"counterCurrency\": \"USD\", "
							+ "\"buyRate\": 1.25, "
							+ "\"sellRate\": 1.28, "
							+ "\"spread\": 0.03}"),
					@ExampleObject(name = "EUR/USD rate response", value = "{"
							+ "\"timestamp\": \"2022-08-28T21:07:07.908Z\", "
							+ "\"baseCurrency\": \"EUR\", "
							+ "\"counterCurrency\": \"USD\", "
							+ "\"buyRate\": 0.9963, "
							+ "\"sellRate\": 0.9961, "
							+ "\"spread\": 0.02}")
			})
	})
	@GetMapping(path = "latest/{baseCurrency}/{counterCurrency}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ForexRate getLatestRates(@PathVariable String baseCurrency, @PathVariable String counterCurrency)
			throws InvalidRequestException {

		return rateService.fetchLatestRate(baseCurrency, counterCurrency);

	}

}

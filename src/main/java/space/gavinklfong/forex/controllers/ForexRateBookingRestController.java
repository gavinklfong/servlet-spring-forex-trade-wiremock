package space.gavinklfong.forex.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import space.gavinklfong.forex.dto.ForexRateBookingReq;
import space.gavinklfong.forex.exceptions.InvalidRequestException;
import space.gavinklfong.forex.models.ForexRateBooking;
import space.gavinklfong.forex.services.ForexRateService;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin
@RestController
@RequestMapping("/rates")
@Tag(name = "Rate API", description = "Forex rate retrieval and booking")
public class ForexRateBookingRestController {

	@Value("${app.default-base-currency}")
	private String defaultBaseCurrency;

	@Autowired
	private ForexRateService rateService;

	@PostMapping(path = "book", produces = MediaType.APPLICATION_JSON_VALUE)
	public ForexRateBooking bookRate(@Valid @RequestBody ForexRateBookingReq req) throws InvalidRequestException {

		// obtain booking
		return rateService.obtainBooking(req);

	}

}

package space.gavinklfong.forex.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import space.gavinklfong.forex.dto.ForexTradeDealReq;
import space.gavinklfong.forex.exceptions.ErrorBody;
import space.gavinklfong.forex.exceptions.InvalidRateBookingException;
import space.gavinklfong.forex.exceptions.InvalidRequestException;
import space.gavinklfong.forex.exceptions.UnknownCustomerException;
import space.gavinklfong.forex.models.ForexTradeDeal;
import space.gavinklfong.forex.services.ForexTradeService;

import javax.validation.Valid;
import java.util.List;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/deals")
@Tag(name = "Deal API", description = "Forex trade deal retrieval and submission")
public class ForexTradeDealRestController {

	@Autowired
	private ForexTradeService tradeService;

	/**
	 * Expose API for customers to retrieve their forex trade deals
	 * 
	 * API - GET /deals
	 * 
	 * @param customerId - Exception will be thrown if customer id is empty,
	 *                   the exception will be translated to HTTP response with 4xx
	 *                   status
	 * 
	 * @return List of forex trade deal records. The framework formats it into JSON
	 *         format when sending HTTP response
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ForexTradeDeal> getDeals(@RequestParam Long customerId) throws InvalidRequestException {

		if (customerId == null) {
			throw new InvalidRequestException("customerId", "customer Id cannot be blank");
		}

		return tradeService.retrieveTradeDealByCustomer(customerId);
	}

	/**
	 * Expose API for customers to post forex trade deal
	 * 
	 * API - POST /deals
	 * 
	 * @param req - Java bean contains trade deal information.
	 *            '@RequestBody' annotation instructs the framework to convert JSON
	 *            format into Java bean
	 *            while '@Valid' annotation tells the framework to validate the
	 *            object otherwise exception will be thrown
	 *            and send HTTP response with 4xx status back to client
	 * 
	 * 
	 * @return Trade deal object. The framework formats it into JSON format when
	 *         sending HTTP response
	 */
	@Operation(summary = "Submit forex trade deal", description = "This API submits a forex trading request with amount, rate, trade action and "
			+ "a valid booking reference. The booking reference will be validated in order to make sure "
			+ "the reserved rate is not yet expired. Upon completion, this API will return a response with "
			+ "a deal reference which is a unique reference for record query.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Deal has been submitted successfully", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ForexTradeDeal.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid request such as unknown customer, invalid rate booking", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorBody.class)) }),
			@ApiResponse(responseCode = "500", description = "Server internal error", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorBody.class)) })
	})
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ForexTradeDeal submitDeal(@Valid @RequestBody ForexTradeDealReq req)
			throws UnknownCustomerException, InvalidRateBookingException {

		// submit trade deal
		return tradeService.postTradeDeal(req);
	}

}
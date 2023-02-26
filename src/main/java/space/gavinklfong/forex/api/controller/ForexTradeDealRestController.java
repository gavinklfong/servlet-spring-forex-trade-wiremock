package space.gavinklfong.forex.api.controller;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import space.gavinklfong.forex.api.DealApi;
import space.gavinklfong.forex.api.dto.ForexTradeDealApiRequest;
import space.gavinklfong.forex.api.dto.ForexTradeDealApiResponse;
import space.gavinklfong.forex.domain.service.ForexTradeService;
import space.gavinklfong.forex.exception.InvalidRateBookingException;
import space.gavinklfong.forex.exception.InvalidRequestException;
import space.gavinklfong.forex.exception.UnknownCustomerException;
import space.gavinklfong.forex.mapper.ApiModelAdapter;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ForexTradeDealRestController implements DealApi {

	private final ApiModelAdapter mapper = Mappers.getMapper(ApiModelAdapter.class);

	@Autowired
	private ForexTradeService tradeService;
	
	/**
	 * Expose API for customers to retrieve their forex trade deals
	 * 
	 * API - GET /deals
	 * 
	 * @param customerId - Exception will be thrown if customer id is empty,
	 * the exception will be translated to HTTP response with 4xx status
	 * 
	 * @return List of forex trade deal records. The framework formats it into JSON format when sending HTTP response
	 */
	@Override
	public ResponseEntity<List<ForexTradeDealApiResponse>> getDeals(@RequestParam Long customerId) throws InvalidRequestException {
		
		if (customerId == null) {
			throw new InvalidRequestException("customerId", "customer Id cannot be blank");
		}
				
		return ResponseEntity.ok().body(
				mapper.mapModelToForexTradeDealDtoList(tradeService.retrieveTradeDealByCustomer(customerId))
		);
	}

	/**
	 * Expose API for customers to post forex trade deal
	 * 
	 * API - POST /deals
	 * 
	 * @param req - Java bean contains trade deal information.
	 * '@RequestBody' annotation instructs the framework to convert JSON format into Java bean
	 * while '@Valid' annotation tells the framework to validate the object otherwise exception will be thrown
	 * and send HTTP response with 4xx status back to client
	 * 
	 * 
	 * @return Trade deal object. The framework formats it into JSON format when sending HTTP response
	 */
	@Override
	public ResponseEntity<ForexTradeDealApiResponse> submitDeal(@Valid @RequestBody ForexTradeDealApiRequest req) throws UnknownCustomerException, InvalidRateBookingException {
		
		// submit trade deal
		return ResponseEntity.ok().body(
				mapper.mapModelToDto(
						tradeService.postTradeDeal(mapper.mapApiDtoToDto(req))
				)
		);
	}

}
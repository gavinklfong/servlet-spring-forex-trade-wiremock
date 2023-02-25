package space.gavinklfong.forex.controllers;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import space.gavinklfong.forex.adapters.ApiModelAdapter;
import space.gavinklfong.forex.api.DealApiApi;
import space.gavinklfong.forex.api.dto.ForexTradeDeal;
import space.gavinklfong.forex.api.dto.ForexTradeDealReq;
import space.gavinklfong.forex.exceptions.InvalidRateBookingException;
import space.gavinklfong.forex.exceptions.InvalidRequestException;
import space.gavinklfong.forex.exceptions.UnknownCustomerException;
import space.gavinklfong.forex.services.ForexTradeService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ForexTradeDealRestController implements DealApiApi {

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
	public ResponseEntity<List<ForexTradeDeal>> getDeals(@RequestParam Long customerId) throws InvalidRequestException {
		
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
	public ResponseEntity<ForexTradeDeal> submitDeal(@Valid @RequestBody ForexTradeDealReq req) throws UnknownCustomerException, InvalidRateBookingException {
		
		// submit trade deal
		return ResponseEntity.ok().body(
				mapper.mapModelToDto(
						tradeService.postTradeDeal(mapper.mapApiDtoToDto(req))
				)
		);
	}

}
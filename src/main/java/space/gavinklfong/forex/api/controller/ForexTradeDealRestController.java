package space.gavinklfong.forex.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import space.gavinklfong.forex.api.DealApi;
import space.gavinklfong.forex.api.dto.ForexTradeDealApiRequest;
import space.gavinklfong.forex.api.dto.ForexTradeDealApiResponse;
import space.gavinklfong.forex.domain.dto.ForexTradeDealReq;
import space.gavinklfong.forex.domain.model.ForexTradeDeal;
import space.gavinklfong.forex.domain.service.ForexTradeService;
import space.gavinklfong.forex.exception.InvalidRateBookingException;
import space.gavinklfong.forex.exception.InvalidRequestException;
import space.gavinklfong.forex.exception.UnknownCustomerException;
import space.gavinklfong.forex.mapper.ApiModelAdapter;

import java.util.List;

@RestController
public class ForexTradeDealRestController implements DealApi {

	private static final ApiModelAdapter mapper = ApiModelAdapter.INSTANCE;

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
	public ResponseEntity<List<ForexTradeDealApiResponse>> getDeals(Long customerId)
			throws InvalidRequestException {
		
		if (customerId == null) {
			throw new InvalidRequestException("customerId", "customer Id cannot be blank");
		}
				
		return ResponseEntity.ok().body(
				mapper.mapModelToForexTradeDealApiResponse(
						tradeService.retrieveTradeDealByCustomer(customerId)
				)
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
	public ResponseEntity<ForexTradeDealApiResponse> submitDeal(ForexTradeDealApiRequest req)
			throws UnknownCustomerException, InvalidRateBookingException {

		// map API object to domain object
		ForexTradeDealReq request = mapper.mapApiRequestToDto(req);

		// submit request to domain logic
		ForexTradeDeal tradeDeal = tradeService.postTradeDeal(request);

		// map domain object to api object
		return ResponseEntity.ok().body(mapper.mapModelToApiResponse(tradeDeal));
	}

}
package space.gavinklfong.forex.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import space.gavinklfong.forex.domain.dto.ForexTradeDealReq;
import space.gavinklfong.forex.domain.model.Customer;
import space.gavinklfong.forex.domain.model.ForexRateBooking;
import space.gavinklfong.forex.domain.model.ForexTradeDeal;
import space.gavinklfong.forex.domain.repo.CustomerRepo;
import space.gavinklfong.forex.domain.repo.ForexTradeDealRepo;
import space.gavinklfong.forex.exception.InvalidRateBookingException;
import space.gavinklfong.forex.exception.UnknownCustomerException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@Component
public class ForexTradeService {
	private final ForexTradeDealRepo tradeDealRepo;
	private final CustomerRepo customerRepo;
	private final ForexRateService rateService;

	/**
	 * This method process forex trade deal submission. It carries out the following validation:
	 *  1. Check if customer id exists in customer repository
	 *  2. Invoke Rate Service for validation of rate booking
	 *
	 *  If everything is fine, then trade deal record will be assigned a unique deal reference save to repository
	 *
	 * Flow:
	 * Mono<Customer>			[Validate customer existence by retrieving record from repos]
	 * 							[Fire exception if data is empty]
	 * --> Mono<Boolean>		[Validate rate booking and return result as a boolean]
	 * --> Mono<ForexTradeDeal>	[Build and save a new trade deal record]
	 *
	 * @param req - Java bean containing trade deal request
	 * @return Trade Deal Record with unique deal reference wrapped in Mono type
	 */
	public ForexTradeDeal postTradeDeal(ForexTradeDealReq req) {

		Optional<Customer> customer = customerRepo.findById(req.getCustomerId());

		if (customer.isEmpty())
			throw new UnknownCustomerException();

		ForexRateBooking rateBooking = ForexRateBooking.builder()
				.baseCurrency(req.getBaseCurrency())
				.counterCurrency(req.getCounterCurrency())
				.rate(req.getRate())
				.baseCurrencyAmount(req.getBaseCurrencyAmount())
				.bookingRef(req.getRateBookingRef())
				.build();

		if (rateService.validateRateBooking(rateBooking)) {
			// build and save trade deal record
			return tradeDealRepo.save(
					ForexTradeDeal.builder()
							.dealRef(UUID.randomUUID().toString())
							.timestamp(Instant.now())
							.baseCurrency(req.getBaseCurrency())
							.counterCurrency(req.getCounterCurrency())
							.rate(req.getRate())
							.baseCurrencyAmount(req.getBaseCurrencyAmount())
							.customerId(req.getCustomerId())
							.tradeAction(req.getTradeAction())
							.build()
			);
		} else {
			throw new InvalidRateBookingException();
		}
	}
	
	/**
	 * Fetch trade deal by customer id
	 * 
	 * @param customerId 
	 * @return List of trade deal wrapped in Flux data type
	 */
	public List<ForexTradeDeal> retrieveTradeDealByCustomer(Long customerId) {
		return tradeDealRepo.findByCustomerId(customerId);
	}
	
}

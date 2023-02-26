package space.gavinklfong.forex.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import space.gavinklfong.forex.apiclient.ForexRateApiClient;
import space.gavinklfong.forex.apiclient.dto.ForexRateApiResponse;
import space.gavinklfong.forex.domain.dto.ForexPricing;
import space.gavinklfong.forex.domain.dto.ForexRateBookingReq;
import space.gavinklfong.forex.domain.model.Customer;
import space.gavinklfong.forex.domain.model.ForexRate;
import space.gavinklfong.forex.domain.model.ForexRateBooking;
import space.gavinklfong.forex.domain.model.TradeAction;
import space.gavinklfong.forex.domain.repo.CustomerRepo;
import space.gavinklfong.forex.domain.repo.ForexRateBookingRepo;
import space.gavinklfong.forex.exception.InvalidRequestException;
import space.gavinklfong.forex.exception.UnknownCustomerException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringJUnitConfig
@TestPropertySource(properties = {
	    "app.rate-booking-duration=120",
	    "app.default-additional-pip=1"
})
@ContextConfiguration(classes = {ForexRateService.class})
@Tag("UnitTest")
class ForexRateServiceTest {
	
	private static Logger logger = LoggerFactory.getLogger(ForexRateServiceTest.class);
	
	@MockBean
	private ForexRateApiClient forexRateApiClient;
	
	@MockBean
	private CustomerRepo customerRepo;
	
	@MockBean
	private ForexRateBookingRepo rateBookingRepo;
	
	@MockBean
	private ForexPricingService forexPriceService;

	@Autowired
	private ForexRateService rateService;
	
	private static final double ADDITIONAL_PIP = 0.0001;
	
	@Test
	void validateRateBookingTest_validBooking() {
		
		ForexRateBooking mockRecord = new ForexRateBooking("GBP", "USD", 0.25, BigDecimal.valueOf(1000), "ABC");
		mockRecord.setExpiryTime(Instant.now().plus(Duration.ofMinutes(15)));
		when(rateBookingRepo.findByBookingRef(anyString())).thenReturn(Arrays.asList(mockRecord));
		
		ForexRateBooking rateBooking = new ForexRateBooking("GBP", "USD", 0.25, BigDecimal.valueOf(1000), "ABC");
		
		Boolean result = rateService.validateRateBooking(rateBooking);
		assertTrue(result);
	}
	
	@Test
	void validateRateBookingTest_invalidBooking_notFound() {
		
		when(rateBookingRepo.findByBookingRef(anyString())).thenReturn(null);
		
		ForexRateBooking rateBooking = new ForexRateBooking("GBP", "USD", 0.25, BigDecimal.valueOf(1000), "ABC");
		
		Boolean result = rateService.validateRateBooking(rateBooking);
		assertFalse(result);
	}
	
	@Test
	void validateRateBookingTest_invalidBooking_expired() {
		
		ForexRateBooking mockRecord = new ForexRateBooking("GBP", "USD", 0.25, BigDecimal.valueOf(1000), "ABC");
		mockRecord.setExpiryTime(Instant.now().minus(Duration.ofMinutes(15)));
		when(rateBookingRepo.findByBookingRef(anyString())).thenReturn(Arrays.asList(mockRecord));
		
		ForexRateBooking rateBooking = new ForexRateBooking("GBP", "USD", 0.25, BigDecimal.valueOf(1000), "ABC");
		
		Boolean result = rateService.validateRateBooking(rateBooking);
		assertFalse(result);
		
	}
	
	
	@Test
	void fetchLatestRatesTest() {
		
		final double USD_RATE = 1.3868445262;
		final double EUR_RATE = 1.1499540018;

		final double USD_RATE_WITH_PRICE = 1.3868445262 + ADDITIONAL_PIP;
		final double EUR_RATE_WITH_PRICE = 1.1499540018 + ADDITIONAL_PIP;

		
		Map<String, Double> rates = new HashMap<>();
		rates.put("USD", Double.valueOf(USD_RATE));
		rates.put("EUR", Double.valueOf(EUR_RATE));
		ForexRateApiResponse forexRateApiResponse = new ForexRateApiResponse("GBP", LocalDate.now(), rates);
		
		when(forexRateApiClient.fetchLatestRates("GBP"))
		.thenReturn(forexRateApiResponse);

		when(forexPriceService.obtainForexPrice(anyString(), anyString(), anyDouble()))
				.thenAnswer(invocation -> {
					String base = (String)invocation.getArgument(0);
					String counter = (String)invocation.getArgument(1);
					Double rate = (Double)invocation.getArgument(2);
					return ForexRate.builder().
							baseCurrency(base).counterCurrency(counter)
							.buyRate(rate + 2).sellRate(rate + 4).build();
				});

		List<ForexPricing> forexPrices = Arrays.asList(
				ForexPricing.builder().baseCurrency("GBP").counterCurrency("EUR").sellPip(2).buyPip(4).build(),
				ForexPricing.builder().baseCurrency("GBP").counterCurrency("USD").sellPip(1).buyPip(2).build()
		);
		when(forexPriceService.findCounterCurrencies(anyString())).thenReturn(forexPrices);

		List<ForexRate> resp = rateService.fetchLatestRates("GBP");
		assertEquals(2, resp.size());
		ForexRate rate = resp.get(0);
		assertTrue(rate.getBaseCurrency().contentEquals("GBP")
				&& rate.getCounterCurrency().contentEquals("EUR")
				&& rate.getBuyRate() > EUR_RATE
				&& rate.getBuyRate() < rate.getSellRate());
		rate = resp.get(1);
		assertTrue(rate.getBaseCurrency().contentEquals("GBP")
				&& rate.getCounterCurrency().contentEquals("USD")
				&& rate.getBuyRate() > USD_RATE
				&& rate.getBuyRate() < rate.getSellRate());

	}
	
	@Test
	void obtainBookingTest_CustomerTier1() throws JsonProcessingException, UnknownCustomerException {
		
		ForexRateBooking rateBooking = obtainBookingTest(1);
		assertNotNull(rateBooking);
		Instant timestamp = rateBooking.getTimestamp();
		Instant expiryTime = rateBooking.getExpiryTime();
		assertTrue(timestamp.isBefore(expiryTime));
		assertEquals(3 + CustomerRateTier.TIER1.rate, rateBooking.getRate());
		
	}
	
	@Test
	void obtainBookingTest_CustomerTier2() throws JsonProcessingException, UnknownCustomerException {
		
		ForexRateBooking rateBooking = obtainBookingTest(2);
		assertNotNull(rateBooking);
		Instant timestamp = rateBooking.getTimestamp();
		Instant expiryTime = rateBooking.getExpiryTime();
		assertTrue(timestamp.isBefore(expiryTime));
		assertEquals(3 + CustomerRateTier.TIER2.rate, rateBooking.getRate());
	}
	
	@Test
	void obtainBookingTest_CustomerTier3() throws JsonProcessingException, UnknownCustomerException {
		
		ForexRateBooking rateBooking = obtainBookingTest(3);
		assertNotNull(rateBooking);
		Instant timestamp = rateBooking.getTimestamp();
		Instant expiryTime = rateBooking.getExpiryTime();
		assertTrue(timestamp.isBefore(expiryTime));
		assertEquals(3 + CustomerRateTier.TIER3.rate, rateBooking.getRate());
		
	}
	
	@Test
	void obtainBookingTest_CustomerTier4() throws JsonProcessingException, UnknownCustomerException {
		
		// Test rate booking for customer with tier 4 account
		ForexRateBooking rateBooking = obtainBookingTest(4);
		
		// Assert result
		assertNotNull(rateBooking);

		Instant timestamp = rateBooking.getTimestamp();
		Instant expiryTime = rateBooking.getExpiryTime();
		assertTrue(timestamp.isBefore(expiryTime));
		
		assertEquals(3 + CustomerRateTier.TIER4.rate, rateBooking.getRate());
	}


	private ForexRateBooking obtainBookingTest(Integer tier) throws JsonProcessingException, InvalidRequestException {

		// Forex API client returns 1 when fetchLatestRates() is invoked
		when(forexRateApiClient.fetchLatestRate(anyString(), anyString()))
				.thenAnswer(invocation -> {
					Map<String, Double> rates = new HashMap<>();
					rates.put((String)invocation.getArgument(1), 1d);
					return new ForexRateApiResponse((String)invocation.getArgument(0), LocalDate.now(), rates);
				});
		
		// Customer Repo return a mock customer record when findById() is invoked
		when(customerRepo.findById(anyLong()))
		.thenReturn(Optional.of(new Customer(1l, "Tester 1", tier)));
		
		// Rate Booking Repo return a mock return when save() is invoked
		when(rateBookingRepo.save(any(ForexRateBooking.class)))
		.thenAnswer(invocation -> {
			ForexRateBooking record = (ForexRateBooking)invocation.getArgument(0);
			record.setId((long)Math.random() * 10 + 1);
			return record;
		});

		when(forexPriceService.obtainForexPrice(anyString(), anyString(), anyDouble()))
				.thenAnswer(invocation -> {
					String base = (String)invocation.getArgument(0);
					String counter = (String)invocation.getArgument(1);
					Double rate = (Double)invocation.getArgument(2);
					return ForexRate.builder().
							baseCurrency(base).counterCurrency(counter)
							.buyRate(rate + 2).sellRate(rate + 4).build();
				});

		when(forexPriceService.findCounterCurrencies("GBP")).thenReturn(
				Arrays.asList(
						ForexPricing.builder()
						.baseCurrency("GBP")
						.counterCurrency("USD")
						.sellPip(1)
						.buyPip(2)
						.build()
				)
		);

		// Create a request to test obtainBooking()
		ForexRateBookingReq request = ForexRateBookingReq.builder()
				.baseCurrency("GBP").counterCurrency("USD")
				.baseCurrencyAmount(BigDecimal.valueOf(1000))
				.customerId(1l).tradeAction(TradeAction.BUY)
				.build();

		return rateService.obtainBooking(request);
	}
}

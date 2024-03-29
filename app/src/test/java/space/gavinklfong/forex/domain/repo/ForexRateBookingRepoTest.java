package space.gavinklfong.forex.domain.repo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import space.gavinklfong.forex.domain.model.ForexRateBooking;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@Sql({"/data-unittest.sql"})
@Tag("UnitTest")
class ForexRateBookingRepoTest {

    private static final Logger logger = LoggerFactory.getLogger(ForexRateBookingRepoTest.class);	
	
	@Autowired
	private ForexRateBookingRepo rateBookingRepo;
	
	@DisplayName("save rate booking")
	@Test
	void testSave() {
		
		UUID uuid = UUID.randomUUID();
		
		ForexRateBooking rate = new ForexRateBooking();
		rate.setBaseCurrency("GBP");
		rate.setCounterCurrency("USD");
		rate.setTimestamp(Instant.now());
		rate.setRate(Double.valueOf(2.25));
		rate.setExpiryTime(Instant.now().plus(Duration.ofMinutes(10)));
		rate.setBookingRef(uuid.toString());
		rate.setCustomerId(1l);

		rate = rateBookingRepo.save(rate);
		
		assertNotNull(rate.getId());
		
		
	}
	
	@DisplayName("find all rate booking")
	@Test
	void testFindAll() {
		
		UUID uuid = UUID.randomUUID();
		
		
		ForexRateBooking bookingOriginal = new ForexRateBooking();
		bookingOriginal.setBaseCurrency("GBP");
		bookingOriginal.setCounterCurrency("USD");
		bookingOriginal.setTimestamp(Instant.now());
		bookingOriginal.setRate(Double.valueOf(2.25));
		bookingOriginal.setExpiryTime(Instant.now().plus(Duration.ofMinutes(10)));
		bookingOriginal.setBookingRef(uuid.toString());
		
		bookingOriginal = rateBookingRepo.save(bookingOriginal);
		
		Iterable<ForexRateBooking> bookings = rateBookingRepo.findAll();
		
		int count = 0;

		Iterator<ForexRateBooking> it = bookings.iterator();
		while (it.hasNext()) {
			count++;
			it.next();
		}
		
		assertTrue(count > 0);
	}	
	
	@DisplayName("find by booking ref")
	@Test
	void findByBookingRef_withRecord() {

		List<ForexRateBooking> bookings = rateBookingRepo.findByBookingRef("BOOKING-REF-01");
		
		assertNotNull(bookings);
		assertEquals(1, bookings.size());
		
		ForexRateBooking booking = bookings.get(0);
		assertEquals("2021-02-01T11:50:00Z", DateTimeFormatter.ISO_INSTANT.format(booking.getTimestamp()));
		assertEquals("2021-02-01T12:10:00Z", DateTimeFormatter.ISO_INSTANT.format(booking.getExpiryTime()));
		assertEquals("GBP", booking.getBaseCurrency());
		assertEquals("USD", booking.getCounterCurrency());
		assertEquals(1000d, booking.getBaseCurrencyAmount().doubleValue());	
	}
	
	@DisplayName("find by booking ref - no record")
	@Test
	void findByBookingRef_noRecord() {

		List<ForexRateBooking> bookings = rateBookingRepo.findByBookingRef("BOOKING-REF-02");
		
		assertNotNull(bookings);
		assertEquals(0, bookings.size());

	}
	
}

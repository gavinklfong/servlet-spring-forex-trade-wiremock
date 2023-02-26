package space.gavinklfong.forex.domain.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import space.gavinklfong.forex.domain.model.ForexRateBooking;

import java.util.List;

@Repository
public interface ForexRateBookingRepo extends CrudRepository <ForexRateBooking, Long>{

	/**
	 * Retrieve rate booking record by booking ref
	 *
	 * @param customerId
	 * @return
	 */
	List<ForexRateBooking> findByBookingRef(String bookingRef);

}

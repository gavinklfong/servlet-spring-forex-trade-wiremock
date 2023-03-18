package space.gavinklfong.forex.domain.repo;

import org.springframework.data.repository.CrudRepository;
import space.gavinklfong.forex.domain.model.ForexTradeDeal;

import java.util.List;

public interface ForexTradeDealRepo extends CrudRepository<ForexTradeDeal, Long> {

	/**
	 * Retrieve list of trade deal record by customer id
	 * 
	 * @param customerId
	 * @return
	 */
	List<ForexTradeDeal> findByCustomerId(Long customerId);
}

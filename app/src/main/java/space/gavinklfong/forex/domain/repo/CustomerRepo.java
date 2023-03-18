package space.gavinklfong.forex.domain.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import space.gavinklfong.forex.domain.model.Customer;

@Repository
public interface CustomerRepo extends CrudRepository<Customer, Long> {

}

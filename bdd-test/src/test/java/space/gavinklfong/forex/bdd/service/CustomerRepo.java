package space.gavinklfong.forex.bdd.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import space.gavinklfong.forex.bdd.dto.Customer;

@Repository
public interface CustomerRepo extends CrudRepository<Customer, Long> {

}

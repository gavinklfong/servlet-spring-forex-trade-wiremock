package space.gavinklfong.forex.bdd.service;

import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.springframework.boot.test.context.TestComponent;
import space.gavinklfong.forex.bdd.dto.Customer;

import javax.sql.DataSource;
import java.util.Map;

@TestComponent
public class CustomerRepo {

    private final FluentJdbc fluentJdbc;

    public CustomerRepo(DataSource dataSource) {
        fluentJdbc = new FluentJdbcBuilder()
                .connectionProvider(dataSource)
                .build();
    }

    public void insert(Customer customer) {
        fluentJdbc.query()
                .update("INSERT INTO customer(ID, NAME, TIER) VALUES (:id, :name, :tier)")
                .namedParams(Map.of("id", customer.getId(),
                        "name", customer.getName(),
                        "tier", customer.getTier()))
                .run();
    }

    public void deleteAll() {
        fluentJdbc.query()
                .update("DELETE FROM customer")
                .run();
    }

}

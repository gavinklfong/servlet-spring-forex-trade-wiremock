package space.gavinklfong.forex.bdd.service;

import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.springframework.boot.test.context.TestComponent;
import space.gavinklfong.forex.bdd.dto.ForexTradeDeal;

import javax.sql.DataSource;
import java.util.Map;

@TestComponent
public class ForexTradeDealRepo {

    private final FluentJdbc fluentJdbc;

    public ForexTradeDealRepo(DataSource dataSource) {
        fluentJdbc = new FluentJdbcBuilder()
                .connectionProvider(dataSource)
                .build();
    }

    public void insert(ForexTradeDeal forexTradeDeal) {
        fluentJdbc.query()
                .update("INSERT INTO forex_trade_deal (ID, base_currency, counter_currency, base_currency_amount, "
                         + "customer_id, deal_ref, rate, timestamp, trade_action) VALUES ( "
                         + ":id, :base_currency, :counter_currency, :base_currency_amount, :customer_id, :deal_ref, "
                         + ":rate, :timestamp, :trade_action)")
                .namedParams(Map.of("id", forexTradeDeal.getId(),
                        "base_currency", forexTradeDeal.getBaseCurrency(),
                        "counter_currency", forexTradeDeal.getCounterCurrency(),
                        "base_currency_amount", forexTradeDeal.getBaseCurrencyAmount(),
                        "customer_id", forexTradeDeal.getCustomerId(),
                        "deal_ref", forexTradeDeal.getDealRef(),
                        "rate", forexTradeDeal.getRate(),
                        "timestamp", forexTradeDeal.getTimestamp(),
                        "trade_action", forexTradeDeal.getTradeAction()
                        )
                )
                .run();
    }

    public void deleteAll() {
        fluentJdbc.query()
                .update("DELETE FROM forex_trade_deal")
                .run();
    }

}

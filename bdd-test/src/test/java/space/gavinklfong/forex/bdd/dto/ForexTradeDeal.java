package space.gavinklfong.forex.bdd.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;

@Value
@Builder
public class ForexTradeDeal {

	Long id;
	String dealRef;
	Instant timestamp;
	String baseCurrency;
	String counterCurrency;
	Double rate;
	String tradeAction;
	BigDecimal baseCurrencyAmount;
	Long customerId;

}

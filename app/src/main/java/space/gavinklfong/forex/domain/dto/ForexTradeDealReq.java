package space.gavinklfong.forex.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import space.gavinklfong.forex.domain.model.TradeAction;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForexTradeDealReq {

	@NotEmpty
	private String baseCurrency;

	@NotEmpty
	private String counterCurrency;

	@NotNull
	@Positive
	private Double rate;

	@NotNull
	private TradeAction tradeAction;

	@NotNull
	@Positive
	private BigDecimal baseCurrencyAmount;

	@NotNull
	private Long customerId;

	@NotEmpty
	private String rateBookingRef;

}

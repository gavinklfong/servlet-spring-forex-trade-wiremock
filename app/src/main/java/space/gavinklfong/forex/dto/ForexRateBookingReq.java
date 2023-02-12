package space.gavinklfong.forex.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForexRateBookingReq {

	@NotEmpty
	private String baseCurrency;

	@NotEmpty
	private String counterCurrency;

	@NotNull
	@Positive
	private BigDecimal baseCurrencyAmount;

	@NotNull
	private TradeAction tradeAction;

	@NotNull
	private Long customerId;


}

package space.gavinklfong.forex.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForexTradeDealReq {

	@Schema(example = "GBP")
	@NotEmpty
	private String baseCurrency;

	@Schema(example = "USD")
	@NotEmpty
	private String counterCurrency;

	@Schema(example = "1.25")
	@NotNull
	@Positive
	private Double rate;

	@NotNull
	private TradeAction tradeAction;

	@Schema(example = "1000")
	@NotNull
	@Positive
	private BigDecimal baseCurrencyAmount;

	@Schema(example = "1")
	@NotNull
	private Long customerId;

	@Schema(example = "002c0ed8-2208-4d71-af3f-333ebc867eea")
	@NotEmpty
	private String rateBookingRef;

}

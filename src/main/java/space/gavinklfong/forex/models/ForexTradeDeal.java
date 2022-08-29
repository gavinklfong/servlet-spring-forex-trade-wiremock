package space.gavinklfong.forex.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import space.gavinklfong.forex.dto.TradeAction;

import javax.persistence.*;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "forex_trade_deal")
public class ForexTradeDeal {

	@Schema(example = "230")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Schema(example = "6b19159b-3ff6-42cb-a6a0-cab57da8392a")
	@Column(unique = true)
	private String dealRef;

	private Instant timestamp;

	@Schema(example = "GBP")
	private String baseCurrency;

	@Schema(example = "USD")
	private String counterCurrency;

	@Schema(example = "1.25")
	private Double rate;

	@Schema(example = "BUY")
	private TradeAction tradeAction;

	@Schema(example = "1000")
	private BigDecimal baseCurrencyAmount;

	@Schema(example = "1")
	private Long customerId;

}

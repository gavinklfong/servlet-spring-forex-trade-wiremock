package space.gavinklfong.forex.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import space.gavinklfong.forex.dto.TradeAction;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="forex_trade_deal")
public class ForexTradeDeal {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true)
	private String dealRef;
	
	private Instant timestamp;
		
	private String baseCurrency;
	
	private String counterCurrency;
	
	private Double rate;

	private TradeAction tradeAction;

	private BigDecimal baseCurrencyAmount;

	private Long customerId;

}

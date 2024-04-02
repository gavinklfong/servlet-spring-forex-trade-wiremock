package space.gavinklfong.forex.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "forex_rate_booking")
public class ForexRateBooking {

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Instant timestamp;
	private String baseCurrency;
	private String counterCurrency;
	private Double rate;

	@Enumerated(EnumType.STRING)
	private TradeAction tradeAction;
	private BigDecimal baseCurrencyAmount;
	
	@Column(unique = true)
	private String bookingRef;
	
	private Instant expiryTime;

	private Long customerId;

	public ForexRateBooking(Long id, Instant timestamp, String baseCurrency, String counterCurrency, Double rate,
							String bookingRef, Instant expiryTime, Long customerId) {
		this.id = id;
		this.timestamp = timestamp;
		this.baseCurrency = baseCurrency;
		this.counterCurrency = counterCurrency;
		this.rate = rate;
		this.bookingRef = bookingRef;
		this.expiryTime = expiryTime;
		this.customerId = customerId;
	}
}

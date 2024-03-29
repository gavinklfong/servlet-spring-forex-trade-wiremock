package space.gavinklfong.forex.domain.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.Instant;

@Data
@NoArgsConstructor
@Builder
public class ForexRate {

	private Instant timestamp;
	private String baseCurrency;
	private String counterCurrency;

	@With
	private Double buyRate;

	@With
	private Double sellRate;

	@JsonGetter
	public Double getSpread() {
		return Math.round(Math.abs(buyRate - sellRate) * 10000d) / 10000d;
	}

	public ForexRate(Instant timestamp, String baseCurrency, String counterCurrecy, Double buyRate, Double sellRate) {
		this.timestamp = timestamp;
		this.baseCurrency = baseCurrency;
		this.counterCurrency = counterCurrecy;
		this.buyRate = buyRate;
		this.sellRate = sellRate;
	}

}
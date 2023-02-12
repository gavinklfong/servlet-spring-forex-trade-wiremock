package space.gavinklfong.forex.config;

import com.opencsv.exceptions.CsvValidationException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import space.gavinklfong.forex.services.ForexPricingService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class AppConfig {

	@Bean
	public ForexPricingService initializeForexRateSpreadRepo() throws CsvValidationException, IOException {
		Resource resource = new ClassPathResource("/pricing.csv");
		return new ForexPricingService(resource.getInputStream());
	}

	@Bean
	@ConfigurationProperties(prefix = "app.filters.basic-auth-filter")
	public Map<String, String> apiCredentials() {
		return new HashMap<>();
	}
}

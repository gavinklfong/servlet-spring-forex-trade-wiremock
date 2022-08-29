package space.gavinklfong.forex.config;

import com.opencsv.exceptions.CsvValidationException;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import space.gavinklfong.forex.services.ForexPricingService;

import java.io.IOException;

@Configuration
public class AppConfig {

	@Bean
	public ForexPricingService initializeForexRateSpreadRepo() throws CsvValidationException, IOException {
		Resource resource = new ClassPathResource("/pricing.csv");
		return new ForexPricingService(resource.getInputStream());
	}

	@Bean
	public OpenAPI forexAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Forex API")
						.description("This is a sample API for the demonstration of foreign exchange trading.")
						.version("v0.1"))
				.externalDocs(new ExternalDocumentation()
						.description("Forex API GitHub")
						.url("https://github.com/gavinklfong/servlet-spring-forex-trade-wiremock"));
	}

}

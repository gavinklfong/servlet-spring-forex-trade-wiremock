package space.gavinklfong.forex.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

public class WebClientFilter {
	
	private static Logger logger = LoggerFactory.getLogger(WebClientFilter.class);
	
	public static ExchangeFilterFunction logResponse() {
		return ExchangeFilterFunction.ofResponseProcessor(response -> {
			logStatus(response);
			logHeaders(response);
			
			return logBody(response);
		});
	}
	

	private static void logStatus(ClientResponse response) {
		logger.debug(String.format("Returned status code {} ({})", response.statusCode().value(), response.statusCode()));
	}
	
	
	private static Mono<ClientResponse> logBody(ClientResponse response) {
		return response.bodyToMono(String.class)
				.flatMap(body -> {
					logger.debug("Body is {}", body);						
					return Mono.just(response);
				});
	}
	
	
	private static void logHeaders(ClientResponse response) {
		response.headers().asHttpHeaders().forEach((name, values) -> {
			values.forEach(value -> {
				logger.debug(name + " = " + value);
			});
		});
	}
}

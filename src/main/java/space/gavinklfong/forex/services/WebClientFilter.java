package space.gavinklfong.forex.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

@Slf4j
public class WebClientFilter {

	public static ExchangeFilterFunction logResponse() {
		return ExchangeFilterFunction.ofResponseProcessor(response -> {
			logStatus(response);
			logHeaders(response);
			
			return logBody(response);
		});
	}
	

	private static void logStatus(ClientResponse response) {
		log.debug("Returned status code {} ({})", response.statusCode().value(), response.statusCode());
	}
	
	
	private static Mono<ClientResponse> logBody(ClientResponse response) {
		return response.bodyToMono(String.class)
				.flatMap(body -> {
					log.debug("Body is {}", body);
					return Mono.just(response);
				});
	}
	
	
	private static void logHeaders(ClientResponse response) {
		response.headers().asHttpHeaders().forEach((name, values) ->
			values.forEach(value -> log.debug("{} = {}", name, value))
		);
	}
}

package br.com.thomas.wex.challenge.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Thomas J. Mazon de Oiveira
 */
@Configuration
public class WebClientConfig {
	
	@Value("${app.external-api.treasury-fiscaldata.base-url}")
	private String treasuryRatesExchangeBaseUrl;
	
	@Bean(name = "webClientTreasuryRatesExchange")
	public WebClient webClientTreasuryRatesExchange(WebClient.Builder builder) {
		return builder.baseUrl(treasuryRatesExchangeBaseUrl	)
		    .exchangeStrategies(ExchangeStrategies.builder()
		            .codecs(configurer -> configurer
		                    .defaultCodecs()
		                    .maxInMemorySize(16 * 1024 * 1024))
		            .build())
		    .build();
	}
}

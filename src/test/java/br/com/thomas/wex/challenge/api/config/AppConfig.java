package br.com.thomas.wex.challenge.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


/**
 * @author Thomas J. Mazon de Oiveira
 */
@Configuration
public class AppConfig {
	
	
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
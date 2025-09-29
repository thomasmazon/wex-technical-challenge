package br.com.thomas.wex.challenge.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;

/**
 * @author Thomas J. Mazon de Oiveira
 */
@SpringBootApplication
@EnableScheduling	
@EnableAutoConfiguration
@ComponentScan("br.com.thomas.wex")
public class PurchaseAppApplication {

    
    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder b = new Jackson2ObjectMapperBuilder();
        b.propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        return b;
    }

	public static void main(String[] args) {
		SpringApplication.run(PurchaseAppApplication.class, args);
	}

}

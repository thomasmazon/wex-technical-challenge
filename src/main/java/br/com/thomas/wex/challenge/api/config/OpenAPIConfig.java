package br.com.thomas.wex.challenge.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * @author Thomas J. Mazon de Oiveira
 */
@Configuration
public class OpenAPIConfig {

	@Bean
    public OpenAPI customOpenAPI(@Value("${application-description}") String appDesciption,
            @Value("${application-version}") String appVersion) {
		
        // @formatter:off
        return new OpenAPI().components(
            new Components()
                .addSecuritySchemes("token", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                 )
        ).info(
            new Info()
                .title(appDesciption)
                .version(appVersion)
                .description(appDesciption)
                .termsOfService("http://swagger.io/terms/")
                .license(new License().name("Apache 2.0").url("http://springdoc.org")));
        // @formatter:on
    }
}

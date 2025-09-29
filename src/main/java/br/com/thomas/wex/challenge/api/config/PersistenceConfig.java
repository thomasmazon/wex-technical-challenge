package br.com.thomas.wex.challenge.api.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * @author Thomas J. Mazon de Oiveira
 */
@Configuration
@EnableTransactionManagement
@EnableJpaAuditing()
@EnableJpaRepositories(basePackages = { "br.com.thomas.wex.*" })
@EntityScan(basePackages = { "br.com.thomas.wex.*" })
public class PersistenceConfig {
	
	@Autowired
	DataSource datasource;
	
	
	@Bean
    public JdbcTemplate jdbcTemplate() {

        var template = new JdbcTemplate();
        template.setDataSource(datasource);

        return template;
    }
}

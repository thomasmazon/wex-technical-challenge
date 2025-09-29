package br.com.thomas.wex.challenge.api.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.hazelcast.collection.IList;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import br.com.thomas.wex.challenge.api.service.dto.ExchangeRateDto;
import br.com.thomas.wex.challenge.api.service.dto.ExchangeRateResponse;
import reactor.core.publisher.Mono;

/**
 * 
 * @author Thomas J. Mazon de Oiveira
 */
@Component
public class TreasureService {
	
	String RATES_OF_EXCHANGE_ENDPOINT = "/services/api/fiscal_service/v1/accounting/od/rates_of_exchange";
	DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Autowired
	@Qualifier("webClientTreasuryRatesExchange")
	WebClient webClient;
	
	@Autowired
	HazelcastInstance hazelcastInstance;
	
	@Autowired
	IMap<String, ExchangeRateDto> treasureExchangeRatesMap;
	
	@Autowired
	IList<String> treasureCountryList;
	
	public boolean validCountry(String country) {
		return treasureCountryList.contains(country);
	}
	
	
	public ExchangeRateDto getExchangeRateByCountryAndDate(String country, LocalDate date) {

		LocalDate sixMonthsAgo = date.minusMonths(6);
		
		String startDateStr = date.format(DATE_FORMAT);
		String endDateStr = sixMonthsAgo.format(DATE_FORMAT);
		
		String key = country+"_"+startDateStr;
		
		if (!treasureExchangeRatesMap.containsKey(key)) {
			List<ExchangeRateDto> rates = fetchRatesPerCountry(country, startDateStr, endDateStr);
			if (!rates.isEmpty()) {
				treasureExchangeRatesMap.put(key, rates.get(0));
			}
		}
		return treasureExchangeRatesMap.get(key);
	}
	
	
	public IList<String> getCoutries() throws Exception {
		return treasureCountryList;
	}
	
	
	public List<ExchangeRateDto> fetchRatesPerCountry(String country, String startDateStr, String endDateStr) {
		Mono<ExchangeRateResponse> response = webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path(RATES_OF_EXCHANGE_ENDPOINT)
						.queryParam("filter", "country:eq:" + country + ",record_date:lte:" + startDateStr + ",record_date:gte:" + endDateStr)
						.queryParam("sort", "-record_date")
                        .queryParam("page[size]", "1")
						.build())
				.retrieve()
				.bodyToMono(ExchangeRateResponse.class);
		
		return response.block().getData();
		
			
		
		
	}
	public void syncCountryNames() {

        Mono<ExchangeRateResponse> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(RATES_OF_EXCHANGE_ENDPOINT)
//                        .queryParam("fields", "country")
                        .queryParam("sort", "country")
                        .build())
                .retrieve()
                .bodyToMono(ExchangeRateResponse.class);
        
        List<ExchangeRateDto> excangeRates = response.block().getData();
        treasureCountryList.addAll(
        		excangeRates.stream()
        		.map(ExchangeRateDto::getCountry)
        		.toList()
        	);
	}
	
	


}

package br.com.thomas.wex.challenge.api.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.collection.IList;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import br.com.thomas.wex.challenge.api.listener.CurrencyMapListener;
import br.com.thomas.wex.challenge.api.service.dto.ExchangeRateDto;

/**
 * @author Thomas J. Mazon de Oiveira
 */
@Configuration
public class HazelcastConfig {
	
	@Bean
	public Config hazelcastConfig() {
		Config config = new Config();
		config.setInstanceName("wex-hazelcast-instance");
		
		NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.getJoin().getMulticastConfig().setEnabled(true);
        networkConfig.getJoin().getTcpIpConfig().setEnabled(false);
        networkConfig.setPort(5701);
		
		MapConfig mapConfig = new MapConfig();
        mapConfig.setName("treasure-exchange-rates"); 
        mapConfig.setTimeToLiveSeconds(2000);
//        mapConfig.setMaxSizeConfig(new MaxSizeConfig(200, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE));
//        mapConfig.setEvictionPolicy(EvictionPolicy.LRU);
        config.addMapConfig(mapConfig);
		
		return config;
	}
	
	@Bean
	public HazelcastInstance getHazelcastInstance() {
		
		HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(hazelcastConfig());
		
		return hazelcastInstance;
	}
	
	@Bean
	public IMap<String, ExchangeRateDto> treasureExchangeRatesMap(HazelcastInstance hazelcastInstance) {
		IMap<String, ExchangeRateDto> exchangeRatesMap =  hazelcastInstance.getMap("treasure-exchange-rates");
		exchangeRatesMap.addEntryListener( new CurrencyMapListener(), true );
		
		return exchangeRatesMap;
		
	}
	
	@Bean
	public IList<String> treasureCountryList(HazelcastInstance hazelcastInstance) {
		IList<String> countries =  hazelcastInstance.getList("treasure-country");
		
		return countries;
		
	}
	

}

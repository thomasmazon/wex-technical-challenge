package br.com.thomas.wex.challenge.api.listener;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.MapEvent;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.map.listener.MapClearedListener;
import com.hazelcast.map.listener.MapEvictedListener;

import br.com.thomas.wex.challenge.api.service.dto.ExchangeRateDto;

/**
 * @author Thomas J. Mazon de Oiveira
 */
public class CurrencyMapListener implements 
					EntryAddedListener<String, ExchangeRateDto>, 
					EntryRemovedListener<String, ExchangeRateDto>, 
					EntryUpdatedListener<String, ExchangeRateDto>, 
					EntryEvictedListener<String, ExchangeRateDto>, 
					MapEvictedListener, 
					MapClearedListener {
	
	ObjectMapper mapper = null;
	public CurrencyMapListener() {
		mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
	}
	@Override
	public void entryAdded(EntryEvent<String, ExchangeRateDto> event) {
//		System.out.println("Entry Added: " + event);
	}

	@Override
	public void entryRemoved(EntryEvent<String, ExchangeRateDto> event) {
//		System.out.println("Entry Removed:" + event);
	}

	@Override
	public void entryUpdated(EntryEvent<String, ExchangeRateDto> event) {
//		System.out.println("Entry Updated:" + event);
	}

	@Override
	public void entryEvicted(EntryEvent<String, ExchangeRateDto> event) {
//		System.out.println("Entry Evicted:" + event);
	}

	@Override
	public void mapEvicted(MapEvent event) {
//		System.out.println("Map Evicted:" + event);
	}

	@Override
	public void mapCleared(MapEvent event) {
//		System.out.println("Map Cleared:" + event);
	}
}
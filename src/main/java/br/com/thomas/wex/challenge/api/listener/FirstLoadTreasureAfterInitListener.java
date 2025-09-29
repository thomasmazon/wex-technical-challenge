package br.com.thomas.wex.challenge.api.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import br.com.thomas.wex.challenge.api.service.TreasureService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Thomas J. Mazon de Oiveira
 */
@Slf4j
@Component
public class FirstLoadTreasureAfterInitListener
	implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    TreasureService treasureService;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

		try {
		    treasureService.syncCountryNames();
		} catch (Exception e) {
		    log.error("Error when try to sync Exchange Rage", e);
		    throw e;
		} finally {
		    log.error("!!!!! Exchange Rate Synced !!!!!");
		}

	}
}
package br.com.thomas.wex.challenge.api.service.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Thomas J. Mazon de Oiveira 
 */
@SuppressWarnings("serial")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateResponse implements Serializable {
	
	List<ExchangeRateDto> data;

}

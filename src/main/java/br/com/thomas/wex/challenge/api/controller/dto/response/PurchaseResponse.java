package br.com.thomas.wex.challenge.api.controller.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * @author Thomas J. Mazon de Oiveira
 */
@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class PurchaseResponse implements Serializable {
	
	@JsonProperty("id")
    private UUID id;
    
	@JsonProperty("idescriptiond")
    private String description;

	@JsonProperty("date")
    private LocalDate date;
    
	@JsonProperty("id")
    private BigDecimal amount;


}
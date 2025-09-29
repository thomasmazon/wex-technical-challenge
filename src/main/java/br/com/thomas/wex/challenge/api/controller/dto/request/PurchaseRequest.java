package br.com.thomas.wex.challenge.api.controller.dto.request;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Thomas J. Mazon de Oiveira
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@SuppressWarnings("serial")
public class PurchaseRequest implements Serializable {

	@NotBlank
    @Size(max = 50)
	@JsonProperty("description")
    private String description;

	
	/**
	 * date-format=yyyy-MM-dd HH:mm:ss
	 */
    @NotNull
    @PastOrPresent
    @JsonProperty("description")
    private LocalDate date;

    @NotNull
    @Positive
    @JsonProperty("amount")
    private BigDecimal amount;
}

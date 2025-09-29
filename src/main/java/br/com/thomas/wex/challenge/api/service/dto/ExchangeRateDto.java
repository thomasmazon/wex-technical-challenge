package br.com.thomas.wex.challenge.api.service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Thomas J. Mazon de Oiveira
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateDto {

	@JsonFormat(pattern="yyyy-MM-dd")
	@JsonProperty("record_date")
	LocalDate recordDate;
	
	@JsonProperty("effective_date")
	@JsonFormat(pattern="yyyy-MM-dd")
	LocalDate effectiveDate;
	
	@JsonProperty("country")
	String country;
	
	@JsonProperty("currency")
	String currency;
	
	@JsonProperty("country_currency_desc")
	String countryCurrencyDescription;
	
	@JsonProperty("exchange_rate")
	BigDecimal exchangeRate;
	
	@JsonProperty("src_line_nbr")
	String srcLineNbr;
	
	@JsonProperty("record_fiscal_year")
	String recordFiscalYear;
	
	@JsonProperty("record_fiscal_quarter")
	String recordFiscalQuarter;
	
	@JsonProperty("record_calendar_year")
	String recordCalendarYear;
	
	@JsonProperty("record_calendar_quarter")
	String recordCalendarQuarter;
	
	@JsonProperty("record_calendar_month")
	String recordCalendarMonth;
	
	@JsonProperty("record_calendar_day")
	String recordCalendarDay;
	
	
}

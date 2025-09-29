package br.com.thomas.wex.challenge.api.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.thomas.wex.challenge.api.controller.dto.request.PurchaseRequest;
import br.com.thomas.wex.challenge.api.controller.dto.response.ConvertedPurchaseResponse;
import br.com.thomas.wex.challenge.api.controller.dto.response.PurchaseResponse;
import br.com.thomas.wex.challenge.api.exception.NotFoundException;
import br.com.thomas.wex.challenge.api.model.PurchaseTransaction;
import br.com.thomas.wex.challenge.api.repository.PurchaseTransactionRepository;
import br.com.thomas.wex.challenge.api.service.dto.ExchangeRateDto;

/**
 * @author Thomas J. Mazon de Oiveira
 */
@Component
public class PurchaseService {

	@Autowired
	PurchaseTransactionRepository repository;
	
	@Autowired
	TreasureService treasureService;

	/**
	 * @param request
	 * @return
	 */
	@Transactional(rollbackFor = Throwable.class)
	public PurchaseResponse createPurchase(PurchaseRequest request) {
		
		BigDecimal amount = request.getAmount().setScale(2, RoundingMode.HALF_UP);
		
		/**
		 * Double validation. 
		 * It will be validated during request
		 */
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Purchase amount must be positive");
		}

		PurchaseTransaction transaction = new PurchaseTransaction();
		transaction.setDescription(request.getDescription());
		transaction.setDate(request.getDate());
		transaction.setAmount(amount);

		PurchaseTransaction saved = repository.save(transaction);

		return PurchaseResponse.builder()
				.id(saved.getId())
				.description(saved.getDescription())
				.date(saved.getDate())
				.amount(saved.getAmount())
				.build();
	}

	/**
	 * 
	 * @param id
	 * @param country
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = true)
	public ConvertedPurchaseResponse getConvertedPurchase(UUID id, String country) throws Exception {
		
		
		if (!treasureService.validCountry(country)) {
			throw new NotFoundException("Country not found");
		}
		
		PurchaseTransaction transaction = repository.findById(id)
				.orElseThrow(() -> new NotFoundException("Purchase not found"));
		
		ExchangeRateDto exchangeRate = treasureService.getExchangeRateByCountryAndDate(country, transaction.getDate());

		BigDecimal converted = transaction.getAmount().multiply(exchangeRate.getExchangeRate()).setScale(2, RoundingMode.HALF_UP);

		return ConvertedPurchaseResponse.builder()
				.id(transaction.getId())
				.description(transaction.getDescription())
				.usdAmount(transaction.getAmount())
				.convertedAmount(converted)
				.exchangeRate(exchangeRate.getExchangeRate())
				.build();
	}
}
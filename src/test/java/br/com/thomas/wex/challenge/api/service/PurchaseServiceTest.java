package br.com.thomas.wex.challenge.api.service;

import br.com.thomas.wex.challenge.api.controller.dto.request.PurchaseRequest;
import br.com.thomas.wex.challenge.api.controller.dto.response.ConvertedPurchaseResponse;
import br.com.thomas.wex.challenge.api.controller.dto.response.PurchaseResponse;
import br.com.thomas.wex.challenge.api.exception.NotFoundException;
import br.com.thomas.wex.challenge.api.model.PurchaseTransaction;
import br.com.thomas.wex.challenge.api.repository.PurchaseTransactionRepository;
import br.com.thomas.wex.challenge.api.service.dto.ExchangeRateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Thomas J. Mazon de Oiveira
 */
public class PurchaseServiceTest {

    @Mock
    private PurchaseTransactionRepository repository;

    @Mock
    private TreasureService treasureService;

    private PurchaseService purchaseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        purchaseService = new PurchaseService();
        purchaseService.repository = repository;
        purchaseService.treasureService = treasureService;
    }

    @Test
    void testCreatePurchaseHappyPath() {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("Test");
        request.setDate(LocalDate.now());
        request.setAmount(new BigDecimal("100.00"));

        PurchaseTransaction saved = new PurchaseTransaction();
        saved.setId(UUID.randomUUID());
        saved.setDescription("Test");
        saved.setDate(request.getDate());
        saved.setAmount(new BigDecimal("100.00"));

        when(repository.save(any(PurchaseTransaction.class))).thenReturn(saved);

        PurchaseResponse response = purchaseService.createPurchase(request);

        assertThat(response.getId()).isEqualTo(saved.getId());
        assertThat(response.getAmount()).isEqualTo(new BigDecimal("100.00"));
    }

    @Test
    void testCreatePurchaseWithRounding() {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("Test");
        request.setDate(LocalDate.now());
        request.setAmount(new BigDecimal("100.005"));

        PurchaseTransaction saved = new PurchaseTransaction();
        saved.setId(UUID.randomUUID());
        saved.setDescription("Test");
        saved.setDate(request.getDate());
        saved.setAmount(new BigDecimal("100.01"));

        when(repository.save(any(PurchaseTransaction.class))).thenReturn(saved);

        PurchaseResponse response = purchaseService.createPurchase(request);

        assertThat(response.getAmount()).isEqualTo(new BigDecimal("100.01"));
    }

    @Test
    void testCreatePurchaseInvalidAmount() {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("Test");
        request.setDate(LocalDate.now());
        request.setAmount(BigDecimal.ZERO);

        assertThatThrownBy(() -> purchaseService.createPurchase(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Purchase amount must be positive");
    }

    @Test
    void testGetConvertedPurchaseHappyPath() throws Exception {
        UUID id = UUID.randomUUID();
        PurchaseTransaction transaction = new PurchaseTransaction();
        transaction.setId(id);
        transaction.setDescription("Test");
        transaction.setDate(LocalDate.of(2023, 12, 31));
        transaction.setAmount(new BigDecimal("100.00"));

        when(repository.findById(id)).thenReturn(Optional.of(transaction));
        when(treasureService.validCountry("Canada")).thenReturn(true);

        ExchangeRateDto rateDto = new ExchangeRateDto();
        rateDto.setExchangeRate(new BigDecimal("1.326"));

        when(treasureService.getExchangeRateByCountryAndDate("Canada", transaction.getDate())).thenReturn(rateDto);

        ConvertedPurchaseResponse response = purchaseService.getConvertedPurchase(id, "Canada");

        assertThat(response.getUsdAmount()).isEqualTo(new BigDecimal("100.00"));
        assertThat(response.getExchangeRate()).isEqualTo(new BigDecimal("1.326"));
        assertThat(response.getConvertedAmount()).isEqualTo(new BigDecimal("132.60"));
    }

    @Test
    void testGetConvertedPurchaseCountryNotFound() {
        UUID id = UUID.randomUUID();

        when(treasureService.validCountry("Invalid")).thenReturn(false);

        assertThatThrownBy(() -> purchaseService.getConvertedPurchase(id, "Invalid"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Country not found");
    }

    @Test
    void testGetConvertedPurchasePurchaseNotFound() {
        UUID id = UUID.randomUUID();

        when(treasureService.validCountry("Canada")).thenReturn(true);
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> purchaseService.getConvertedPurchase(id, "Canada"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Purchase not found");
    }

    @Test
    void testGetConvertedPurchaseNoExchangeRate() throws Exception {
        UUID id = UUID.randomUUID();
        PurchaseTransaction transaction = new PurchaseTransaction();
        transaction.setId(id);
        transaction.setDescription("Test");
        transaction.setDate(LocalDate.of(2023, 12, 31));
        transaction.setAmount(new BigDecimal("100.00"));

        when(repository.findById(id)).thenReturn(Optional.of(transaction));
        when(treasureService.validCountry("Canada")).thenReturn(true);
        when(treasureService.getExchangeRateByCountryAndDate("Canada", transaction.getDate())).thenReturn(null);

        assertThatThrownBy(() -> purchaseService.getConvertedPurchase(id, "Canada"))
                .isInstanceOf(NullPointerException.class);
    }
}
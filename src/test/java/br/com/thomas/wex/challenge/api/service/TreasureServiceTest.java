package br.com.thomas.wex.challenge.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.hazelcast.collection.IList;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import br.com.thomas.wex.challenge.api.service.dto.ExchangeRateDto;
import br.com.thomas.wex.challenge.api.service.dto.ExchangeRateResponse;
import reactor.core.publisher.Mono;

/**
 * @author Thomas J. Mazon de Oiveira
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class TreasureServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private HazelcastInstance hazelcastInstance;

    @Mock
    private IMap<String, ExchangeRateDto> treasureExchangeRatesMap;

    @Mock
    private IList<String> treasureCountryList;

    private TreasureService treasureService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        treasureService = new TreasureService();
        treasureService.webClient = webClient;
        treasureService.hazelcastInstance = hazelcastInstance;
        treasureService.treasureExchangeRatesMap = treasureExchangeRatesMap;
        treasureService.treasureCountryList = treasureCountryList;
    }

    @Test
    void testValidCountry() {
        when(treasureCountryList.contains("Canada")).thenReturn(true);

        boolean valid = treasureService.validCountry("Canada");

        assertThat(valid).isTrue();
    }

    @Test
    void testValidCountryFalse() {
        when(treasureCountryList.contains("Invalid")).thenReturn(false);

        boolean valid = treasureService.validCountry("Invalid");

        assertThat(valid).isFalse();
    }

    @Test
    void testGetExchangeRateByCountryAndDateFromMap() {
        String key = "Canada_2023-12-31";
        ExchangeRateDto dto = new ExchangeRateDto();
        dto.setExchangeRate(new BigDecimal("1.326"));

        when(treasureExchangeRatesMap.containsKey(key)).thenReturn(true);
        when(treasureExchangeRatesMap.get(key)).thenReturn(dto);

        ExchangeRateDto result = treasureService.getExchangeRateByCountryAndDate("Canada", LocalDate.of(2023, 12, 31));

        assertThat(result.getExchangeRate()).isEqualTo(new BigDecimal("1.326"));
    }

    @Test
    void testGetExchangeRateByCountryAndDateFetchSuccess() {
        String country = "Canada";
        LocalDate date = LocalDate.of(2023, 12, 31);
        String key = country + "_" + date.toString();

        when(treasureExchangeRatesMap.containsKey(key)).thenReturn(false);

        ExchangeRateDto dto = new ExchangeRateDto();
        dto.setExchangeRate(new BigDecimal("1.326"));
        List<ExchangeRateDto> rates = Arrays.asList(dto);

        when(treasureService.fetchRatesPerCountry(eq(country), eq("2023-12-31"), eq("2023-06-31"))).thenReturn(rates);

        ExchangeRateDto result = treasureService.getExchangeRateByCountryAndDate(country, date);

        verify(treasureExchangeRatesMap).put(eq(key), eq(dto));
        assertThat(result.getExchangeRate()).isEqualTo(new BigDecimal("1.326"));
    }

    @Test
    void testGetExchangeRateByCountryAndDateFetchEmpty() {
        String country = "Canada";
        LocalDate date = LocalDate.of(2023, 12, 31);
        String key = country + "_" + date.toString();

        when(treasureExchangeRatesMap.containsKey(key)).thenReturn(false);
        when(treasureService.fetchRatesPerCountry(eq(country), eq("2023-12-31"), eq("2023-06-31"))).thenReturn(Arrays.asList());

        ExchangeRateDto result = treasureService.getExchangeRateByCountryAndDate(country, date);

        verify(treasureExchangeRatesMap, never()).put(any(), any());
        assertThat(result).isNull();
    }

	@Test
    void testFetchRatesPerCountry() {
        // Mock the WebClient chain
        RequestHeadersUriSpec uriSpec = mock(RequestHeadersUriSpec.class);
        RequestHeadersSpec headersSpec = mock(RequestHeadersSpec.class);
        ResponseSpec responseSpec = mock(ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        ExchangeRateResponse exchangeResponse = new ExchangeRateResponse();
        ExchangeRateDto dto = new ExchangeRateDto();
        dto.setExchangeRate(new BigDecimal("1.326"));
        exchangeResponse.setData(Arrays.asList(dto));

        when(responseSpec.bodyToMono(ExchangeRateResponse.class)).thenReturn(Mono.just(exchangeResponse));

        List<ExchangeRateDto> rates = treasureService.fetchRatesPerCountry("Canada", "2023-12-31", "2023-06-31");

        assertThat(rates).hasSize(1);
        assertThat(rates.get(0).getExchangeRate()).isEqualTo(new BigDecimal("1.326"));
    }

    @Test
    void testSyncCountryNames() {
        // Mock the WebClient chain
        RequestHeadersUriSpec uriSpec = mock(RequestHeadersUriSpec.class);
        RequestHeadersSpec headersSpec = mock(RequestHeadersSpec.class);
        ResponseSpec responseSpec = mock(ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        ExchangeRateResponse exchangeResponse = new ExchangeRateResponse();
        ExchangeRateDto dto1 = new ExchangeRateDto();
        dto1.setCountry("Canada");
        ExchangeRateDto dto2 = new ExchangeRateDto();
        dto2.setCountry("Mexico");
        exchangeResponse.setData(Arrays.asList(dto1, dto2));

        when(responseSpec.bodyToMono(ExchangeRateResponse.class)).thenReturn(Mono.just(exchangeResponse));

        treasureService.syncCountryNames();

        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(treasureCountryList).addAll(captor.capture());
        List<String> countries = captor.getValue();
        assertThat(countries).containsExactly("Canada", "Mexico");
    }

    @Test
    void testGetCountries() throws Exception {
        when(treasureCountryList.iterator()).thenReturn(Arrays.asList("Canada", "Mexico").iterator()); // Assuming get returns the list

        IList<String> countries = treasureService.getCoutries();

        assertThat(countries).isEqualTo(treasureCountryList);
    }
}
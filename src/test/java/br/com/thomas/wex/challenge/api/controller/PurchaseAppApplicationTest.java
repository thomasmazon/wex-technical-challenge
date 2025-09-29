package br.com.thomas.wex.challenge.api.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;

import br.com.thomas.wex.challenge.api.controller.dto.request.PurchaseRequest;
import br.com.thomas.wex.challenge.api.controller.dto.response.ConvertedPurchaseResponse;
import br.com.thomas.wex.challenge.api.controller.dto.response.PurchaseResponse;

/**
 * @author Thomas J. Mazon de Oiveira
 */
@SpringBootTest
@AutoConfigureMockMvc
class PurchaseAppApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(options().port(8089));
        wireMockServer.start();
        configureFor("localhost", 8089);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void testCreateAndGetConvertedPurchase() throws Exception {
        // Mock the exchange rate API
        String mockResponse = "{\"data\": [{\"exchange_rate\": \"1.326\", \"record_date\": \"2023-12-31\"}], \"meta\": {}}";
        wireMockServer.stubFor(get(urlPathMatching("/services/api/fiscal_service/v1/accounting/od/rates_of_exchange"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBody(mockResponse)));

        // Create purchase
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("Test purchase");
        request.setDate(LocalDate.of(2023, 12, 31));
        request.setAmount(new BigDecimal("100.00"));

        String requestJson = objectMapper.writeValueAsString(request);

        MvcResult createResult = mockMvc.perform(MockMvcRequestBuilders.post("/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponseJson = createResult.getResponse().getContentAsString();
        PurchaseResponse purchaseResponse = objectMapper.readValue(createResponseJson, PurchaseResponse.class);
        UUID id = purchaseResponse.getId();

        // Get converted
        String getUrl = "/purchases/" + id + "?targetCurrency=Canada-Dollar";
        MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String getResponseJson = getResult.getResponse().getContentAsString();
        ConvertedPurchaseResponse convertedResponse = objectMapper.readValue(getResponseJson, 
        		ConvertedPurchaseResponse.class);

        assertThat(convertedResponse.getUsdAmount()).isEqualTo(new BigDecimal("100.00"));
        assertThat(convertedResponse.getExchangeRate()).isEqualTo(new BigDecimal("1.326"));
        assertThat(convertedResponse.getConvertedAmount()).isEqualTo(new BigDecimal("132.60"));

        // Test no rate
        wireMockServer.stubFor(get(urlPathMatching("/services/api/fiscal_service/v1/accounting/od/rates_of_exchange"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBody("{\"data\": [], \"meta\": {}}")));

        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The purchase cannot be converted to the target currency"));
    }

    @Test
    void testInvalidPurchaseRequest() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setDescription("This description is way too long to be valid and should fail validation because it's over 50 characters");
        request.setDate(LocalDate.now().plusDays(1)); // Future date
        request.setAmount(new BigDecimal("-10.00")); // Negative amount

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }
}
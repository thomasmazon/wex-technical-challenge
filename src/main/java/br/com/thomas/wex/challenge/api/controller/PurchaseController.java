package br.com.thomas.wex.challenge.api.controller;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.thomas.wex.challenge.api.controller.dto.request.PurchaseRequest;
import br.com.thomas.wex.challenge.api.controller.dto.response.ApiErrorDto;
import br.com.thomas.wex.challenge.api.controller.dto.response.ConvertedPurchaseResponse;
import br.com.thomas.wex.challenge.api.controller.dto.response.PurchaseResponse;
import br.com.thomas.wex.challenge.api.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Thomas J. Mazon de Oiveira
 */
@Slf4j
@Validated
@RestController
@Tag(name = "PurchaseController", description = "Controller responsable to accept request about Purchase")
@RequestMapping(path = { "/purchases" }, produces = MediaType.APPLICATION_JSON_VALUE)
public class PurchaseController {
	
	@Autowired
    PurchaseService purchaseService;

	// @formatter:off
	@Operation(summary = "Endpoint to create a purchase")
	@ApiResponses({
			@ApiResponse(responseCode = "201", 
                  description = "Purchase persisted with success.", 
                  content = @Content( mediaType = MediaType.APPLICATION_JSON_VALUE,
                                      schema = @Schema(implementation = PurchaseResponse.class))
			 ),
			 @ApiResponse(responseCode = "404", 
						description = ""
								+ "\n\t * Erro ao cadastrar profissional - {error.profissional.create.profissional.generic}",
							content = @Content(schema = @Schema(implementation = ApiErrorDto.class))
			 ),
	})
	@PostMapping("")
    public ResponseEntity<PurchaseResponse> create(@RequestBody @Valid PurchaseRequest request) throws Exception {
        PurchaseResponse response = purchaseService.createPurchase(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

	@ApiResponses({
		@ApiResponse(responseCode = "200", 
              description = "Retrieve Purchase converted", 
              content = @Content( mediaType = MediaType.APPLICATION_JSON_VALUE,
                                  schema = @Schema(implementation = ConvertedPurchaseResponse.class))
		 ),
		 @ApiResponse(responseCode = "404", 
					description = ""
							+ "\n\t * Invalid Country"
							+ "\n\t * Invalid Purchase ID"
							,
						content = @Content(schema = @Schema(implementation = ApiErrorDto.class))
		 ),
	})
    @GetMapping("/{id}")
    public ResponseEntity<ConvertedPurchaseResponse> getConverted(
    		@PathVariable UUID id, 
    		@RequestParam String country) throws Exception {
        return ResponseEntity.ok(purchaseService.getConvertedPurchase(id, country));
    }

}
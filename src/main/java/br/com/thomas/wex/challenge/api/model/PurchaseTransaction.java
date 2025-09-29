package br.com.thomas.wex.challenge.api.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import br.com.thomas.wex.challenge.api.repository.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Thomas J. Mazon de Oiveira 
 */
@Data
@Audited
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings({"serial", "deprecation"})
//@formatter:off
@Table(name = "purchase_transaction", 
	uniqueConstraints = {
		@UniqueConstraint(name = "UNQ_PURCHASE_TRANSACTION", columnNames = { "id" })
	}
)
//@formatter:on
public class PurchaseTransaction extends BaseAuditModel {
	
	@Id
	@GenericGenerator(name = Constants.UUID_GENERATOR_NAME, strategy = Constants.UUID_GENERATOR_STRATEGY)
    @GeneratedValue(generator = Constants.UUID_VALUE_GENERATOR)
	@Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "description", length = 50, nullable = false)
    private String description;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Builder.Default
	@Column(name = "amount", nullable = false, precision = 10, scale = 2, columnDefinition = "Decimal(10,2) default '0.00'")
	private BigDecimal amount = BigDecimal.ZERO;

}
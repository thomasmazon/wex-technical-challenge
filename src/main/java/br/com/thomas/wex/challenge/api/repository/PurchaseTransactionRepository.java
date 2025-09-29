package br.com.thomas.wex.challenge.api.repository;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.thomas.wex.challenge.api.model.PurchaseTransaction;

/**
 * @author Thomas J. Mazon de Oiveira
 */
@Repository
public interface PurchaseTransactionRepository extends JpaRepository<PurchaseTransaction, UUID> {
	
	/**
     * Find by id
     * 
     * @param id
     * @return
     */
	Optional<PurchaseTransaction> findById(UUID id);
}
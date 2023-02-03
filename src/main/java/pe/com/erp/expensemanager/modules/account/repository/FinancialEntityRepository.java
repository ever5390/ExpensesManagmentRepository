package pe.com.erp.expensemanager.modules.account.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pe.com.erp.expensemanager.modules.account.model.FinancialEntity;

import java.util.List;

@Repository
public interface FinancialEntityRepository extends CrudRepository<FinancialEntity, Long>{

	@Query("SELECT fe FROM FinancialEntity fe WHERE fe.owner.id = :idOwnerId")
	List<FinancialEntity> findByOwnerId(Long idOwnerId);
}

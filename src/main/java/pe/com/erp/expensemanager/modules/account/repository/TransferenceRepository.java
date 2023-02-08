package pe.com.erp.expensemanager.modules.account.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pe.com.erp.expensemanager.modules.account.model.Transference;

@Repository
public interface TransferenceRepository extends CrudRepository<Transference, Long> {

	@Query("SELECT t FROM Transference t WHERE t.period.id = :idPeriod")
	List<Transference> listTransferencesByIdPeriod(Long idPeriod);
	@Query("SELECT t FROM Transference t WHERE (t.accountOrigin.id = :idAccount or t.accountDestiny.id = :idAccount) and t.period.id = :idPeriod")
	List<Transference> listTransferencesByIdAccountAndIdPeriod(Long idAccount, Long idPeriod);
}

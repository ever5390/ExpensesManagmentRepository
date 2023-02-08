package pe.com.erp.expensemanager.modules.account.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pe.com.erp.expensemanager.modules.account.model.Transference;
import java.util.List;

@Repository
public interface AccountTransferencesRepository extends CrudRepository<Transference, Long> {

    @Query("SELECT t FROM Transference t WHERE (t.accountOrigin.id =:idAccount or t.accountDestiny.id =:idAccount) and t.period.id = :periodId")
    List<Transference> findTransferencesByAccountIdAndPeriodId(Long idAccount, Long periodId);
}

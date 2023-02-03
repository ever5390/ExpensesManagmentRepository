package pe.com.erp.expensemanager.modules.account.repository.generic;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pe.com.erp.expensemanager.modules.account.model.generic.FinancialEntityGeneric;

@Repository
public interface FinancialEntityGenericRepository extends CrudRepository<FinancialEntityGeneric, Long>{

}

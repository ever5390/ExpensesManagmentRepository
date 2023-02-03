package pe.com.erp.expensemanager.modules.account.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pe.com.erp.expensemanager.modules.account.model.AccountType;

@Repository
public interface AccountTypeRepository extends CrudRepository<AccountType, Long>{

}

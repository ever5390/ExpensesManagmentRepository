package pe.com.erp.expensemanager.modules.account.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pe.com.erp.expensemanager.modules.account.model.Account;
import pe.com.erp.expensemanager.modules.account.model.TypeStatusAccountOPC;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long>{

	@Query("SELECT a FROM Account a WHERE a.period.id = :idPeriod")
	List<Account> listAccountByIdPeriod(Long idPeriod);
	 
	@Query("SELECT a FROM Account a WHERE a.accountType.id = :idAccountType and a.period.id= :idPeriod")
	Account accountByTypeAccountIdAndPeriodId(Long idAccountType, Long idPeriod );
	
	@Query("SELECT a FROM Account a WHERE a.accountName = :nameAccount and a.period.id= :idPeriod")
	List<Account> existAccountByNameAndPeriodId(String nameAccount, Long idPeriod );
	
	//Get Account by type[PARENT or CHILD] and status[INIT, PROC, CLOSED] and idPeriod
	@Query("SELECT a FROM Account a WHERE a.accountType.id = :idAccountType and a.statusAccount = :status and a.period.id= :idPeriod")
	Account findAccountByTypeAccountAndStatusAccountAndPeriodId(Long idAccountType, TypeStatusAccountOPC status, Long idPeriod );
	
	@Query("SELECT a FROM Account a WHERE a.accountType.id = :idAccountType and a.statusAccount = :status and a.period.id= :idPeriod")
	List<Account> findListAccountByTypeAccountAndStatusAccountAndPeriodId(Long idAccountType, TypeStatusAccountOPC status, Long idPeriod );
	
	@Query("SELECT a FROM Account a WHERE a.statusAccount = :status and a.period.id= :idPeriod")
	List<Account> findListAccountByStatusAccountAndPeriodId(TypeStatusAccountOPC status, Long idPeriod );	
	
	@Modifying
	@Query("DELETE Account a WHERE a.period.id= :idPeriod")
	int deleteAllAccountByPeriodId(Long idPeriod);
	
	@Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber and a.period.id = :idPeriod")
	Account accountByAccountNumberIdAndPeriodId(String accountNumber, Long idPeriod );
	
	@Modifying
	@Query("UPDATE Account a SET a.accountName = :newAccountName WHERE a.accountNumber = :accountNumber and a.period.id= :idPeriod")
	int updateAllAccountNameByAccountNumber(String newAccountName, String accountNumber, Long idPeriod);
	
	@Query("SELECT a FROM Account a WHERE a.accountType.id = :idAccountType")
	List<Account> listAllAccountByIdTypeAccount(Long idAccountType);





	@Query("SELECT a FROM Account a WHERE a.accountName = :accountName and a.period.id = :periodId and a.financialEntity.name = :financialEntity")
	List<Account> findParentAccountByPeriodIdAndFinancialEntity(String accountName, Long periodId, String financialEntity);

	@Query("SELECT a FROM Account a WHERE a.accountName = :accountName and a.period.id = :periodId and a.accountParentId = :accountParentId")
	List<Account> findAccountByPeriodIdAndAccountParentId(String accountName, Long periodId, Long accountParentId);

	@Query("SELECT a FROM Account a WHERE a.accountParentId = :idAccountParent and a.accountType.typeName = :type")
	List<Account> findAccountsByParentIdAndTypeChild(Long idAccountParent, String type);

	@Query("SELECT a FROM Account a WHERE a.accountParentId = :accountParentId")
	List<Account> findAccountsByParentId(Long accountParentId);

	@Modifying
	@Query("DELETE FROM Account a WHERE a.accountParentId = :accountParentId")
	void deleteAllByAccountParentId(Long accountParentId);
}

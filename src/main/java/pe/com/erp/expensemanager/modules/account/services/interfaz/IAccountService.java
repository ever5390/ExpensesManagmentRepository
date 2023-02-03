package pe.com.erp.expensemanager.modules.account.services.interfaz;

import java.util.List;

import pe.com.erp.expensemanager.exception.CustomException;
import pe.com.erp.expensemanager.modules.account.model.Account;
import pe.com.erp.expensemanager.modules.account.model.TypeStatusAccountOPC;
import pe.com.erp.expensemanager.modules.categories.model.Category;
import pe.com.erp.expensemanager.shared.model.Response;

public interface IAccountService {

	List<Account> listAccountByIdPeriod(Long idPeriod);

	Response deleteAccountById(Long idAccount, String messageLog);

	Response save(Account account, String messageLog) throws CustomException;

	Response update(Account account, Long idAccount, String messageLog) throws CustomException;

	Account findById(Long id);
	
	List<Account> listAccountByIdAccountType(Long idAccountType);

	List<Category> findCategoriesNotAssocToAccount(Long idAccount, String mmessageLog);
}

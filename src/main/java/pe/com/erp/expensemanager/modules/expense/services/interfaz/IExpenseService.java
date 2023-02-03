package pe.com.erp.expensemanager.modules.expense.services.interfaz;

import java.util.Date;
import java.util.List;

import pe.com.erp.expensemanager.exception.CustomException;
import pe.com.erp.expensemanager.modules.expense.model.Expense;
import pe.com.erp.expensemanager.shared.model.Response;

public interface IExpenseService {

	Response saveExpense(Expense expenseRequest, String messageLog) throws CustomException;

	Response savePay(Expense payRequest, String messageLog) throws CustomException;
	
	//List<Expense> findExpensesByIdPeriodAndIStatusPay(Long idPeriod, boolean statusPay);

	// List<String> findPayerDistinctNamesExpensessByWorskpaceId(Long idWorkspace);
	
	//Response updateStatusPay(Long idExpenseUpdate);

	Response deleteExpenseById(Long idExpense, String messageLog);

	Response updateExpense(Expense expenseRequest, Long idExpenseUpdateReq, String messageLog);

    //Expense updateVouchers(Expense expenseRequest);
	
	
	//List<Expense> findExpensesByIdWorkspaceAndIdPeriod(Long idWorkspace, Long idPeriod);
	//List<Expense> findExpensesByIdWorkspaceAndIdPeriod(Long idWorkspace, Long idPeriod);

	List<Expense> findExpensessByWorskpaceIdAndDateRange(Long idWorkspace, Date dateBegin, Date dateEnd );

	
}

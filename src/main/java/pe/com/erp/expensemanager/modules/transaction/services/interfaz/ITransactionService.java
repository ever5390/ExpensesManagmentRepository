package pe.com.erp.expensemanager.modules.transaction.services.interfaz;

import java.util.Date;
import java.util.List;

import pe.com.erp.expensemanager.exception.CustomException;
import pe.com.erp.expensemanager.modules.transaction.model.Transaction;
import pe.com.erp.expensemanager.shared.model.Response;

public interface ITransactionService {

	Response saveTransaction(Transaction transactionRequest, String messageLog) throws CustomException;

	Response savePay(Transaction payRequest, String messageLog) throws CustomException;

	Response deleteTransactionById(Long idExpense, String messageLog);

	Response updateTransactionById(Transaction transactionRequest, Long idTransaction, String messageLog);

	List<Transaction> findTransactionByWorskpaceIdAndDateRange(Long idWorkspace, String dateBegin, String dateEnd );

	List<Transaction> findTransactionByAccountIdAndPeriodId(Long idAccount, Long idPeriod );

	Transaction updateVouchers(Transaction expenseRequest, String messageLog);
}

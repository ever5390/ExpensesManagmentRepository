package pe.com.erp.expensemanager.modules.expense.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pe.com.erp.expensemanager.modules.account.model.Account;
import pe.com.erp.expensemanager.modules.expense.model.Expense;

@Repository
public interface ExpenseRepository extends CrudRepository<Expense, Long>{

	@Query("Select COALESCE(sum(amount),0) from Expense e where e.period.id =:idPeriod")
	double totalSpentedByDatePeriodId(Long idPeriod);
			
	@Query("Select e from Expense e where e.createAt BETWEEN :dateBegin and :dateEnd ORDER BY e.createAt DESC")
	List<Expense> findExpensesByDateRange(Date dateBegin, Date dateEnd);
	/*
	@Query("Select DISTINCT(e.payer) from Expense e where e.workspace.id = :idWorkspace")
	List<String> findPayerDistinctNamesExpensessByWorskpaceId(Long idWorkspace);

	@Query("Select e from Expense e where e.period.id =:idPeriod and e.isPendingPayment = :isPendingPayment")
	List<Expense> findExpensesBypIdPeriodAndIsPendingPay(Long idPeriod, boolean isPendingPayment);
		*/
	@Query("Select COALESCE(sum(amount),0) from Expense e where e.period.id = :idPeriod and e.category.id = :idCateg")
	double totalSpentedByIdCategoryAndPeriod(Long idPeriod, Long idCateg);
	/*
	@Query("Select COALESCE(sum(amount),0) from Expense e where e.period.id = :idPeriod and e.account.paymentMethod.id = :idPaymentMethod")
	double totalSpentedByIdidPaymentMethodAndPeriod(Long idPeriod, Long idPaymentMethod);
*/
	@Transactional
	@Modifying
	@Query("UPDATE Expense e SET e.account = :newAccount WHERE e.category.id= :idCategory and e.period.id = :idPeriod")
	int updateExpenseRegisterWithNewAccountByIdCategory(Account newAccount, Long idCategory, Long idPeriod);

	/*
	@Query("Select e from Expense e where e.workspace.id = :idWorkspace AND e.period.id =:idPeriod ORDER BY e.createAt DESC")
	List<Expense> findExpensesByIdWorkspaceAndIdperiod(Long idWorkspace, Long idPeriod);
*/
	@Query("Select e from Expense e where e.period.workspace.id = :idWorkspace and e.createAt BETWEEN :dateBegin and :dateEnd ORDER BY e.createAt DESC")
	List<Expense> findExpensessByWorskpaceIdAndDateRange(Long idWorkspace, Date dateBegin, Date dateEnd);

}


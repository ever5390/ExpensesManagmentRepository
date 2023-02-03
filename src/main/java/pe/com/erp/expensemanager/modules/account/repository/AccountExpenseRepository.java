package pe.com.erp.expensemanager.modules.account.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pe.com.erp.expensemanager.modules.expense.model.Expense;

import java.util.List;

@Repository
public interface AccountExpenseRepository extends CrudRepository<Expense, Long> {

    @Query("SELECT e FROM Expense e WHERE e.account.id = :idAccount")
    List<Expense> findExpensesByAccountId(Long idAccount);
}

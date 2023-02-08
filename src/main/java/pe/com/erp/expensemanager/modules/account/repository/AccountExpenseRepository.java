package pe.com.erp.expensemanager.modules.account.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pe.com.erp.expensemanager.modules.transaction.model.Transaction;

import java.util.List;

@Repository
public interface AccountExpenseRepository extends CrudRepository<Transaction, Long> {

    @Query("SELECT e FROM Transaction e WHERE e.account.id = :idAccount")
    List<Transaction> findTransactionByAccountId(Long idAccount);
}

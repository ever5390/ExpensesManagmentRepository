package pe.com.erp.expensemanager.modules.expense.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pe.com.erp.expensemanager.modules.expense.model.According;

@Repository
public interface AccordingRepository extends CrudRepository<According, Long>{

}

package pe.com.erp.expensemanager.shared.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pe.com.erp.expensemanager.shared.model.Vouchers;

@Repository
public interface VoucherRepository extends CrudRepository<Vouchers, Long>{
	 
}

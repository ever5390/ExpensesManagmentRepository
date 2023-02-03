package pe.com.erp.expensemanager.modules.partners.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pe.com.erp.expensemanager.modules.notifications.model.NotificationExpense;
import pe.com.erp.expensemanager.modules.partners.model.Partner;

import java.util.List;

@Repository
public interface PartnerRepository extends CrudRepository<Partner, Long>{

	@Query("SELECT p FROM Partner p WHERE p.ownerId = :idOwner")
	List<Partner> findPartnersByOwnerId(Long idOwner);
}

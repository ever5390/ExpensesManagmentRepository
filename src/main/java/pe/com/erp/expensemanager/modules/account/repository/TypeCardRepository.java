package pe.com.erp.expensemanager.modules.account.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pe.com.erp.expensemanager.modules.account.model.TypeCard;

import java.util.List;

@Repository
public interface TypeCardRepository extends CrudRepository<TypeCard, Long>{

	@Query("SELECT tc FROM TypeCard tc WHERE tc.owner.id = :idOwnerId")
	List<TypeCard> findByOwnerId(Long idOwnerId);
}

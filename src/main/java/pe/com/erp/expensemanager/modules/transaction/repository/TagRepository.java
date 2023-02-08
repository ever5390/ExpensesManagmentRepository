package pe.com.erp.expensemanager.modules.transaction.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pe.com.erp.expensemanager.modules.transaction.model.Tag;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long>{

	
	@Query("SELECT t FROM Tag t WHERE t.owner.id =:ownerId")
	List<Tag> listTagsByOwnerId(Long ownerId);
}

package pe.com.erp.expensemanager.modules.categories.repository.generic;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pe.com.erp.expensemanager.modules.categories.model.generic.CategoryGeneric;

@Repository
public interface CategoryGenericRepository extends JpaRepository<CategoryGeneric, Long>{

	@Query("SELECT c FROM CategoryGeneric c")
	List<CategoryGeneric> listAllCategoryGenericByOwnerId();
	
}

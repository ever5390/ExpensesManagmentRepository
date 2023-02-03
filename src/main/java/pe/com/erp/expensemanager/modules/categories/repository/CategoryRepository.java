package pe.com.erp.expensemanager.modules.categories.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pe.com.erp.expensemanager.modules.categories.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{

	@Query("SELECT c FROM Category c WHERE c.owner.id = :idOwner")
	List<Category> listAllCategoryByOwnerId(Long idOwner);
	
	@Query("SELECT c FROM Category c WHERE c.name = :name and c.owner.id = :idOwner")
	Category findByNameAndOwnerId(String name, Long idOwner);
    
    @Transactional
    @Modifying
	@Query("UPDATE Category c SET c.active = false WHERE c.owner.id = :idOwner")
	void updateCategoriesStatusZEROByOwnerId(Long idOwner);
    
    @Transactional
    @Modifying
	@Query("UPDATE Category c SET c.active = false WHERE c.owner.id =:idOwner")
	int updateCategoriesStatusZEROByOwnerId2(Long idOwner);
}

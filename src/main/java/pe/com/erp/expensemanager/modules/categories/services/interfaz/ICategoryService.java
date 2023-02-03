package pe.com.erp.expensemanager.modules.categories.services.interfaz;

import java.sql.SQLException;
import java.util.List;

import pe.com.erp.expensemanager.modules.categories.model.Category;
import pe.com.erp.expensemanager.shared.model.Response;

public interface ICategoryService {

	Category save(Category category);
	
	Category update(Category category, Long id);
	
	Category findById(Long idCategory);
		
	List<Category> listAllCategoryByIdOwner(Long idOwner);
	
	Response deleteCategoryById(Long idCategory) throws SQLException;
	
	List<Category> listAllCategory();
	
}

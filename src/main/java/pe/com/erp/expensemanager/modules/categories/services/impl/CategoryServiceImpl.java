package pe.com.erp.expensemanager.modules.categories.services.impl;

import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.com.erp.expensemanager.exception.CustomException;
import pe.com.erp.expensemanager.modules.account.repository.AccountRepository;
import pe.com.erp.expensemanager.modules.categories.model.Category;
import pe.com.erp.expensemanager.modules.categories.repository.CategoryRepository;
import pe.com.erp.expensemanager.modules.categories.services.interfaz.ICategoryService;
import pe.com.erp.expensemanager.modules.transaction.repository.TransactionRepository;
import pe.com.erp.expensemanager.properties.PropertiesExtern;
import pe.com.erp.expensemanager.shared.model.Response;

@Service
public class CategoryServiceImpl implements ICategoryService {
	
	private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

	@Autowired
	PropertiesExtern properties;
	
	@Autowired
	CategoryRepository categoryRepo;
	
	@Autowired
	AccountRepository accountRepo;
	
	@Autowired
    TransactionRepository expensesRepo;
	
	@Override
	@Transactional
	public Category save(Category categoryRequest) {
		
		Category categoryAlreadyExist = categoryRepo.findByNameAndOwnerId(categoryRequest.getName(), categoryRequest.getOwner().getId());
		if(categoryAlreadyExist != null) {
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_CATEGORY_NAME_REPEAT);
		}

		return categoryRepo.save(categoryRequest);
	}
	
	@Override
	@Transactional
	public Category update(Category categoryRequest, Long idCateg) {
		
		Category categoryLocated = null;
		
		categoryLocated = categoryRepo.findById(idCateg).orElse(null);
		if(categoryLocated == null) {
			throw new CustomException(properties.RESPONSE_GENERIC_ERROR_NOTFOUND_MESSAGE);
		}
		
		Category categoryAlreadyExist = categoryRepo.findByNameAndOwnerId(categoryRequest.getName(), categoryRequest.getOwner().getId());
		
		if(categoryAlreadyExist != null && categoryAlreadyExist.getId() != categoryRequest.getId()) {
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_CATEGORY_NAME_REPEAT);
		}
		
		categoryLocated.setName(categoryRequest.getName());
		categoryLocated.setImage(categoryRequest.getImage());
		return categoryRepo.save(categoryLocated);		

	}
	
	

	@Override
	public Category findById(Long idCategory) {
		return categoryRepo.findById(idCategory).orElseGet(null);
	}

	@Override
	@Transactional
	public Response deleteCategoryById(Long idCategory) throws SQLException {
		
		Response response = new Response();
		//double totalSpentByCateg = expensesRepo.totalSpentedByIdCategoryAndPeriod(idPeriod, idCategory);
		
		/*
		 * if(totalSpentByCateg > 0) {
		 * response.setTitle(properties.RESPONSE_GENERIC_INFO_TITLE);
		 * response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
		 * response.setMessage(properties.
		 * RESPONSE_CUSTOMIZED_MESSAGE_CATEGORY_NOT_DELETED); response.setObject(null);
		 * return response; }
		 */
		
		categoryRepo.deleteById(idCategory);
		
		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_DELETE_SUCCESS_MESSAGE);
		response.setObject(null);

		return response;
	}

	@Override
	public List<Category> listAllCategoryByIdOwner(Long idOwner) {
		return categoryRepo.listAllCategoryByOwnerId(idOwner);
	}

	@Override
	public List<Category> listAllCategory() {
		return categoryRepo.findAll();
	}

}

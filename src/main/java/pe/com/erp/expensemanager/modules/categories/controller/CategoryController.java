package pe.com.erp.expensemanager.modules.categories.controller;

import java.sql.SQLException;
import java.util.List;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import pe.com.erp.expensemanager.exception.CustomException;
import pe.com.erp.expensemanager.modules.categories.model.Category;
import pe.com.erp.expensemanager.modules.categories.services.interfaz.ICategoryService;
import pe.com.erp.expensemanager.properties.PropertiesExtern;
import pe.com.erp.expensemanager.shared.model.Response;

@RestController
@EnableTransactionManagement
@CrossOrigin(origins = { "http://localhost:4200", "*" })
@RequestMapping(path = "/api/v1")
public class CategoryController {

	private static final Logger LOG = LoggerFactory.getLogger(CategoryController.class);

	@Autowired
	PropertiesExtern properties;

	@Autowired
	ICategoryService icategoryService;

	@GetMapping(path = "/category")
	public List<Category> listAllCategory() {
		return icategoryService.listAllCategory();
	}

	@GetMapping(path = "/owner/{idOwner}/category")
	public List<Category> listAllCategoryByIdOwner(@PathVariable Long idOwner) {
		return icategoryService.listAllCategoryByIdOwner(idOwner);
	}

	@GetMapping(path = "/category/{id}")
	public ResponseEntity<Response> findById(@PathVariable("id") Long idCategory) {
		Response response = new Response();
		Category categoryLocated = null;

		try {
			categoryLocated = icategoryService.findById(idCategory);

		} catch (NullPointerException e) {

			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			response.setMessage(properties.RESPONSE_GENERIC_ERROR_NOTFOUND_MESSAGE);

			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.NOT_FOUND);

		} catch (DataAccessException e) {
			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
			response.setMessage(properties.RESPONSE_GENERIC_UPDATE_ERROR_INTERNALSERVER_MESSAGE + " "
					+ e.getMostSpecificCause().getMessage());

			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_SUCCESS_FOUND_MESSAGE);

		response.setObject(categoryLocated);

		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@PostMapping(path = "/category")
	public ResponseEntity<Response> save(@Valid @RequestBody Category categoryRequest, BindingResult validity) {

		Response response = new Response();
		Category categoryCreated = null;

		try {
			response.setTitle("NO REGISTRADO");
			response.setStatus("info");

			if (categoryRequest.getName().isEmpty() || categoryRequest.getName() == null) {
				response.setMessage("El campo nombre se encuentra vacío");
				return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
			}

			categoryCreated = icategoryService.save(categoryRequest);

		} catch (CustomException ce) {
			response.setTitle(properties.RESPONSE_GENERIC_INFO_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			response.setMessage(ce.getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.CONFLICT);
			
		} catch (RuntimeException re) {
			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
			response.setMessage(re.getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_SAVE_SUCCESS_MESSAGE);
		response.setObject(categoryCreated);

		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@PutMapping(path = "/category/{id}")
	public ResponseEntity<Response> update(@Valid @RequestBody Category categoryRequest,
			@PathVariable("id") Long idcategory) {
		LOG.info("pase por aquí UPDATE");

		Response response = new Response();
		Category categoryUpdated = null;

		try {
			response.setStatus("info");
			
			if (categoryRequest.getName().isEmpty() || categoryRequest.getName() == null) {
				response.setMessage("El campo nombre se encuentra vacío");
				return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
			}

			categoryUpdated = icategoryService.update(categoryRequest, idcategory);

		} catch (NullPointerException e) {

			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			response.setMessage(properties.RESPONSE_GENERIC_ERROR_NOTFOUND_MESSAGE);

			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.NOT_FOUND);
			
		} catch (CustomException ce) {
			response.setTitle(properties.RESPONSE_GENERIC_INFO_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			response.setMessage(ce.getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.CONFLICT);			

		} catch (RuntimeException re) {
			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
			response.setMessage(re.getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_UPDATE_SUCCESS_MESSAGE);

		response.setObject(categoryUpdated);

		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@DeleteMapping(path = "/category/{categId}")
	@ResponseBody
	ResponseEntity<Response> deleteExpenseById(@PathVariable Long categId) throws SQLException{
		Response response = new Response();
		try {
			response =  icategoryService.deleteCategoryById(categId);
			if(!response.getStatus().equals(properties.RESPONSE_GENERIC_SUCCESS_STATUS)) {
				return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
			}
			
		} catch (CustomException ce) {
			response.setTitle(properties.RESPONSE_GENERIC_INFO_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			response.setMessage(ce.getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.CONFLICT);
		} catch(DataAccessException  e) {
			System.out.println(e.getMostSpecificCause().getMessage());
			if(e.getMostSpecificCause().getMessage().contains("FOREIGN KEY")) {
				response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
				response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
				response.setMessage(properties.RESPONSE_CUSTOMIZED_MESSAGE_CATEGORY_ACCOUNT_NOT_DELETED);
				response.setObject(null);
				return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch(Exception e) {
			System.out.println(e);
			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
			response.setMessage(properties.RESPONSE_GENERIC_SAVE_ERROR_INTERNALSERVER_MESSAGE);
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Response>(response, HttpStatus.OK);	
	}
	

}

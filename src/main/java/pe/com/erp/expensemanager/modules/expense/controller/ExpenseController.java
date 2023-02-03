package pe.com.erp.expensemanager.modules.expense.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.com.erp.expensemanager.exception.CustomException;
import pe.com.erp.expensemanager.modules.expense.model.Expense;
import pe.com.erp.expensemanager.modules.expense.model.Reposition;
import pe.com.erp.expensemanager.modules.expense.model.Tag;
import pe.com.erp.expensemanager.modules.expense.repository.ExpenseRepository;
import pe.com.erp.expensemanager.modules.expense.services.interfaz.IExpenseService;
import pe.com.erp.expensemanager.modules.expense.services.interfaz.ITagService;
import pe.com.erp.expensemanager.modules.owner.repository.OwnerRepository;
import pe.com.erp.expensemanager.properties.PropertiesExtern;
import pe.com.erp.expensemanager.shared.model.Response;
import pe.com.erp.expensemanager.utils.Utils;

@RestController
@EnableTransactionManagement
@CrossOrigin(origins = {"http://localhost:4200", "*"})
@RequestMapping(path="/api/v1")
public class ExpenseController {

	private static final Logger LOG = LoggerFactory.getLogger(ExpenseController.class);
	
	@Autowired
	PropertiesExtern properties;
	
	@Autowired
	IExpenseService expenseService;
	
	@Autowired
	ExpenseRepository expenseRepo;
	
	@Autowired
	OwnerRepository ownerRepo;
	
	@Autowired
	ITagService tagService;
	/*
	@GetMapping("workspace/{idWorkspace}/expense/payers")
	public List<String> findPayerDistinctNamesExpensessByWorskpaceId(@PathVariable Long idWorkspace) {
		return expenseService.findPayerDistinctNamesExpensessByWorskpaceId(idWorkspace);
	}
	*/
	@PostMapping(path="/expense")
	public ResponseEntity<Response> save( @RequestBody Expense expenseRequest) {
		
		Response response =  new Response();
		String ownerInfoMessage = "[X10598] EXPENSE SAVE ::";
		try {
			LOG.info(ownerInfoMessage + " BEGIN " );
			response = expenseService.saveExpense(expenseRequest, ownerInfoMessage);
			if(!response.getStatus().equals(properties.RESPONSE_GENERIC_SUCCESS_STATUS)) {
				return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
			}
			
		} catch (CustomException ce) {
			response.setTitle(properties.RESPONSE_GENERIC_INFO_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			response.setMessage(ce.getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.CONFLICT);
		} catch(RuntimeException  e) {
			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
			response.setMessage(properties.RESPONSE_GENERIC_SAVE_ERROR_INTERNALSERVER_MESSAGE);
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			LOG.info(ownerInfoMessage + " END " );
		}
		 		
		return new ResponseEntity<Response>(response, HttpStatus.CREATED);
	}
	/*
	@GetMapping("/period/{idPeriod}/expenses/by-statuspay/{statusPay}")
	public ResponseEntity<List<Expense>> findExpensesByIdperiodAndStatusPay(@PathVariable Long idPeriod, @PathVariable boolean statusPay) {
		return new ResponseEntity<>(expenseService.findExpensesByIdPeriodAndIStatusPay(idPeriod, statusPay), HttpStatus.OK);
	}
	*/
	@GetMapping("/owner/{ownerId}/tags")
	List<Tag> listTagsByOwnerId(@PathVariable Long ownerId) {
		return tagService.listTagsByOwnerId(ownerId);
	}
	
	@GetMapping("/expense/update-statuspay/{idExpense}")
	ResponseEntity<Response> updateStatusPay(@PathVariable Long idExpense) {
		Response response = new Response();
		
		try {
			//response = expenseService.updateStatusPay(idExpense);
			if(!response.getStatus().equals(properties.RESPONSE_GENERIC_SUCCESS_STATUS)) {
				return new ResponseEntity<Response>(response, HttpStatus.CONFLICT);
			}
			
		} catch (CustomException ce) {
			response.setTitle(properties.RESPONSE_GENERIC_INFO_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			response.setMessage(ce.getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.CONFLICT);
		} catch(RuntimeException  e) {
			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
			response.setMessage(properties.RESPONSE_GENERIC_SAVE_ERROR_INTERNALSERVER_MESSAGE);
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	/*
	@PutMapping("/expense/update-vouchers")
	Expense updateVouchers(@RequestBody Expense expenseRequest) {
		return expenseService.updateVouchers(expenseRequest);
	}
	*/
	@PutMapping("/expense/{idExpense}")
	ResponseEntity<Response> updateExpense(@RequestBody Expense expenseRequest, @PathVariable Long idExpense) {
		Response response = new Response();
		String ownerInfoMessage = "[X10598] EXPENSE SAVE ::";
		try {
			response = expenseService.updateExpense(expenseRequest, idExpense, ownerInfoMessage);
			if(!response.getStatus().equals(properties.RESPONSE_GENERIC_SUCCESS_STATUS)) {
				return new ResponseEntity<Response>(response, HttpStatus.CONFLICT);
			}
			
		} catch (CustomException ce) {
			response.setTitle(properties.RESPONSE_GENERIC_INFO_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			response.setMessage(ce.getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.CONFLICT);
		} catch(RuntimeException  e) {
			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
			response.setMessage(properties.RESPONSE_GENERIC_SAVE_ERROR_INTERNALSERVER_MESSAGE);
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
	@DeleteMapping("/expense/{idExpense}")
	ResponseEntity<Response> deleteExpenseById(@PathVariable Long idExpense) {
		Response response = new Response();
		String ownerInfoMessage = "[X10598] EXPENSE SAVE ::";
		try {
			response =  expenseService.deleteExpenseById(idExpense, ownerInfoMessage);
			if(!response.getStatus().equals(properties.RESPONSE_GENERIC_SUCCESS_STATUS)) {
				return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
			}
			
		} catch (CustomException ce) {
			response.setTitle(properties.RESPONSE_GENERIC_INFO_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			response.setMessage(ce.getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.CONFLICT);
		} catch(RuntimeException  e) {
			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
			response.setMessage(properties.RESPONSE_GENERIC_SAVE_ERROR_INTERNALSERVER_MESSAGE);
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Response>(response, HttpStatus.OK);	
	}
	/*
	@GetMapping("/workspace/{idWorkspace}/period/{idPeriod}/expenses")
	public ResponseEntity<List<Expense>> findExpensessByWorskpaceIdAndIdPeriod(@PathVariable Long idWorkspace, @PathVariable Long idPeriod) {
		return new ResponseEntity<>(expenseService.findExpensesByIdWorkspaceAndIdPeriod(
				idWorkspace, 
				idPeriod
			), HttpStatus.OK);
	}
*/
	@GetMapping("/workspace/{idWorkspace}/expenses/date-range")
	public ResponseEntity<List<Expense>> findExpensessByWorskpaceIdAndDateRange(@PathVariable("idWorkspace") Long idWorkspace, @RequestParam String dateBegin,
			@RequestParam String dateEnd) {
				
		return new ResponseEntity<>(expenseService.findExpensessByWorskpaceIdAndDateRange(
				idWorkspace, 
				Utils.convertStringToDate(dateBegin), 
				Utils.convertStringToDate(dateEnd)
			), HttpStatus.OK);
	}

}

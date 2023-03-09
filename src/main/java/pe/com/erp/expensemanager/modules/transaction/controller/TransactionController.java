package pe.com.erp.expensemanager.modules.transaction.controller;

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
import pe.com.erp.expensemanager.modules.transaction.model.Tag;
import pe.com.erp.expensemanager.modules.transaction.model.Transaction;
import pe.com.erp.expensemanager.modules.transaction.repository.TransactionRepository;
import pe.com.erp.expensemanager.modules.transaction.services.interfaz.ITransactionService;
import pe.com.erp.expensemanager.modules.transaction.services.interfaz.ITagService;
import pe.com.erp.expensemanager.modules.owner.repository.OwnerRepository;
import pe.com.erp.expensemanager.properties.PropertiesExtern;
import pe.com.erp.expensemanager.shared.model.Response;
import pe.com.erp.expensemanager.utils.Utils;

@RestController
@EnableTransactionManagement
@CrossOrigin(origins = {"http://localhost:4200", "*"})
@RequestMapping(path="/api/v1")
public class TransactionController {

	private static final Logger LOG = LoggerFactory.getLogger(TransactionController.class);
	
	@Autowired
	PropertiesExtern properties;
	
	@Autowired
    ITransactionService iTransactionService;

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
	public ResponseEntity<Response> save( @RequestBody Transaction expenseRequest) {
		
		Response response =  new Response();
		String messageLog = "[X10598] EXPENSE SAVE ::";
		LOG.info(messageLog + "=====================================");
		LOG.info(messageLog + " INICIO SAVE TRANSACCIÓN");
		LOG.info(messageLog + "======================================");
		try {
			response = iTransactionService.saveTransaction(expenseRequest, messageLog);

		} catch (CustomException ce) {
			LOG.info(messageLog + ce.getMessage());
			response.setTitle(properties.RESPONSE_GENERIC_INFO_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			response.setMessage(ce.getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.CONFLICT);
		} catch(RuntimeException  e) {
			LOG.error(messageLog + e.getMessage());
			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
			response.setMessage(properties.RESPONSE_GENERIC_SAVE_ERROR_INTERNALSERVER_MESSAGE);
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			LOG.info(messageLog + "=====================================");
			LOG.info(messageLog + " FINAL SAVE TRANSACCIÓN");
			LOG.info(messageLog + "======================================");
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

	@PutMapping("/expense/update-vouchers")
	Transaction updateVouchers(@RequestBody Transaction expenseRequest) {
		Transaction transactionSaved = new Transaction();
		String messageLog = "[X10598] EXPENSE UPDATE VOUCHER ::";
		LOG.info(messageLog + "=====================================");
		LOG.info(messageLog + " INICIO UPDATE VOUCHER AL REGISTRO DE TRANSACCIÓN GUARDADA PREVIAMENTE");
		LOG.info(messageLog + "======================================");
		try {
			transactionSaved = iTransactionService.updateVouchers(expenseRequest, messageLog);
		} catch(Exception e) {
			LOG.error(e.getMessage());
			transactionSaved = new Transaction();
		} finally {
			LOG.info(messageLog + "=====================================");
			LOG.info(messageLog + " FINAL UPDATE VOUCHER AL REGISTRO DE TRANSACCIÓN GUARDADA PREVIAMENTE");
			LOG.info(messageLog + "======================================");
		}
		return transactionSaved;
	}

	@PutMapping("/expense/{idExpense}")
	ResponseEntity<Response> updateExpense(@RequestBody Transaction expenseRequest, @PathVariable Long idExpense) {
		Response response = new Response();
		String messageLog = "[X10598] EXPENSE UPDATE ::";
		LOG.info(messageLog + "=====================================");
		LOG.info(messageLog + " INICIO UPDATE TRANSACCIÓN");
		LOG.info(messageLog + "======================================");
		try {
			response = iTransactionService.updateTransactionById(expenseRequest, idExpense, messageLog);
			if(!response.getStatus().equals(properties.RESPONSE_GENERIC_SUCCESS_STATUS)) {
				return new ResponseEntity<Response>(response, HttpStatus.CONFLICT);
			}
			
		} catch (CustomException ce) {
			LOG.info(messageLog + ce.getMessage());
			response.setTitle(properties.RESPONSE_GENERIC_INFO_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			response.setMessage(ce.getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.CONFLICT);
		} catch(RuntimeException  e) {
			LOG.info(messageLog + e.getMessage());
			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
			response.setMessage(properties.RESPONSE_GENERIC_SAVE_ERROR_INTERNALSERVER_MESSAGE);
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			LOG.info(messageLog + "=====================================");
			LOG.info(messageLog + " FINAL UPDATE TRANSACCIÓN");
			LOG.info(messageLog + "======================================");
		}
		
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
	@DeleteMapping("/expense/{idExpense}")
	ResponseEntity<Response> deleteExpenseById(@PathVariable Long idExpense) {
		Response response = new Response();
		String messageLog = "[X10598] EXPENSE DELETE ::";
		LOG.info(messageLog + "=====================================");
		LOG.info(messageLog + " INICIO DELETE TRANSACCIÓN");
		LOG.info(messageLog + "======================================");
		try {
			response =  iTransactionService.deleteTransactionById(idExpense, messageLog);
			if(!response.getStatus().equals(properties.RESPONSE_GENERIC_SUCCESS_STATUS)) {
				return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
			}
			
		} catch (CustomException ce) {
			LOG.info(messageLog + ce.getMessage());
			response.setTitle(properties.RESPONSE_GENERIC_INFO_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			response.setMessage(ce.getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.CONFLICT);
		} catch(RuntimeException  e) {
			LOG.info(messageLog + e.getMessage());
			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
			response.setMessage(properties.RESPONSE_GENERIC_SAVE_ERROR_INTERNALSERVER_MESSAGE);
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			LOG.info(messageLog + "=====================================");
			LOG.info(messageLog + " FINAL DELETE TRANSACCIÓN");
			LOG.info(messageLog + "======================================");
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
	public List<Transaction> findExpensessByWorskpaceIdAndDateRange(@PathVariable("idWorkspace") Long idWorkspace, @RequestParam String dateBegin,
																				   @RequestParam String dateEnd) {
		return iTransactionService.findTransactionByWorskpaceIdAndDateRange(idWorkspace, dateBegin, dateEnd);
	}
	@GetMapping("/period/{idPeriod}/account/{idAccount}/expenses")
	public List<Transaction> findTransactionByAccountIdAndPeriodId( @PathVariable Long idPeriod, @PathVariable Long idAccount) {
		return iTransactionService.findTransactionByAccountIdAndPeriodId(idAccount, idPeriod);
	}
}

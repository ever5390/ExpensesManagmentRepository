package pe.com.erp.expensemanager.modules.account.controller;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.com.erp.expensemanager.exception.CustomException;
import pe.com.erp.expensemanager.modules.account.model.Account;
import pe.com.erp.expensemanager.modules.account.model.TypeStatusAccountOPC;
import pe.com.erp.expensemanager.modules.account.repository.AccountRepository;
import pe.com.erp.expensemanager.modules.account.services.interfaz.IAccountService;
import pe.com.erp.expensemanager.modules.categories.model.Category;
import pe.com.erp.expensemanager.properties.PropertiesExtern;
import pe.com.erp.expensemanager.shared.model.Response;

@RestController
@EnableTransactionManagement
@CrossOrigin(origins = {"http://localhost:4200", "*"})
@RequestMapping(path="/api/v1")
public class AccountController {
	private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

	@Autowired(required = true)
	PropertiesExtern properties;
	
	@Autowired
	IAccountService iaccountService;
	@Autowired
	private AccountRepository accountRepository;

	@GetMapping(path="/list-account/account-type/{idAccountType}")
	public List<Account> listAccountByIdAccountType(@PathVariable Long idAccountType) {
		return iaccountService.listAccountByIdAccountType(idAccountType);
	}
	
	@GetMapping(path="/period/{idPeriod}/accounts")
	public List<Account> listAccountByIdPeriod(@PathVariable Long idPeriod) {
		return iaccountService.listAccountByIdPeriod(idPeriod);
	}

	@GetMapping(path="/account/{id}")
	public ResponseEntity<Response> findById(@PathVariable("id") Long idAccount) {
		Response response = new Response();
		
		Account accountLocated = null;
		
		try {
			accountLocated = iaccountService.findById(idAccount);
			if(accountLocated == null) {
				response.setTitle(properties.RESPONSE_GENERIC_INFO_TITLE);
				response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
				response.setMessage(properties.RESPONSE_GENERIC_ERROR_NOTFOUND_MESSAGE);
				response.setObject(null);
				return new ResponseEntity<Response>(response, HttpStatus.NOT_FOUND);
			}
		} catch(NullPointerException  e) {
			response.setTitle(properties.RESPONSE_GENERIC_INFO_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			response.setMessage(properties.RESPONSE_GENERIC_ERROR_NOTFOUND_MESSAGE);
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.NOT_FOUND);
			
		}  catch (DataAccessException e) {
			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
			response.setMessage(properties.
			RESPONSE_GENERIC_UPDATE_ERROR_INTERNALSERVER_MESSAGE + " " +
			e.getMostSpecificCause().getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}	
		
		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_SUCCESS_FOUND_MESSAGE);
		response.setObject(accountLocated);
		
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
	@PostMapping(path="/account")
	public ResponseEntity<Response> save( @Valid @RequestBody Account accountRequest, BindingResult binding) {
		
		Response response = new Response();
		String ownerInfoMessage = "[X10598]";
		try {
			validationParamasAccount(accountRequest, ownerInfoMessage);
			response = iaccountService.save(accountRequest, ownerInfoMessage);
			if(!response.getStatus().equals(properties.RESPONSE_GENERIC_SUCCESS_STATUS)) {
				return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (CustomException ce) {
			LOG.info("SavecONTROLLER: " +  ce.getMessage());
			response.setTitle(properties.RESPONSE_GENERIC_INFO_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			response.setMessage(ce.getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    } catch (DataAccessException e) {
			response.setTitle(properties.RESPONSE_GENERIC_INFO_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			response.setMessage(properties.
					RESPONSE_GENERIC_UPDATE_ERROR_INTERNALSERVER_MESSAGE + " " +
					e.getMostSpecificCause().getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(RuntimeException  e) {
			LOG.info(" ::: ERROR SAVE ACCOUNT::: " + e.getMessage());
			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
			response.setMessage(properties.RESPONSE_GENERIC_SAVE_ERROR_INTERNALSERVER_MESSAGE + e.getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
	
	
	@PutMapping(path="/account/{id}")
	public ResponseEntity<Response> update(@RequestBody Account accountRequest, @PathVariable("id") Long idAccount, BindingResult binding) {
		Response response = new Response();
		String ownerInfoMessage = "[X10598]";
		try {
			validationParamasAccount(accountRequest, ownerInfoMessage);
			response = iaccountService.update(accountRequest, idAccount, ownerInfoMessage);
			if(!response.getStatus().equals(properties.RESPONSE_GENERIC_SUCCESS_STATUS)) {
				return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			return new ResponseEntity<Response>(response, HttpStatus.OK);
		} catch (CustomException ce) {
			response.setTitle(properties.RESPONSE_GENERIC_INFO_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			response.setMessage(ce.getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (DataAccessException e) {
			response.setTitle(properties.RESPONSE_GENERIC_INFO_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			response.setMessage(properties.
			RESPONSE_GENERIC_UPDATE_ERROR_INTERNALSERVER_MESSAGE + " " +
			e.getMostSpecificCause().getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(RuntimeException  e) {
			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
			response.setMessage(e.getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@DeleteMapping(path="/account/{id}")
	public ResponseEntity<Response> delete( @PathVariable("id") Long id) {
		
		Response response = new Response();
		String ownerInfoMessage = "[X10598] ACCOUNT :: ";
		try {
			response = iaccountService.deleteAccountById(id, ownerInfoMessage);
			if(!response.getStatus().equals(properties.RESPONSE_GENERIC_SUCCESS_STATUS))
				return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (CustomException ce) {
			response.setTitle(properties.RESPONSE_GENERIC_INFO_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			response.setMessage(ce.getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(RuntimeException  e) {
			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
			response.setMessage(e.getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@GetMapping(path="/account-parent/{id}/categories-not-assoc")
	public List<Category> findCategoriesNotAssocToAccount(@PathVariable("id") Long idAccountParent) {
		String ownerInfoMessage = "[X10598] ACCOUNT :: ";
		return iaccountService.findCategoriesNotAssocToAccount(idAccountParent, ownerInfoMessage);
	}

	private void validationParamasAccount(Account accountRequestValidation, String messageLog) throws CustomException{
		if(accountRequestValidation.getAccountName().isEmpty()){
			LOG.error(messageLog + " ::: ACCOUNT ACCOUNTNAME IS EMPTY  ::: ");
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_ACCOUNTNAME_ISEMPTY);
		}
		if(accountRequestValidation.getAccountType() != null && accountRequestValidation.getAccountType().getTypeName().isEmpty()){
			LOG.error(messageLog + " ::: ACCOUNT ACCOUNTYPE IS EMPTY  ::: ");
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_ACCOUNTTYPE_ISEMPTY);
		}
		if(accountRequestValidation.getFinancialEntity() != null && accountRequestValidation.getFinancialEntity().getName().isEmpty()){
			LOG.error(messageLog + " ::: ACCOUNT FINANCIALENTITY IS EMPTY  ::: ");
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_FINANCIALENTITY_ISEMPTY);
		}
		if(accountRequestValidation.getTypeCard() != null && accountRequestValidation.getTypeCard().getName().isEmpty()){
			LOG.error(messageLog + " ::: ACCOUNT TYPECARD IS EMPTY  ::: ");
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_TYPECARD_ISEMPTY);
		}
	}

}

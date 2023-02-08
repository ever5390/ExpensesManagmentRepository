package pe.com.erp.expensemanager.modules.account.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
import org.springframework.web.bind.annotation.RestController;

import pe.com.erp.expensemanager.exception.CustomException;
import pe.com.erp.expensemanager.modules.account.model.Transference;
import pe.com.erp.expensemanager.modules.account.model.TransferenceRequest;
import pe.com.erp.expensemanager.modules.account.services.interfaz.ITransferenceService;
import pe.com.erp.expensemanager.properties.PropertiesExtern;
import pe.com.erp.expensemanager.shared.model.Response;

@RestController
@EnableTransactionManagement
@CrossOrigin(origins = {"http://localhost:4200", "*"})
@RequestMapping(path="/api/v1")
public class TransferenceController {
	private static final Logger LOG = LoggerFactory.getLogger(TransferenceController.class);
	
	@Autowired 
	PropertiesExtern properties;
	
	@Autowired
	ITransferenceService itransferenceService;
	
	@GetMapping(path="period/{idPeriod}/transferences")
	public List<Transference> listAllCategoryByIdPeriod(@PathVariable Long idPeriod) {
		return itransferenceService.listTransferencesByIdPeriod(idPeriod);
	}
	@GetMapping(path="period/{idPeriod}/account/{idAccount}/transferences")
	public List<Transference> listTransferencesByIdAccountAndIdPeriod(Long idPeriod, Long idAccount) {
		return itransferenceService.listTransferencesByIdAccountAndIdPeriod(idAccount, idPeriod);
	}

	@PostMapping(path="/transference")
	public ResponseEntity<Response> save(@RequestBody Transference transferenceRequest) {
		
		Response response = new Response();
		String ownerInfoMessage = "[X10598] TRANSFERENCE :: ";
		try {
			response = itransferenceService.saveTransference(transferenceRequest, ownerInfoMessage);

		} catch (CustomException e) {
			response.setTitle(properties.RESPONSE_GENERIC_INFO_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			response.setMessage(e.getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (DataAccessException e) {
			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
			response.setMessage(properties.
						  RESPONSE_GENERIC_SAVE_ERROR_INTERNALSERVER_MESSAGE + " " +
						  e.getMostSpecificCause().getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(RuntimeException  e) {
			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
			response.setMessage(properties.RESPONSE_GENERIC_SAVE_ERROR_INTERNALSERVER_MESSAGE);
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		LOG.error(ownerInfoMessage + " ::: TRANSFERENCE SAVE END  ::: ");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@DeleteMapping("/transference/{id}")
	public ResponseEntity<Response> deleteRegisterTransference(@PathVariable Long idTransference) {
		Response response = new Response();
		String ownerInfoMessage = "[X10598] TRANSFERENCE :: ";
		try {
			response = itransferenceService.deleteTransferenceByIdTransfer(idTransference, ownerInfoMessage);
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

		LOG.error(ownerInfoMessage + " ::: TRANSFERENCE DELETE END  ::: ");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@PutMapping(path="/transference/{idTransference}")
	ResponseEntity<Response> updateTransferenceByIdTransference(@RequestBody Transference transferenceUpdateRequest, @PathVariable Long idTransference) {
		Response response = new Response();
		String ownerInfoMessage = "[X10598] TRANSFERENCE :: ";
		try {
			response = itransferenceService.updateTransferenceByIdTransference(transferenceUpdateRequest, idTransference, ownerInfoMessage);

		} catch (CustomException e) {
			response.setTitle(properties.RESPONSE_GENERIC_INFO_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			response.setMessage(e.getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (DataAccessException e) {
			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
			response.setMessage(properties.
					RESPONSE_GENERIC_SAVE_ERROR_INTERNALSERVER_MESSAGE + " " +
					e.getMostSpecificCause().getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(RuntimeException  e) {
			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
			response.setMessage(properties.RESPONSE_GENERIC_SAVE_ERROR_INTERNALSERVER_MESSAGE);
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		LOG.error(ownerInfoMessage + " ::: TRANSFERENCE SAVE END  ::: ");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

}

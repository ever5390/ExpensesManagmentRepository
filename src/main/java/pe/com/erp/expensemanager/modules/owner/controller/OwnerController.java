package pe.com.erp.expensemanager.modules.owner.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.com.erp.expensemanager.modules.owner.dao.DaoOwner;
import pe.com.erp.expensemanager.modules.owner.model.Owner;
import pe.com.erp.expensemanager.modules.owner.services.interfaz.IOwnerService;
import pe.com.erp.expensemanager.properties.PropertiesExtern;
import pe.com.erp.expensemanager.shared.model.Response;

@RestController
@RequestMapping(path="/api/v1")
@EnableTransactionManagement
public class OwnerController {
	
	private static final Logger logger = LoggerFactory.getLogger(OwnerController.class);
	
	@Autowired 
	PropertiesExtern properties;
	 
	@Autowired
	IOwnerService iOwnerService;
	
	@GetMapping(path="/owner")
	public List<Owner> findAll() {
		return iOwnerService.findAll();
	}
	
	@GetMapping(path="/specific-owner")
	public List<DaoOwner> getAllOwnerData() {
		return iOwnerService.getAllOwnerData();
	}
	
	@GetMapping(path="/owner/{id}")
	public ResponseEntity<Response> findById(@PathVariable("id") Long idOwner) {
		
		Response response = new Response();
		Owner ownerLocated = null;
		
		try { 
			ownerLocated = iOwnerService.findById(idOwner);
			
		} catch(NullPointerException  e) {
			
			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			response.setMessage(properties.RESPONSE_GENERIC_ERROR_NOTFOUND_MESSAGE);
			 
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.NOT_FOUND);
			
		} catch (DataAccessException e) {
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
		 
		response.setObject(ownerLocated);
		
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
	@PostMapping(path="/owner")
	public ResponseEntity<Response> save(@RequestBody Owner ownerRequest) {
		
		Response response = new Response();
		Owner ownerCreated = null;
		
		try {
			ownerCreated = iOwnerService.save(ownerRequest);
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
		
		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_SAVE_SUCCESS_MESSAGE);
		response.setObject(ownerCreated);
		
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
	@PostMapping(path="/owner/{id}")
	public ResponseEntity<Response> update(@RequestBody Owner ownerRequest, @PathVariable("id") Long idOwner) {
		
		Response response = new Response();
		Owner ownerLocated = null;
		Owner ownerUpdated = null;
		
		try {
			ownerLocated = iOwnerService.findById(idOwner);
			ownerLocated.setName(ownerRequest.getName());
			ownerLocated.setEmail(ownerRequest.getEmail());
			ownerLocated.setImage(ownerRequest.getImage());
			ownerUpdated =  iOwnerService.save(ownerLocated);
			
		} catch(NullPointerException  e) {
			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			response.setMessage(properties.RESPONSE_GENERIC_ERROR_NOTFOUND_MESSAGE);
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.NOT_FOUND);
	
		} catch (DataAccessException e) {
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
		response.setMessage(properties.RESPONSE_GENERIC_UPDATE_SUCCESS_MESSAGE);
		response.setObject(ownerUpdated);
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

}


package pe.com.erp.expensemanager.modules.workspace.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.com.erp.expensemanager.modules.workspace.model.TypeWorkSpace;
import pe.com.erp.expensemanager.modules.workspace.services.interfaz.ITypeWorkspaceService;
import pe.com.erp.expensemanager.properties.PropertiesExtern;
import pe.com.erp.expensemanager.shared.model.Response;

@RestController
@EnableTransactionManagement
@RequestMapping(path="/api/v1")
public class TypeWorkspaceController {

private static final Logger logger = LoggerFactory.getLogger(TypeWorkspaceController.class);
	
	@Autowired 
	PropertiesExtern properties;
	
	@Autowired
	ITypeWorkspaceService iTypeWorkspaceService;
	
	
	@PostMapping(path="/typeworkspace")
	public ResponseEntity<Response> save(TypeWorkSpace workspaceRequest) {
		
		Response response = new Response();
		
		TypeWorkSpace typeWorkSpaceCreated = null;
		try {

			typeWorkSpaceCreated = iTypeWorkspaceService.save(workspaceRequest);
			
		} catch (DataAccessException e) {
			
			  response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			  response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
			  response.setMessage(properties.
			  RESPONSE_GENERIC_SAVE_ERROR_INTERNALSERVER_MESSAGE + " " +
			  e.getMostSpecificCause().getMessage());
			 
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_SAVE_SUCCESS_MESSAGE);
		 
		response.setObject(typeWorkSpaceCreated);
		
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
	@PostMapping(path="/typeworkspace/{id}")
	public ResponseEntity<Response> update(@RequestBody TypeWorkSpace typeWorkSpaceRequest, @PathVariable("id") Long idTypeWorkSpace) {
		
		Response response = new Response();
		
		TypeWorkSpace typelocated = iTypeWorkspaceService.findById(idTypeWorkSpace);
		TypeWorkSpace typeUpdated = null;
		
		if(typelocated == null) {
			
			  response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			  response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			  response.setMessage(properties.RESPONSE_GENERIC_ERROR_NOTFOUND_MESSAGE);
			 
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.NOT_FOUND);
		}
		
		try {
			typelocated.setTypeName(typeWorkSpaceRequest.getTypeName());
			typeUpdated =  iTypeWorkspaceService.save(typelocated);
			
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
		 
		response.setObject(typeUpdated);
		
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
	@GetMapping("/typeworkspace/{id}")
	public ResponseEntity<Response> findById(@PathVariable Long id) {
		Response response = new Response();
		
		TypeWorkSpace typeWorkSpaceLocated = iTypeWorkspaceService.findById(id);
		
		if(typeWorkSpaceLocated == null) {
			
			  response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			  response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			  response.setMessage(properties.RESPONSE_GENERIC_ERROR_NOTFOUND_MESSAGE);
			 
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.NOT_FOUND);
		}
		
		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_SUCCESS_FOUND_MESSAGE);
		 
		response.setObject(typeWorkSpaceLocated);
		
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@GetMapping("/typeworkspace")
	public List<TypeWorkSpace> listAllWorkspace() {
		return iTypeWorkspaceService.listAllWorkspace();
	}
}

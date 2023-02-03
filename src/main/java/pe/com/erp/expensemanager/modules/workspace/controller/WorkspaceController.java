package pe.com.erp.expensemanager.modules.workspace.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.com.erp.expensemanager.modules.workspace.model.WorkSpace;
import pe.com.erp.expensemanager.modules.workspace.services.interfaz.IWorkspaceService;
import pe.com.erp.expensemanager.properties.PropertiesExtern;
import pe.com.erp.expensemanager.shared.model.Response;

@RestController
@EnableTransactionManagement
@RequestMapping(path="/api/v1")
public class WorkspaceController {

	private static final Logger logger = LoggerFactory.getLogger(WorkspaceController.class);
	
	@Autowired 
	PropertiesExtern properties;
	
	@Autowired
	IWorkspaceService iworkspaceService;
	
	@PostMapping(path="/workspace")
	public ResponseEntity<Response> save(@RequestBody WorkSpace workspaceRequest) {
		
		Response response = new Response();	
		WorkSpace workSpaceCreated = null;
		
		try {
			workSpaceCreated = iworkspaceService.save(workspaceRequest);
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
		response.setObject(workSpaceCreated);
		
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
	@PutMapping(path="/workspace/{id}")
	public ResponseEntity<Response> update(@RequestBody WorkSpace workspaceRequest, @PathVariable("id") Long idWorkspace) {
		
		Response response = new Response();	
		WorkSpace workSpaceUpdated = null;
		WorkSpace workSpaceLocated = null;
		
		try {
			workSpaceLocated = iworkspaceService.findById(idWorkspace);
			workSpaceLocated.setName(workspaceRequest.getName());
			workSpaceLocated.setTypeWSPC(workspaceRequest.getTypeWSPC());
			workSpaceUpdated = iworkspaceService.save(workSpaceLocated);
			
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
		response.setObject(workSpaceUpdated);
		
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@DeleteMapping(path="/workspace/{id}")
	public void delete( @PathVariable("id") Long id) {
		iworkspaceService.deleteById(id);
	}
	
	@GetMapping(path="/workspace/owner/{id}")
	public List<WorkSpace> listWorkspaceByIdOwner( @PathVariable("id") Long IdOwner) {
		
		return iworkspaceService.listWorkspaceByIdOwner(IdOwner);
	}
	
	@GetMapping("/workspace/{id}")
	public ResponseEntity<Response> findById(@PathVariable Long id) {
		Response response = new Response();
		WorkSpace workSpaceLocated = null;
				
		try { 
			workSpaceLocated = iworkspaceService.findById(id);
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
		response.setObject(workSpaceLocated);
		
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
}

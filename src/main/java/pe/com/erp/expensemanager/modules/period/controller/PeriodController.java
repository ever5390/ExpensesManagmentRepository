package pe.com.erp.expensemanager.modules.period.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.com.erp.expensemanager.exception.CustomException;
import pe.com.erp.expensemanager.modules.period.dao.PeriodDetailDao;
import pe.com.erp.expensemanager.modules.period.model.Period;
import pe.com.erp.expensemanager.modules.period.services.interfaz.IPeriodService;
import pe.com.erp.expensemanager.properties.PropertiesExtern;
import pe.com.erp.expensemanager.shared.model.Response;

@EnableTransactionManagement
@CrossOrigin(origins=  {"http://localhost:4200", "*"})
@RequestMapping(path="/api/v1")
@RestController
public class PeriodController {
	
	private static final Logger LOG = LoggerFactory.getLogger(PeriodController.class);
	@Autowired 
	PropertiesExtern properties;
	
	@Autowired
	IPeriodService iperiodService;
	
	@GetMapping(path="/workspace/{idWorkspace}/periods")
	public List<Period> listAllPeriodByIdWorkspace(@PathVariable Long idWorkspace) {
		return iperiodService.listPeriodByIdWorkspace(idWorkspace);
	}
	
	@GetMapping(path="/workspace/{idWorkspace}/list-period-detail")
	public List<PeriodDetailDao> listPeriodDetailsHeaderByIdWorkspace(@PathVariable Long idWorkspace) {
		return iperiodService.listPeriodDetailsHeaderByIdWorkspace(idWorkspace);
	}
	
	@GetMapping(path="owner/{idOwner}/period/{idPeriod}/period-detail")
	public PeriodDetailDao periodDetailsHeaderByIdPeriod(@PathVariable Long idPeriod, @PathVariable Long idOwner) {
		PeriodDetailDao periodFound = new PeriodDetailDao();
		try {
			periodFound = iperiodService.periodDetailsHeaderByIdPeriod(idPeriod, idOwner);
			 	
		} catch(CustomException  e) {
			LOG.info(e.getMessage());
			return periodFound;
		}
		
		return periodFound;
	}
	
	@PostMapping(path="/period/close-period")
	public ResponseEntity<Response> closePeriod(@RequestBody Period periodRequest) {
		
		Response response = new Response();
		try {
			response = iperiodService.closePeriod(periodRequest);
			if(!response.getStatus().equals(properties.RESPONSE_GENERIC_SUCCESS_STATUS))
				return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
			
		} catch(RuntimeException  e) {
			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
			response.setMessage(properties.RESPONSE_GENERIC_SAVE_ERROR_INTERNALSERVER_MESSAGE);
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
	@PostMapping(path="/period")
	public ResponseEntity<Response> save(@RequestBody Period periodRequest) {
		
		Response response = new Response();
		Period periodCreated = null;
		
		try {
			periodCreated = iperiodService.save(periodRequest);
		} catch (CustomException ce) {
			response.setTitle(properties.RESPONSE_GENERIC_INFO_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
			response.setMessage(ce.getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.CONFLICT);			

		} catch (RuntimeException e) {
		    response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
		    response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
		    response.setMessage(properties.RESPONSE_GENERIC_SAVE_ERROR_INTERNALSERVER_MESSAGE + " " +
		    		e.getMessage());
			response.setObject(null);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_SAVE_SUCCESS_MESSAGE);
		response.setObject(periodCreated);
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
	
	@GetMapping(path="/period/{id}")
	public ResponseEntity<Response> findById(@PathVariable("id") Long idPeriod) {
		Response response = new Response();
		Period periodLocated = iperiodService.findByIdPeriod(idPeriod);
		
		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_SUCCESS_FOUND_MESSAGE);
		response.setObject(periodLocated);
		
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
	@PutMapping(path="/period/{id}")
	public ResponseEntity<Response> update(@RequestBody Period periodRequest, @PathVariable("id") Long idPeriod) {
		
		Response response = new Response();
		Period periodLocated = new Period();		
		try {
			
			periodLocated = iperiodService.findByIdPeriod(idPeriod);			
			periodLocated.setFinalDate(periodRequest.getFinalDate());
			periodLocated =  iperiodService.update(periodLocated);			
		} catch(NullPointerException  e) {
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
		response.setObject(periodLocated);
		
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

}

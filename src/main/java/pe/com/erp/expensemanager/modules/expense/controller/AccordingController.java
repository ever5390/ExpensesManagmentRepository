package pe.com.erp.expensemanager.modules.expense.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.com.erp.expensemanager.modules.expense.model.According;
import pe.com.erp.expensemanager.modules.expense.services.interfaz.IAccordingService;
import pe.com.erp.expensemanager.properties.PropertiesExtern;

@RestController
@CrossOrigin(origins= {"http://localhost:4200", "*"})
@RequestMapping(path="/api/v1")
public class AccordingController {
	
	@Autowired 
	PropertiesExtern properties;
	
	@Autowired
	IAccordingService iAccountService;
	
	@GetMapping(path="/according")
	public List<According> listAccording() {
		return iAccountService.listAllAccording();
	}
	
	

}

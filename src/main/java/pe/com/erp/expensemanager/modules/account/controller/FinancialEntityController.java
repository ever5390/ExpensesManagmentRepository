package pe.com.erp.expensemanager.modules.account.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;
import pe.com.erp.expensemanager.modules.account.model.FinancialEntity;
import pe.com.erp.expensemanager.modules.account.services.interfaz.IFinancialEntityService;
import pe.com.erp.expensemanager.properties.PropertiesExtern;

import java.util.List;

@RestController
@EnableTransactionManagement
@CrossOrigin(origins= {"http://localhost:4200", "*"})
@RequestMapping(path="/api/v1")
public class FinancialEntityController {
	
	private static final Logger LOG = LoggerFactory.getLogger(FinancialEntityController.class);

	@Autowired
	PropertiesExtern properties;

	@Autowired
	IFinancialEntityService iFinancialEntityService;


	@GetMapping(path="/owner/{idOwner}/financial-entity")
	public List<FinancialEntity> findAllFinancialEntityByOwnerId(@PathVariable Long idOwner) {
		return iFinancialEntityService.findAllFinancialEntityByOwnerId(idOwner);
	}

}

package pe.com.erp.expensemanager.modules.account.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;
import pe.com.erp.expensemanager.modules.account.model.TypeCard;
import pe.com.erp.expensemanager.modules.account.services.interfaz.ITypeCardService;
import pe.com.erp.expensemanager.properties.PropertiesExtern;

import java.util.List;

@RestController
@EnableTransactionManagement
@CrossOrigin(origins= {"http://localhost:4200", "*"})
@RequestMapping(path="/api/v1")
public class TypeCardController {
	
	private static final Logger LOG = LoggerFactory.getLogger(TypeCardController.class);

	@Autowired 
	PropertiesExtern properties;
	
	@Autowired
	ITypeCardService iTypeCardService;
	
	
	@GetMapping(path="/owner/{idOwner}/type-card")
	public List<TypeCard> listAllTypeCardByIdOwnerId(@PathVariable Long idOwner) {
		return iTypeCardService.findAllTypeCardByOwnerId(idOwner);
	}

	@PostMapping(path="/type-card")
	public TypeCard saveTypeCard(@RequestBody TypeCard typeCardRequest) {
		return iTypeCardService.saveTypeCard(typeCardRequest);
	}

}

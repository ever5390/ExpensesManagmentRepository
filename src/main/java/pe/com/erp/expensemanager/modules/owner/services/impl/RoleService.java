package pe.com.erp.expensemanager.modules.owner.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.com.erp.expensemanager.modules.owner.model.Role;
import pe.com.erp.expensemanager.modules.owner.repository.IRolRepository;

@Service
public class RoleService {
	
	@Autowired
	IRolRepository irepository;
	
	public Optional<Role> findByRolNombre(String rolNombre) {
		
		return irepository.findByName(rolNombre);
		
	}

}

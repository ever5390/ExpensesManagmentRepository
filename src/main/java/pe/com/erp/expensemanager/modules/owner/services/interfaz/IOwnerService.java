package pe.com.erp.expensemanager.modules.owner.services.interfaz;

import java.util.List;

import pe.com.erp.expensemanager.modules.owner.dao.DaoOwner;
import pe.com.erp.expensemanager.modules.owner.model.Owner;
import pe.com.erp.expensemanager.shared.model.Response;


public interface IOwnerService {
	
	List<Owner> findAll();
	
	Owner findById(Long Id);
			
	Owner save(Owner ownerRequest);
		
	List<DaoOwner> getAllOwnerData();
	
	
	
	
	List<Owner> findByActive(Boolean active);
	
	Owner updateActiveOwner(Owner owner);
		
	Owner updateOwner(Owner owner);
	
	void deleteOwnerById(Long id);
			
	Owner findByUsername(String username);
	
	Response findByUsernameOrEmail(String usernameOrEmail);
	
	boolean existsByUsername(String nombreUsurio);
	
	boolean existsByEmail(String email);
}

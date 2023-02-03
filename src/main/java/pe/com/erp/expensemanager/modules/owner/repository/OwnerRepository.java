package pe.com.erp.expensemanager.modules.owner.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pe.com.erp.expensemanager.modules.owner.model.Owner;

@Repository
public interface OwnerRepository extends CrudRepository<Owner, Long>{

	Owner findByEmail(String email);
	
	Owner findByNameAndEmail(String name, String email);
	
	@Query("select o from Owner o where o.name = ?1")
	Owner findByName(String name);
	
	@Query("select o from Owner o where o.username = ?1")
	Owner findByUsername(String username);
	
	@Query("select o from Owner o where o.username = ?1 or o.email = ?1")
	Owner findByUsernameOrEmail(String usernameOrEmail);
	
	boolean existsByUsername(String nombreUsurio);
	
	boolean existsByEmail(String email);
	
	@Query("SELECT o FROM Owner o")
	List<Owner> getAllOwnerData();
}

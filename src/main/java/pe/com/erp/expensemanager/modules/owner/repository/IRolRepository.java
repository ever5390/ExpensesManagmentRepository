package pe.com.erp.expensemanager.modules.owner.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pe.com.erp.expensemanager.modules.owner.model.Role;

@Repository
public interface IRolRepository extends CrudRepository<Role, Long>{

	Optional<Role> findByName(String rolNombre);
}

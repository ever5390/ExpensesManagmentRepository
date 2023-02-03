package pe.com.erp.expensemanager.modules.workspace.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pe.com.erp.expensemanager.modules.workspace.model.TypeWorkSpace;

@Repository
public interface TypeWorkspaceRepository extends CrudRepository<TypeWorkSpace, Long>{

}

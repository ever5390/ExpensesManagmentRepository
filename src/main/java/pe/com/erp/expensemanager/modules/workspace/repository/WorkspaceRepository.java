package pe.com.erp.expensemanager.modules.workspace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pe.com.erp.expensemanager.modules.workspace.model.WorkSpace;

@Repository
public interface WorkspaceRepository extends CrudRepository<WorkSpace, Long>{

	@Query("SELECT w FROM WorkSpace w where w.owner.id= :idOwner")
    List<WorkSpace> findAllByIdOwner(Long idOwner);
}

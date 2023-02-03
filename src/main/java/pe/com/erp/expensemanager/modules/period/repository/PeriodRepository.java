package pe.com.erp.expensemanager.modules.period.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pe.com.erp.expensemanager.modules.period.model.Period;

@Repository
public interface PeriodRepository extends CrudRepository<Period, Long>{


	@Query("SELECT p FROM Period p WHERE p.workspace.id =?1 and p.statusPeriod=?2")
	Period periodByWorkspaceIdAndStatusPeriod(Long workspaceId, boolean status);
	
	@Query("SELECT p FROM Period p WHERE p.workspace.id =:idWorkspace ORDER BY p.startDate DESC")
	List<Period> listAllPeriodSummaryByWorkspaceId(Long idWorkspace);	
	
	@Query("SELECT p FROM Period p WHERE p.id = :idPeriod and p.workspace.owner.id= :idOwner")
	Period findByPeriodIdAndOwnerId(Long idPeriod, Long idOwner);
	
	
	@Query("SELECT COUNT(p) FROM  Period p WHERE p.workspace.id =?1")
    long countPeriodByWorkspaceId(Long workspaceId);
	 
	 
}

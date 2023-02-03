package pe.com.erp.expensemanager.modules.workspace.services.interfaz;

import java.util.List;

import pe.com.erp.expensemanager.modules.workspace.model.WorkSpace;

public interface IWorkspaceService {

	WorkSpace save(WorkSpace workspace);
		
	void deleteById(Long id);
	
	WorkSpace findById(Long id);
	
	List<WorkSpace> listWorkspaceByIdOwner(Long IdOwner);
	
}

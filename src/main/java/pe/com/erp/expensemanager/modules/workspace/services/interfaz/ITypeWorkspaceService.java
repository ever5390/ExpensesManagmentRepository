package pe.com.erp.expensemanager.modules.workspace.services.interfaz;

import java.util.List;

import pe.com.erp.expensemanager.modules.workspace.model.TypeWorkSpace;

public interface ITypeWorkspaceService {

	TypeWorkSpace save(TypeWorkSpace workspaceRequest);

	TypeWorkSpace findById(Long idTypeWorkSpace);

	List<TypeWorkSpace> listAllWorkspace();

}

package pe.com.erp.expensemanager.modules.workspace.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.com.erp.expensemanager.modules.workspace.model.WorkSpace;
import pe.com.erp.expensemanager.modules.workspace.repository.WorkspaceRepository;
import pe.com.erp.expensemanager.modules.workspace.services.interfaz.IWorkspaceService;

@Service
public class WorkspaceServiceImpl implements IWorkspaceService {

	
	@Autowired
	WorkspaceRepository workspaceRepo;
	
	@Override
	public WorkSpace save(WorkSpace workspace) {
		return workspaceRepo.save(workspace);
	}

	@Override
	public void deleteById(Long id) {
		 workspaceRepo.deleteById(id);
	}

	@Override
	public List<WorkSpace> listWorkspaceByIdOwner(Long IdOwner) {
		return workspaceRepo.findAllByIdOwner(IdOwner);
	}

	@Override
	@Transactional(readOnly = true)
	public WorkSpace findById(Long id) {
		return workspaceRepo.findById(id).orElseGet(null);
	}

}

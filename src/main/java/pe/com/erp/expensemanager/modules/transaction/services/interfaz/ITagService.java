package pe.com.erp.expensemanager.modules.transaction.services.interfaz;

import java.util.List;

import pe.com.erp.expensemanager.modules.transaction.model.Tag;

public interface ITagService {

	Tag save(Tag tag);
		
	void deleteById(Long idTag);
	
	List<Tag> listTagByIdWorkspace(Long idWorkspace);
	
	List<Tag> listTagsByOwnerId(Long ownerId);
}

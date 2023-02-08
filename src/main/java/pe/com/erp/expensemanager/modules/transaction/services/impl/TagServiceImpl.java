package pe.com.erp.expensemanager.modules.transaction.services.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.com.erp.expensemanager.modules.transaction.model.Tag;
import pe.com.erp.expensemanager.modules.transaction.repository.TagRepository;
import pe.com.erp.expensemanager.modules.transaction.services.interfaz.ITagService;

@Service
public class TagServiceImpl implements ITagService {

	@Autowired
	TagRepository tagRepo;
	
	@Override
	@Transactional
	public Tag save(Tag tag) {
		return tagRepo.save(tag);
	}
	
	@Override
	public void deleteById(Long idTag) {
		tagRepo.deleteById(idTag);
		
	}

	@Override
	public List<Tag> listTagByIdWorkspace(Long idWorkspace) {
		// TODO Auto-generated method stub
		return (List<Tag>) tagRepo.findAll();
	}

	@Override
	public List<Tag> listTagsByOwnerId(Long ownerId) {
		
		return tagRepo.listTagsByOwnerId(ownerId);
	}

}

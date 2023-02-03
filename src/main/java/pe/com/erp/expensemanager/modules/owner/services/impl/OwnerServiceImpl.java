package pe.com.erp.expensemanager.modules.owner.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.com.erp.expensemanager.modules.categories.model.Category;
import pe.com.erp.expensemanager.modules.categories.model.generic.CategoryGeneric;
import pe.com.erp.expensemanager.modules.categories.repository.CategoryRepository;
import pe.com.erp.expensemanager.modules.categories.repository.generic.CategoryGenericRepository;
import pe.com.erp.expensemanager.modules.owner.dao.DaoOwner;
import pe.com.erp.expensemanager.modules.owner.model.Owner;
import pe.com.erp.expensemanager.modules.owner.model.Role;
import pe.com.erp.expensemanager.modules.owner.repository.OwnerRepository;
import pe.com.erp.expensemanager.modules.owner.services.interfaz.IOwnerService;
import pe.com.erp.expensemanager.modules.period.model.Period;
import pe.com.erp.expensemanager.modules.period.repository.PeriodRepository;
import pe.com.erp.expensemanager.modules.workspace.model.WorkSpace;
import pe.com.erp.expensemanager.modules.workspace.repository.TypeWorkspaceRepository;
import pe.com.erp.expensemanager.modules.workspace.repository.WorkspaceRepository;
import pe.com.erp.expensemanager.properties.PropertiesExtern;
import pe.com.erp.expensemanager.shared.model.Response;
import pe.com.erp.expensemanager.utils.Utils;

@Service
@Transactional
public class OwnerServiceImpl implements IOwnerService {
		
	private static final Logger logger = LoggerFactory.getLogger(OwnerServiceImpl.class);

	@Autowired
	PropertiesExtern properties;
	
	@Autowired
	OwnerRepository ownerRepository;
	
	//@Autowired
	//BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	RoleService rolservice;
	
	@Autowired
	CategoryGenericRepository categGenericRepo;
	
	@Autowired
	CategoryRepository categOwnerRepo;

	@Autowired
	PeriodRepository periodRepo;
	
	@Autowired
	WorkspaceRepository workspaceRepo;
	
	@Autowired
	TypeWorkspaceRepository typeWrkspc;
	
	@Override
	@Transactional(readOnly = true)
	public List<Owner> findAll() {
		return (List<Owner>) ownerRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Owner findById(Long Id) {
		return ownerRepository.findById(Id).orElseGet(null);
	}

	@Override
	@Transactional
	public Owner save(Owner ownerRequest) {
		
		Owner ownerSave = new Owner();
		List<Role> roleList = new ArrayList<Role>();
		List<CategoryGeneric> listMasterCAtegories = new ArrayList<CategoryGeneric>();

		roleList.add(rolservice.findByRolNombre("ROLE_USER").get());
		if(ownerRequest.getRoles()!=null && ownerRequest.getRoles().size() > 0) {
			roleList.add(rolservice.findByRolNombre("ROLE_ADMIN").get());
		}
		
		//String passwordBcrypot = bCryptPasswordEncoder.encode(ownerRequest.getPassword());
		
		ownerSave = new Owner();
		ownerSave.setName(ownerRequest.getName());
		ownerSave.setEmail(ownerRequest.getEmail());
		ownerSave.setEnabled(true);
		ownerSave.setImage(ownerRequest.getImage());
		ownerSave.setUsername(ownerRequest.getUsername());
		//ownerSave.setPassword(passwordBcrypot);
		ownerSave.setRoles(roleList);
		
		//Almacenando Owner
		ownerSave = ownerRepository.save(ownerSave);
		
		listMasterCAtegories = categGenericRepo.findAll();

		for(CategoryGeneric categoria :listMasterCAtegories) { 
		  Category catOwner = new  Category(); 
		  catOwner.setActive(false);
		  catOwner.setName(categoria.getName());
		  catOwner.setImage(categoria.getImage());
		  catOwner.setOwner(ownerSave); 
		  categOwnerRepo.save(catOwner);
	    }

		WorkSpace workspaceDefault = new WorkSpace();
		workspaceDefault.setActive(true);
		workspaceDefault.setName("Workspace");
		workspaceDefault.setTypeWSPC(typeWrkspc.findById(1L).orElse(null));
		workspaceDefault.setOwner(ownerSave);
		workspaceDefault = workspaceRepo.save(workspaceDefault);
		
		Period periodSummarySave = new Period();
		periodSummarySave.setStartDate(new Date());
		periodSummarySave.setFinalDate(Utils.getNextLocalDate(new Date(),"final", 1));
		periodSummarySave.setActivate(false);
		periodSummarySave.setStatusPeriod(true);
		periodSummarySave.setWorkspace(workspaceDefault);
		periodRepo.save(periodSummarySave);

		return ownerSave;
	}

	@Override
	public List<Owner> findByActive(Boolean active) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Owner updateActiveOwner(Owner owner) {
		Owner acc  = findById(owner.getId());
		if(acc != null ) {
			acc.setEnabled(owner.getEnabled());		
			return ownerRepository.save(acc);	
		}
		return acc;
	}

	@Override
	@Transactional
	public Owner updateOwner(Owner owner) {
		// TODO Auto-generated method stub
		return ownerRepository.save(owner);
	}

	@Override
	@Transactional
	public void deleteOwnerById(Long id) {
		ownerRepository.deleteById(id);			
	}

	@Override
	public Owner findByUsername(String username) {
		// TODO Auto-generated method stub
		return ownerRepository.findByUsername(username);
	}

	@Override
	public Response findByUsernameOrEmail(String usernameOrEmail) {
		Response response = new Response();
		List<Owner> ownerList = new ArrayList<Owner>();
		Owner owner = new Owner();
		try {
			owner = ownerRepository.findByUsernameOrEmail(usernameOrEmail);
			if(owner == null) {
				response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
				response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
				response.setMessage(properties.RESPONSE_CUSTOMIZED_OWNER_ERROR_SEARCHBYUSERNAME);
				response.setObject(null);
				return response;
			}
			
			ownerList.add(owner);
			response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
			response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
			response.setMessage(properties.RESPONSE_CUSTOMIZED_OWNER_SUCCESS_SEARCHBYUSERNAME);
			response.setObject(ownerList);

		} catch (Exception e) {
			response.setTitle(properties.RESPONSE_GENERIC_ERROR_TITLE);
			response.setMessage("error: "+ e.getMessage());
			response.setStatus(properties.RESPONSE_GENERIC_ERROR_STATUS);
		}

		return response;
	}

	@Override
	public boolean existsByUsername(String nombreUsurio) {
		// TODO Auto-generated method stub
		return ownerRepository.existsByUsername(nombreUsurio);
	}

	@Override
	public boolean existsByEmail(String email) {
		// TODO Auto-generated method stub
		return ownerRepository.existsByEmail(email);
	}

	@Override
	public List<DaoOwner> getAllOwnerData() {
		
		DaoOwner daoOwner = null;
		List<DaoOwner> listDaoOwner = new ArrayList<DaoOwner>();
		List<Owner> listOwner = new ArrayList<Owner>();
		
		try {
			listOwner = ownerRepository.getAllOwnerData();
			for (Owner owner : listOwner) {
				daoOwner = new DaoOwner();
				daoOwner.setId(owner.getId());
				daoOwner.setName(owner.getName());
				listDaoOwner.add(daoOwner);
			}
		} catch (Exception e) {
			listDaoOwner = null;
		}
		
		return listDaoOwner;
	}
	
	

}

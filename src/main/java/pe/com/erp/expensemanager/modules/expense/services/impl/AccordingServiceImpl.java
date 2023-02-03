package pe.com.erp.expensemanager.modules.expense.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.com.erp.expensemanager.modules.expense.model.According;
import pe.com.erp.expensemanager.modules.expense.repository.AccordingRepository;
import pe.com.erp.expensemanager.modules.expense.services.interfaz.IAccordingService;

@Service
public class AccordingServiceImpl implements IAccordingService {

	@Autowired
	AccordingRepository accordingRepo;
	
	@Override
	public List<According> listAllAccording() {

		return (List<According>) accordingRepo.findAll();
	}

}

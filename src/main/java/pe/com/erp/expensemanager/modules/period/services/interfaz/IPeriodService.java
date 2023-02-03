package pe.com.erp.expensemanager.modules.period.services.interfaz;

import java.util.List;

import pe.com.erp.expensemanager.modules.period.dao.PeriodDetailDao;
import pe.com.erp.expensemanager.modules.period.model.Period;
import pe.com.erp.expensemanager.shared.model.Response;

public interface IPeriodService {
	
	//Get Period OPEN by WwkspcId :: solo debe existir uno, el actual
	Period periodByWorkspaceIdAndStatusPeriod(Long workspaceId, boolean status);
	
	Period update(Period period);
	
	Period save(Period period);
	
	Period findByIdPeriod(Long idPeriod);
	
	List<PeriodDetailDao> listPeriodDetailsHeaderByIdWorkspace(Long idWorkspace);
	
	PeriodDetailDao periodDetailsHeaderByIdPeriod(Long idPeriod, Long idOwner);
	
	Response closePeriod(Period period);

	List<Period> listPeriodByIdWorkspace(Long idWorkspace);

}

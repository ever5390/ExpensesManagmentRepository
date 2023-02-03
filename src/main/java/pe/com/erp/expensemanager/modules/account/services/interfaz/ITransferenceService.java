package pe.com.erp.expensemanager.modules.account.services.interfaz;

import java.util.List;


import pe.com.erp.expensemanager.modules.account.model.Transference;
import pe.com.erp.expensemanager.shared.model.Response;

public interface ITransferenceService {

	Response save(Transference transference, String messageLog);
	
	List<Transference> listTransferencesByIdPeriod(Long idPeriod);

    Response deleteTransferenceById(Long id, String ownerInfoMessage);
}

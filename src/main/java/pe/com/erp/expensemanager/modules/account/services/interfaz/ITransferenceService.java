package pe.com.erp.expensemanager.modules.account.services.interfaz;

import java.util.List;


import pe.com.erp.expensemanager.modules.account.model.Transference;
import pe.com.erp.expensemanager.modules.account.model.TransferenceRequest;
import pe.com.erp.expensemanager.shared.model.Response;

public interface ITransferenceService {

	Response saveTransference(Transference transference, String messageLog);
	
	List<Transference> listTransferencesByIdPeriod(Long idPeriod);

	List<Transference> listTransferencesByIdAccountAndIdPeriod(Long idAccount, Long idPeriod);

    Response deleteTransferenceByIdTransfer(Long idTransference, String ownerInfoMessage);

	Response updateTransferenceByIdTransference(Transference transferenceUpdateRequest, Long idTransference, String ownerInfoMessage);
}

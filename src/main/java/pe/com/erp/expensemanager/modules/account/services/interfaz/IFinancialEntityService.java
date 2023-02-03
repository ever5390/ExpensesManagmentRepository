package pe.com.erp.expensemanager.modules.account.services.interfaz;

import pe.com.erp.expensemanager.modules.account.model.FinancialEntity;

import java.util.List;

public interface IFinancialEntityService {

	FinancialEntity save(FinancialEntity financialEntityRequest);

	FinancialEntity update(FinancialEntity financialEntityRequest, Long idFinancialEntity);

	void delete(Long idFinancialEntity);

	List<FinancialEntity> findAllFinancialEntityByOwnerId(Long idOwner);
}

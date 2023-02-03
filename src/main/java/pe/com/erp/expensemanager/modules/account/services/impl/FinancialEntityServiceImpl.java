package pe.com.erp.expensemanager.modules.account.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.erp.expensemanager.modules.account.model.FinancialEntity;
import pe.com.erp.expensemanager.modules.account.model.TypeCard;
import pe.com.erp.expensemanager.modules.account.repository.FinancialEntityRepository;
import pe.com.erp.expensemanager.modules.account.repository.TypeCardRepository;
import pe.com.erp.expensemanager.modules.account.services.interfaz.IFinancialEntityService;
import pe.com.erp.expensemanager.modules.account.services.interfaz.ITypeCardService;

import java.util.List;

@Service
public class FinancialEntityServiceImpl implements IFinancialEntityService {

    @Autowired
    FinancialEntityRepository financialEntityRepository;

    @Override
    public FinancialEntity save(FinancialEntity financialEntityRequest) {
        return null;
    }

    @Override
    public FinancialEntity update(FinancialEntity financialEntityRequest, Long idFinancialEntity) {
        return null;
    }

    @Override
    public void delete(Long idFinancialEntity) {

    }

    @Override
    public List<FinancialEntity> findAllFinancialEntityByOwnerId(Long idOwner) {
        return financialEntityRepository.findByOwnerId(idOwner);
    }
}

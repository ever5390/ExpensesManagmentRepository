package pe.com.erp.expensemanager.modules.account.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.erp.expensemanager.modules.account.model.TypeCard;
import pe.com.erp.expensemanager.modules.account.repository.TypeCardRepository;
import pe.com.erp.expensemanager.modules.account.services.interfaz.ITypeCardService;

import java.util.List;

@Service
public class TypeCardServiceImpl implements ITypeCardService {

    @Autowired
    TypeCardRepository typeCardRepository;

    @Override
    public List<TypeCard> findAllTypeCardByOwnerId(Long idOwnerId) {
        return typeCardRepository.findByOwnerId(idOwnerId);
    }

    @Override
    public TypeCard saveTypeCard(TypeCard typeCardRequest) {
        return typeCardRepository.save(typeCardRequest);
    }

    @Override
    public TypeCard save(TypeCard typeCard) {
        return null;
    }

    @Override
    public TypeCard update(TypeCard typeCard, Long idTypeCard) {
        return null;
    }

    @Override
    public void delete(Long idTypeCard) {

    }
}

package pe.com.erp.expensemanager.modules.account.services.interfaz;

import pe.com.erp.expensemanager.modules.account.model.TypeCard;

import java.util.List;

public interface ITypeCardService {

	TypeCard save(TypeCard typeCard);

	TypeCard update(TypeCard typeCard, Long idTypeCard);

	void delete(Long idTypeCard);

	List<TypeCard> findAllTypeCardByOwnerId(Long idOwner);

	TypeCard saveTypeCard(TypeCard typeCardRequest);
}

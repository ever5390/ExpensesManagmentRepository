package pe.com.erp.expensemanager.modules.partners.services.interfaz;

import pe.com.erp.expensemanager.modules.notifications.model.NotificationExpense;
import pe.com.erp.expensemanager.modules.partners.model.Partner;

import java.util.List;

public interface IPartnerService {
	List<Partner> findPartnersByOwnerId(Long idOwner);

}

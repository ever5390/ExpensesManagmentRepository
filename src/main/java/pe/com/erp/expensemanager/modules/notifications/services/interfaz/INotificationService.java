package pe.com.erp.expensemanager.modules.notifications.services.interfaz;

import java.util.List;

import pe.com.erp.expensemanager.modules.notifications.model.NotificationExpense;

public interface INotificationService {

	//List<NotificationExpense> findNotificationStatusByUserIdAndTypeUser(Long idUser);

	NotificationExpense updateNotificationExpense(NotificationExpense notificationExpenseRequest);

}

package pe.com.erp.expensemanager.modules.notifications.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pe.com.erp.expensemanager.modules.notifications.model.NotificationExpense;

@Repository
public interface NotificationRepository extends CrudRepository<NotificationExpense, Long>{
/*
	@Query("SELECT n FROM NotificationExpense n where (n.expenseShared.workspace.owner.id = :idUser OR n.payer.id = :idUser) "
			+ "and (n.statusNotification = 'PENDIENTE_PAGO' OR n.statusNotification = 'POR_CONFIRMAR' OR n.statusNotification = 'RECHAZADO' OR n.statusNotification = 'RECLAMADO' ) ORDER BY n.createAt DESC")
	List<NotificationExpense> findByUserEmisorAndStatus(Long idUser);
	*/
	@Query("SELECT n FROM NotificationExpense n where n.expenseShared.id = :idExpense")
	NotificationExpense findByExpenseId(Long idExpense);
	
}

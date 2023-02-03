package pe.com.erp.expensemanager.modules.notifications.model;

import java.io.Serializable;

public enum TypeStatusNotificationExpense implements Serializable{
	CANCELADO,
	PAGADO,
	PENDIENTE_PAGO,
	POR_CONFIRMAR,
	RECHAZADO,
	RECLAMADO
}

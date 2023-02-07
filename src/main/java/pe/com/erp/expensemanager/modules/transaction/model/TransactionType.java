package pe.com.erp.expensemanager.modules.expense.model;

import java.io.Serializable;

public enum TransactionType implements Serializable{
	EXPENSE,

	INCOME,
	REMINDER,

	PAYMENT
}

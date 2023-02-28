package pe.com.erp.expensemanager.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertiesExtern {
	@Value("${response.customized.message.transferences.selfaccount.dontpossibletransfer}")
    public String RESPONSE_CUSTOMIZED_TRANSFER_ERROR_SELF_ACCOUNT_DONT_POSIBLE_TRANSFER;
    @Value("${response.customized.message.expense.notfound.payments.assoctoexpense.todelete}")
    public String RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_NOTFOUND_PAYMENTS_ASSOC_TO_EXPENSE_TO_DELETE;
	@Value("${response.customized.message.expense.notfound.expenses.assoctopayment.todelete}")
	public String  RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_NOTFOUND_EXPENSE_ASSOC_TO_PAYMENT_TO_DELETE;
    @Value("${response.customized.message.transfer.availableaccounttoreversalisnotsufficientfordiscountamounttransference}")
	public String RESPONSE_CUSTOMIZED_TRANSFER_INFO_AVAILABLE_ACCOUNT_ITS_INSUFFICIENT_FOR_DISCOUNT_AMOUNT_TRANSFERENCE_DONT_POSSIBLE_REVERSAL;
	@Value("${response.customized.message.expenseassocbycreditcarddontexist}")
    public String RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_ASSOC_CREDIT_CARD_DONT_EXIST;
    @Value("${response.customized.message.expense.expenseassocdontexists}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_EXPENSE_TO_ASSOC_DONT_EXIST;
	@Value("${response.customized.message.expense.payregisterdontexist}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_PAYREGISTER_DONT_EXIST;
	@Value("${response.customized.message.expense.topay.dontexist}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_EXPENSE_TO_PAY_DONT_EXIST;
	@Value("${response.customized.message.expense.amounttopay.itsgreatherthan.amountexpense}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_AMOUNT_TO_PAY_ITS_GREATER_THAN_AMOUNT_EXPENSE;

	@Value("${response.customized.message.expense.transaction.isnotincome}")
	public String RESPONSE_CUSTOMIZED_EXPENSE_TRANSACTION_ISNOT_INCOME;
	@Value("${response.customized.message.account.accountname.isempty}")
    public String RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_ACCOUNTNAME_ISEMPTY;

	@Value("${response.customized.message.account.accounttype.isempty}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_ACCOUNTTYPE_ISEMPTY;

	@Value("${response.customized.message.account.financialentity.isempty}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_FINANCIALENTITY_ISEMPTY;

	@Value("${response.customized.message.account.typecard.isempty}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_TYPECARD_ISEMPTY;
	@Value("${response.customized.message.account.exists.expensebyaccountselected.impossible.deleteaccount}")
    public String RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_EXPENSES_EXIST_BY_ACCOUNT_SELECTED_DELETE_ITS_IMPOSSIBLE;

	@Value("${response.customized.message.account.exists.transferencesbyaccountselected.impossible.deleteaccount}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_TRANSFERENCE_EXIST_BY_ACCOUNT_SELECTED_DELETE_ITS_IMPOSSIBLE;

	@Value("${response.customized.message.account.exists.expensesfromsomeaccountsbudget.impossible.deletebudget}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_EXPENSES_EXIST_FOR_BUDGT_SELECTED_DELETE_ITS_IMPOSSIBLE;

	@Value("${response.customized.message.account.exists.transferencesfromsomeaccountsbudget.impossible.deletebudget}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_TRANSFERENCES_EXIST_FOR_BUDGT_SELECTED_DELETE_ITS_IMPOSSIBLE;
	@Value("${response.customized.message.account.status.initial.dontpossible.update}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_STATUS_INITIAL_DONT_POSIBLE_UPDATED;
	@Value("${response.customized.message.account.dontexist}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_DONT_EXIST;
	@Value("${response.customized.message.account.amount.lower.than.amountused}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_AMOUNT_LOWER_THAN_AMOUNTUSED;
	@Value("${response.customized.message.account.amount.better.than.amountallowed}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_AMOUNT_BETTER_THAN_AMOUNT_ALLOWED;
	@Value("${response.customized.message.accountparent.dontexist}")
    public String RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_PARENT_DONT_EXIST;
    @Value("${response.customized.accountspentexist.bycateg.mayorbalancechild}")
	public String RESPONSE_CUSTOMIZED_ACCOUNT_SPENT_EXIST_BYCATEG_MAYOR_BALANCECHILD;

	@Value("${response.generic.sucess.title}")
	public String RESPONSE_GENERIC_SUCCESS_TITLE;

	@Value("${response.generic.error.title}")
	public String RESPONSE_GENERIC_ERROR_TITLE;

	@Value("${response.generic.info.title}")
	public String RESPONSE_GENERIC_INFO_TITLE;

	@Value("${response.generic.success.status}")
	public String RESPONSE_GENERIC_SUCCESS_STATUS;

	@Value("${response.generic.error.status}")
	public String RESPONSE_GENERIC_ERROR_STATUS;

	@Value("${response.generic.info.status}")
	public String RESPONSE_GENERIC_INFO_STATUS;	 
	
	@Value("${response.generic.save.success.message}")
	public String RESPONSE_GENERIC_SAVE_SUCCESS_MESSAGE;

	@Value("${response.generic.save.error.internalserver.message}")
	public String RESPONSE_GENERIC_SAVE_ERROR_INTERNALSERVER_MESSAGE;

	@Value("${response.generic.update.success.message}")
	public String RESPONSE_GENERIC_UPDATE_SUCCESS_MESSAGE;

	@Value("${response.generic.update.error.internalserver.message}")
	public String RESPONSE_GENERIC_UPDATE_ERROR_INTERNALSERVER_MESSAGE;

	@Value("${response.generic.delete.success.message}")
	public String RESPONSE_GENERIC_DELETE_SUCCESS_MESSAGE;

	@Value("${response.generic.delete.error.internalserver.message}")
	public String RESPONSE_GENERIC_DELETE_ERROR_INTERNALSERVER_MESSAGE;

	@Value("${response.generic.error.notfound.message}")
	public String RESPONSE_GENERIC_ERROR_NOTFOUND_MESSAGE;

	@Value("${response.generic.success.found.message}")
	public String RESPONSE_GENERIC_SUCCESS_FOUND_MESSAGE;
	
	//jwt - security
	@Value("${jwt.secret}")
	public String JWT_SECRET;

	@Value("${jwt.expiration}")
	public String JWT_EXPIRATION;
		
	@Value("${response.customized.category.duplicate}")
	public String RESPONSE_CUSTOMIZED_CATEGORY_DUPLICATE;
	
	@Value("${response.customized.paymentmethod.duplicate}")
	public String RESPONSE_CUSTOMIZED_PAYMENTMETHOD_DUPLICATE;
	
	@Value("${response.customized.expense.update.delete.title}")
	public String RESPONSE_CUSTOMIZED_EXPENSE_UPDATE_DELETE_TITLE;
	
	@Value("${response.customized.expense.without.category}")
	public String RESPONSE_CUSTOMIZED_EXPENSE_WITHOUT_CATEGPRY;
	
	@Value("${response.customized.expense.without.according}")
	public String RESPONSE_CUSTOMIZED_EXPENSE_WITHOUT_ACCORDING;
	
	@Value("${response.customized.expense.saldoaccount.zero}")
	public String RESPONSE_CUSTOMIZED_EXPENSE_SALDO_ACCOUNT_ZERO;
	
	@Value("${response.generic.customized.expense.not.itself.ownerpay}")
	public String RESPONSE_CUSTOMIZED_EXPENSE_NO_ITSELF_OWNER_PAY;
	
	@Value("${response.customized.expense.notfound}")
	public String RESPONSE_CUSTOMIZED_EXPENSE_NOTFOUND;	
	
	@Value("${response.customized.expense.usernotifiedpay}")
	public String RESPONSE_CUSTOMIZED_EXPENSE_USERNOTIFIEDPAY;	
	
	@Value("${response.customized.expense.without.paymentmethod}")
	public String RESPONSE_CUSTOMIZED_EXPENSE_WITHOUT_PAYMENTMETHOD;
	
	@Value("${response.customized.expense.without.payer}")
	public String RESPONSE_CUSTOMIZED_EXPENSE_WITHOUT_PAYER;
		
	@Value("${response.customized.expense.without.amount}")
	public String RESPONSE_CUSTOMIZED_EXPENSE_WITHOUT_AMOUNT;
	
	@Value("${response.customized.expense.message.update}")
	public String RESPONSE_CUSTOMIZED_EXPENSE_MESSAGE_ERROR_UPDATE;
	
	//### VALIDACIONES PARA EXPENSE
	@Value("${response.customized.amount.mayor.saldodisponible}") 
	public String  RESPONSE_CUSTOMIZED_AMOUNT_MAYOR_SALDODISPONIBLE;
	
	
	// VALIDATION FOR ACCOUNT
	@Value("${response.customized.account.totalgastado.mayor.balance}")
	public String RESPONSE_CUSTOMIZED_ACCOUNT_TOTALGASTADO_MAYOR_BALANCE;

	@Value("${response.customized.account.balancechild.mayor.balancedisponibleparent}")
	public String RESPONSE_CUSTOMIZED_ACCOUNT_BALANCECHILD_MAYOR_BALANCEDISPONIBLEPARENT;
	
	@Value("${response.customized.account.success.confirmaccount}")
	public String RESPONSE_CUSTOMIZED_ACCOUNT_SUCCESS_CONFIRMACCOUNT;
	
	@Value("${response.customized.account.error.confirmaccount}")
	public String RESPONSE_CUSTOMIZED_ACCOUNT_ERROR_CONFIRMACCOUNT;
	
	//Category
	@Value("${response.customized.category.error.delete}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_CATEGORY_NOT_DELETED;
	
	@Value("${response.customized.category.account.error.delete}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_CATEGORY_ACCOUNT_NOT_DELETED;
	
	@Value("${response.customized.category.error.name.repeat}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_CATEGORY_NAME_REPEAT;
	
	//PaymentMethod
	@Value("${response.customized.paymentmethod.error.delete}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_PAYMENTMETHOD_NOT_DELETED;
	
	@Value("${response.customized.paymentmethod.error.name.repeat}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_PAYMENTMETHOD_NAME_REPEAT;
	
	//Update account
	@Value("${response.customized.account.error.update.generic}")
	public String RESPONSE_CUSTOMIZED_ACCOUNT_ERROR_UPDATE_GENERIC;
	
	@Value("${response.customized.account.error.update.diferenciamontonopermititda}")
	public String RESPONSE_CUSTOMIZED_ACCOUNT_ERROR_MONTO_NO_PERMITIDO;
	
	@Value("${response.customized.account.error.name.repeat}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_NAME_REPEAT;

	@Value("(\"${response.customized.account.error.newamountexpenseisgreatherthanamountpayedtoexpenseupdate}\")")
	public String RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_NEW_AMOUNT_IS_GRATHER_THAN_AMOUNT_PAYED_TO_UPDATE;
	@Value("${response.customized.account.error.save.amountexpenseisgreatherthanamountavailableaccount}")
	public String RESPONSE_AMOUNT_TO_EXPENSE_IS_GRATHER_THAN_TO_AVAILABLE_AMOUNT_ACCOUNT;
	@Value("${response.customized.account.error.save.amountpaymentisgreatherthanamountpendingexpense}")
	public String RESPONSE_AMOUNT_TO_PAYMENT_IS_GRATHER_THAN_TO_AMOUNT_PENDING_EXPENSE;
	@Value("${response.customized.account.success.delete}")
	public String RESPONSE_CUSTOMIZED_ACCOUNT_SUCCESS_DELETE;
	
	@Value("${response.customized.account.error.delete.inesperado}")
	public String RESPONSE_CUSTOMIZED_ACCOUNT_ERROR_DELETE_INESPERADO;
	
	@Value("${response.customized.account.error.dontdelete.because.accounthave.expenseassoc}")
	public String RESPONSE_CUSTOMIZED_DONT_HAVE_DELETE_BECAUSE_ACCOUNT_HAVE_EXPENSE_ASSOC;
	
	@Value("${response.customized.account.error.delete.status.noinitial}")
	public String RESPONSE_CUSTOMIZED_ACCOUNT_ERROR_DELETE_STATUS_NOINITIAL;
	
	//Transfer Error
	@Value("${response.customized.transfer.info.amounttotransfer.betterthan.availableamount.originaccount}")
	public String RESPONSE_CUSTOMIZED_TRANSFER_INFO_AMOUNTTOTRANSFER_BETTERTHAN_AVAILABLEAMOUNT_ORIGINACCOUNT;
	
	@Value("${response.customized.account.success.transfer}")
	public String RESPONSE_CUSTOMIZED_ACCOUNT_SUCCESS_TRANSFER;
	
	@Value("${response.customized.transference.error.someaccount.donstfound}")
	public String RESPONSE_CUSTOMIZED_TRANSFER_INFO_SOMEACCOUNT_DONT_FOUND;
	
	//Owner
	@Value("${response.customized.owner.error.searchbyusername}")
	public String RESPONSE_CUSTOMIZED_OWNER_ERROR_SEARCHBYUSERNAME;
	
	@Value("${response.customized.owner.success.searchbyusername}")
	public String RESPONSE_CUSTOMIZED_OWNER_SUCCESS_SEARCHBYUSERNAME;


	@Value("${response.customized.message.transference.dontexist}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_TRANSFERENCE_DONT_EXIST;

	@Value("${response.customized.message.transference.accountdestiny.dontexist}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_ORIGIN_DONT_EXIST;

	@Value("${response.customized.message.transference.accountorigin.dontexist}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_DESTINY_DONT_EXIST;

	
	//Periodo
	@Value("${response.customized.period.close.success}")
	public String RESPONSE_CUSTOMIZED_PERIOD_CLOSE_SUCCESS;
	
	@Value("${response.customized.period.close.error}")
	public String RESPONSE_CUSTOMIZED_PERIOD_CLOSE_ERROR;
	
	@Value("${response.customized.period.close.error.pendingpayexpense}")
	public String RESPONSE_CUSTOMIZED_PERIOD_CLOSE_ERROR_PENDING_PAY_EXPENSE;
	
	@Value("${response.customized.period.not_found}")
	public String RESPONSE_CUSTOMIZED_MESSAGE_PERIOD_NOT_FOUND;
	
}

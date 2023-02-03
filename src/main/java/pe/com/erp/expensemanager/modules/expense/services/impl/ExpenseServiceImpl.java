package pe.com.erp.expensemanager.modules.expense.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import pe.com.erp.expensemanager.exception.CustomException;
import pe.com.erp.expensemanager.modules.account.model.Account;
import pe.com.erp.expensemanager.modules.expense.model.ExpenseType;
import pe.com.erp.expensemanager.modules.account.model.TypeStatusAccountOPC;
import pe.com.erp.expensemanager.modules.account.repository.AccountRepository;
import pe.com.erp.expensemanager.modules.categories.model.Category;
import pe.com.erp.expensemanager.modules.categories.repository.CategoryRepository;
import pe.com.erp.expensemanager.modules.expense.model.Expense;
import pe.com.erp.expensemanager.modules.expense.model.Reposition;
import pe.com.erp.expensemanager.modules.expense.model.Tag;
import pe.com.erp.expensemanager.modules.expense.repository.ExpenseRepository;
import pe.com.erp.expensemanager.modules.expense.repository.RepositionRepo;
import pe.com.erp.expensemanager.modules.expense.repository.TagRepository;
import pe.com.erp.expensemanager.modules.expense.services.interfaz.IExpenseService;
import pe.com.erp.expensemanager.modules.notifications.model.NotificationExpense;
import pe.com.erp.expensemanager.modules.notifications.model.TypeStatusNotificationExpense;
import pe.com.erp.expensemanager.modules.notifications.repository.NotificationRepository;
import pe.com.erp.expensemanager.modules.owner.model.Owner;
import pe.com.erp.expensemanager.modules.owner.repository.OwnerRepository;
import pe.com.erp.expensemanager.modules.partners.model.Partner;
import pe.com.erp.expensemanager.modules.partners.model.StatusInvitationsPartner;
import pe.com.erp.expensemanager.modules.partners.repository.PartnerRepository;
import pe.com.erp.expensemanager.modules.period.model.Period;
import pe.com.erp.expensemanager.modules.period.repository.PeriodRepository;
import pe.com.erp.expensemanager.properties.PropertiesExtern;
import pe.com.erp.expensemanager.shared.model.Response;
import pe.com.erp.expensemanager.shared.model.Vouchers;
import pe.com.erp.expensemanager.shared.repository.VoucherRepository;
import pe.com.erp.expensemanager.utils.Utils;

@Service
public class ExpenseServiceImpl implements IExpenseService {

	public static final Logger LOG = LoggerFactory.getLogger(ExpenseServiceImpl.class);

	@Autowired
	PropertiesExtern properties;

	@Autowired
	PeriodRepository periodRepo;

	@Autowired
	AccountRepository accountRepo;

	@Autowired
	ExpenseRepository expenseRepo;

	@Autowired
	CategoryRepository categoryRepo;

	@Autowired
	OwnerRepository ownerRepo;

	@Autowired
	TagRepository tagRepo;

	@Autowired
	VoucherRepository voucherRepo;

	@Autowired
	NotificationRepository notifRepo;

	@Autowired
	PartnerRepository partnerRepository;
	@Autowired
	private RepositionRepo repositionRepo;

	@Override
	@Transactional(rollbackFor = {CustomException.class, ValidationException.class})
	public Response saveExpense(Expense expenseRequest, String messageLog) throws CustomException {

		Response response = new Response();
		Account accountAffected = new Account();
		Expense expenseSaved = new Expense();
		boolean goToDiscountAccount = true;
		boolean goToNotifyCounterpart = false;
		Owner userRegister = new Owner();
		LOG.info(messageLog + expenseRequest.getAccount().getBalanceAvailable());
		LOG.info(messageLog + expenseRequest.getExpenseType());
		userRegister = expenseRequest.getPeriod().getWorkspace().getOwner();
		accountAffected = accountRepo.findById(expenseRequest.getAccount().getId()).orElse(null);

		if((expenseRequest.getAmount() > accountAffected.getBalanceAvailable()) && !expenseRequest.getExpenseType().equals(ExpenseType.REMINDER)) {
			LOG.info(messageLog + " AMOUNT IS GREATER THAN AMOUNT AVAILABLE ACCOUNT ");
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_AMOUNT_MAYOR_SALDODISPONIBLE.replace("{0}",
					expenseRequest.getAccount().getBalanceAvailable().toString()));
		}
		LOG.info(messageLog + expenseRequest.getAmount());
		LOG.info(expenseRequest.getCategory().toString());

		if(expenseRequest.getCategory().getName().isEmpty()) {
			new CustomException(properties.RESPONSE_CUSTOMIZED_EXPENSE_WITHOUT_CATEGPRY);
		}

		if(expenseRequest.getCategory().getId() == 0) {
			expenseRequest.setCategory(categoryRepo.save(expenseRequest.getCategory()));
		}

		LOG.info(messageLog + expenseRequest.getAccount().getBalanceAvailable());
		LOG.info(messageLog + accountAffected.getBalanceAvailable());
		if(accountAffected == null) {
			LOG.info(messageLog + " ACCOUNT IS NULL ");
			throw new CustomException("Account IS NULL");
		}
		expenseRequest.setPendingPay(false);

		if(expenseRequest.getExpenseType().equals(ExpenseType.REMINDER)) {
			expenseRequest.setPendingPay(true);
			goToDiscountAccount = false;
		}
		/*
		if(!expenseRequest.getPartner().getName().isBlank() && !expenseRequest.getPartner().getEmail().equals(userRegister.getEmail())) {
			expenseRequest.setPendingPay(true);
			if(expenseRequest.getPartner().getId() == 0) {
				//Save new partner, don't notified
				Partner partnerSaved = saveNewPartner(expenseRequest);
				expenseRequest.setPartner(partnerSaved);
			} else if(expenseRequest.getPartner().getStatusRequest().equals(StatusInvitationsPartner.ACCEPTED)) {
				//Send notification
				goToNotifyCounterpart = true;
			}
		}*/
		expenseRequest.setTag(validateTagsAndSaveIfNotExists(expenseRequest, messageLog));
		expenseRequest.setVouchers(validateVouchersAndSaveIfNotExists(expenseRequest, messageLog));
		expenseRequest.setReposition(saveOrUpdateRepositions(expenseRequest, messageLog));

		if(goToDiscountAccount) {
			LOG.info(messageLog + " UPDATING AVAILABLE AMOUNT ACCOUNT ");
			LOG.info(messageLog + accountAffected.getBalanceAvailable());
			LOG.info(messageLog + expenseRequest.getAmount());
			accountAffected.setBalanceAvailable(accountAffected.getBalanceAvailable() - expenseRequest.getAmount());
			accountRepo.save(accountAffected);
		}

		LOG.info(messageLog + " STEP 1 ");
		expenseRequest.setAmount((-1)*expenseRequest.getAmount());
		expenseSaved = expenseRepo.save(expenseRequest);
		LOG.info(messageLog + " STEP 2 ");
		if(goToNotifyCounterpart) {
			LOG.info(messageLog + " NOTIFYING COUNTERPART IF EXIST ");
			saveNotificationRegister(expenseSaved);
		}

		LOG.info(messageLog + " ::: ACCOUNT SAVED SUCCESSFULLY ::: ");

		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_SAVE_SUCCESS_MESSAGE);
		response.setObject(expenseSaved);

		return response;
	}

	private void saveNotificationRegister(Expense expenseSaved) {
		NotificationExpense notificationExpense = new NotificationExpense();
		notificationExpense.setExpenseShared(expenseSaved);
		//notificationExpense.setPayer();
		notificationExpense.setStatusNotification(TypeStatusNotificationExpense.PENDIENTE_PAGO);
		notificationExpense.setVouchers(expenseSaved.getVouchers());
		notificationExpense.setCreateAt(new Date());
		notificationExpense.setComentarios("Ever realizaó un gasto en name_category que ascendió a amount_expense, " +
				" y le corresponde pagar el monto completo.");
		notifRepo.save(notificationExpense);
	}

	private Partner saveNewPartner(Expense expenseRequest) {
		LOG.info(" SAVING PARTNER");
		Partner partnerNotRegisterApp = new Partner();
		partnerNotRegisterApp = expenseRequest.getPartner();
		partnerNotRegisterApp.setOwnerId(expenseRequest.getPeriod().getWorkspace().getOwner().getId());
		partnerNotRegisterApp.setStatusRequest(StatusInvitationsPartner.NOSENDED);
		LOG.info(" SAVING PARTNER 2");
		return partnerRepository.save(partnerNotRegisterApp);
	}

	private List<Tag> validateTagsAndSaveIfNotExists(Expense expenseRequest, String messageLog) {

		List<Tag> tagsSave = new ArrayList<>();
		if (expenseRequest.getTag().size() > 0) {
			for (Tag tag : expenseRequest.getTag()) {
				LOG.info(messageLog + " SAVING TAGS TO DB");
				tagsSave.add(tagRepo.save(tag));
			}
		}

		return tagsSave;
	}

	private List<Vouchers> validateVouchersAndSaveIfNotExists(Expense expenseRequest, String messageLog) {
		List<Vouchers> voucherToSave = new ArrayList<>();
		if(expenseRequest.getVouchers().size() > 0) {
			for ( Vouchers voucher: expenseRequest.getVouchers()) {
				//if(voucher.getId() == 0) {
					LOG.info(messageLog + " SAVING VOUCHERS TO DB");
					voucherToSave.add(voucherRepo.save(voucher));
				//}
			}
		}
		return voucherToSave;
	}

	private List<Reposition> saveOrUpdateRepositions(Expense expenseRequest, String messageLog) {
		List<Reposition> repositions = new ArrayList<>();
		if(expenseRequest.getReposition().size() > 0) {
			for ( Reposition reposition: expenseRequest.getReposition()) {
				LOG.info(messageLog + " SAVING PARTNER TO DB");
				reposition.setPartnerToPay(partnerRepository.save(reposition.getPartnerToPay()));
				LOG.info(messageLog + " SAVING NOTIFICATIONS WHEN PARTNER ITS SENDED STATUS TO DB");
				if(reposition.getPartnerToPay().getStatusRequest().equals(StatusInvitationsPartner.SENDED)) {
					//notifRepo.save();
				}
				LOG.info(messageLog + " SAVING REPOSITION TO DB");
				repositionRepo.save(reposition);
			}
		}
		return repositions;
	}

	@Override
	@Transactional(rollbackFor = {CustomException.class, ValidationException.class})
	public Response savePay(Expense payRequest, String messageLog) throws CustomException {

		Response response = new Response();
		Account accountAffected = new Account();
		Expense payResponse = new Expense();

		payRequest.setPendingPay(false);
		accountAffected = payRequest.getAccount();

		Expense expenseToPay = expenseRepo.findById(payRequest.getIdExpenseToPay()).orElse(null);

		if(expenseToPay == null) {
			LOG.info(messageLog + " ::: EXPENSE TO PAY DON'T EXIST ::: ");
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_EXPENSE_TO_PAY_DONT_EXIST);
		}

		if(payRequest.getAmount() > expenseToPay.getAmountToRecover()) {
			LOG.info(messageLog + " ::: AMOUNT TO PAY IT´S GREATER THAN AMOUNT EXPENSE ::: ");
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_AMOUNT_TO_PAY_ITS_GREATER_THAN_AMOUNT_EXPENSE);
		}

		if(payRequest.getAmount() ==  Math.abs(expenseToPay.getAmountToRecover())) {
			LOG.info(messageLog + " ::: AMOUNT TO PAY ITS LOWER THAN AMOUNT TO RECOVER ::: ");
			expenseToPay.setPendingPay(false);
		}

		expenseToPay.setReposition(saveOrUpdateRepositions(payRequest, messageLog));

		if(expenseToPay.getAccount().getTypeCard().getName().toUpperCase().equals("CREDIT")) {
			//subtraction amount to account destiny pay && subtraction amount to expense pay
			LOG.info(messageLog + " ::: IT'S CREDIT CARD ::: ");
			if(expenseToPay.getAmount() > accountAffected.getBalanceAvailable()) {
				LOG.info(messageLog + " AMOUNT TO PAY IS GREATER THAN AMOUNT CREDIT CARD AVAILABLE ACCOUNT");
				throw new CustomException(properties.RESPONSE_CUSTOMIZED_AMOUNT_MAYOR_SALDODISPONIBLE.replace("{0}",
						expenseToPay.getAccount().getBalanceAvailable().toString()));
			}
			LOG.info(messageLog + " ::: SUBTRACTION AMOUNT TO ACCOUNT PAYED && SUBTRACTION TO RECOVER EXPENSE PAY ::: ");
			accountAffected.setBalanceAvailable(accountAffected.getBalanceAvailable() - payRequest.getAmount());
			expenseToPay.setAmount((-1)*(Math.abs(expenseToPay.getAmount()) - payRequest.getAmount()));

			payRequest.setAmount((-1)*payRequest.getAmount());

		} else {
			//Sum amount to account destiny pay : available & origin
			LOG.info(messageLog + " ::: SUM AMOUNT TO ACCOUNT PAYED ::: ");
			accountAffected.setBalance(accountAffected.getBalance() + payRequest.getAmount());
			accountAffected.setBalanceAvailable(accountAffected.getBalanceAvailable() + payRequest.getAmount());
		}

		LOG.info(messageLog + " ::: UPDATE EXPENSE RECOVER AMOUNT TO PAY ::: ");
		expenseToPay.setAmountToRecover(expenseToPay.getAmountToRecover() - payRequest.getAmount());

		LOG.info(messageLog + " ::: UPDATE EXPENSE TO PAY ::: ");
		expenseRepo.save(expenseToPay);
		LOG.info(messageLog + " ::: UPDATE AMOUNT OF ACCOUNT TO PAY ::: ");
		accountRepo.save(accountAffected);
		LOG.info(messageLog + " ::: SAVE TAG & VOUCHERS IF EXISTS ::: ");
		validateTagsAndSaveIfNotExists(payRequest, messageLog);
		LOG.info(messageLog + " ::: SAVE REGISTER PAY ::: ");
		payResponse = expenseRepo.save(payRequest);

		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_SAVE_SUCCESS_MESSAGE);
		response.setObject(payResponse);

		return response;
	}

	private Response validationInputParams(Expense expenseRequest) {
		Response response = new Response();

		response.setTitle(properties.RESPONSE_GENERIC_INFO_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_INFO_STATUS);
		
		if(expenseRequest.getAmount() == null) {
			response.setMessage(properties.RESPONSE_CUSTOMIZED_EXPENSE_WITHOUT_AMOUNT);
			return response;
		}

		LOG.info("5");
		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		return response;
	}


	public Account updateAccountIfExists(Expense expenseUpdateReq) {
		LOG.info("Update Account");
		//If not exist account in request --> setea Parent account
		if (expenseUpdateReq.getAccount() == null) {
			Account accountMainExist = accountRepo
					.findAccountByTypeAccountAndStatusAccountAndPeriodId(1L,
							TypeStatusAccountOPC.PROCESS,
							expenseUpdateReq.getPeriod().getId());

			if (accountMainExist == null) return null;
			expenseUpdateReq.setAccount(accountMainExist);
		}

		Account accountUpdate = expenseUpdateReq.getAccount();
		double amountBalanceAvailableUpdate = expenseUpdateReq.getAccount().getBalanceAvailable();
		double amountSpentReq = expenseUpdateReq.getAmount();
		accountUpdate.setBalanceAvailable(Utils.roundTwoDecimals(amountBalanceAvailableUpdate + amountSpentReq));
		accountUpdate = accountRepo.save(accountUpdate);

		LOG.info("Update saldo nuevo: " + accountUpdate.getBalanceAvailable());
		return accountUpdate;
	}

	@Override
	@Transactional(rollbackFor = {CustomException.class, ValidationException.class})
	public Response updateExpense(Expense transactionRegister, Long idExpenseUpdateReq, String messageLog) {

		Response response = new Response();
		Account accountAffected = new Account();

		Expense expenseAssocToTransaction = expenseRepo.findById(transactionRegister.getIdExpenseToPay()).orElse(null);

		if(expenseAssocToTransaction == null) {
			LOG.info(messageLog + " ::: EXPENSE TO ASSOC DON'T EXIST ::: ");
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_EXPENSE_TO_ASSOC_DONT_EXIST);
		}

		if(transactionRegister.getExpenseType().equals(ExpenseType.REMINDER)) {
			this.saveExpense(transactionRegister, messageLog);
		}

		if(expenseAssocToTransaction.getExpenseType().equals(ExpenseType.EXPENSE)) {
			//Validar que el gasto a editar no tenga pagos asociados a este.


			// -------------------------------------

			accountAffected.setBalanceAvailable(accountAffected.getBalanceAvailable() + expenseAssocToTransaction.getAmount());

			//IF EXISTS NOTIFICATION, DELETE REGISTER ASSOC TO EXPENSE
			NotificationExpense notificationAssocToExpense = notifRepo.findByExpenseId(expenseAssocToTransaction.getId());
			if(notificationAssocToExpense != null) {
				notifRepo.deleteById(notificationAssocToExpense.getId());
			}

			accountRepo.save(accountAffected);
			response = this.saveExpense(transactionRegister, messageLog);
		}

		if(expenseAssocToTransaction.getExpenseType().equals(ExpenseType.PAYMENT)) {
			Expense payRegisterExist = expenseRepo.findById(idExpenseUpdateReq).orElse(null);
			if(payRegisterExist == null) {
				LOG.info(messageLog + " ::: REGISTER TO UPDATE DON'T EXIST ::: ");
				throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_PAYREGISTER_DONT_EXIST);
			}

			expenseAssocToTransaction.setAmountToRecover(expenseAssocToTransaction.getAmountToRecover() + payRegisterExist.getAmount());
			expenseAssocToTransaction.setPendingPay(true);

			if(expenseAssocToTransaction.getAccount().getTypeCard().getName().toUpperCase().equals("CREDIT")) {
				accountAffected.setBalanceAvailable(accountAffected.getBalanceAvailable() + payRegisterExist.getAmount());
				expenseAssocToTransaction.setAmount((-1)*(Math.abs(expenseAssocToTransaction.getAmount()) + payRegisterExist.getAmount()));
			} else {
				accountAffected.setBalanceAvailable(accountAffected.getBalanceAvailable() - payRegisterExist.getAmount());
			}

			accountRepo.save(accountAffected);
			expenseRepo.save(expenseAssocToTransaction);

			response = this.savePay(transactionRegister, messageLog);
		}

		if(!response.getStatus().equals(properties.RESPONSE_GENERIC_SUCCESS_STATUS)) {
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_EXPENSE_MESSAGE_ERROR_UPDATE);
		}

		response.setMessage(properties.RESPONSE_GENERIC_UPDATE_SUCCESS_MESSAGE);
		return response;
	}

	@Override
	@Transactional
	public Response deleteExpenseById(Long idExpense, String messageLog) {

		Response response = new Response();
		Account accountAffected = new Account();

		Expense expenseAssocToTransaction = expenseRepo.findById(idExpense).orElse(null);

		if(expenseAssocToTransaction == null) {
			LOG.info(messageLog + " ::: EXPENSE TO ASSOC DON'T EXIST ::: ");
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_EXPENSE_TO_ASSOC_DONT_EXIST);
		}

		if(expenseAssocToTransaction.getExpenseType().equals(ExpenseType.REMINDER)) {
			expenseRepo.deleteById(expenseAssocToTransaction.getId());
		}

		if(expenseAssocToTransaction.getExpenseType().equals(ExpenseType.EXPENSE)) {
			//Validar que el gasto a editar no tenga pagos asociados a este.


			// -------------------------------------

			accountAffected.setBalanceAvailable(accountAffected.getBalanceAvailable() + expenseAssocToTransaction.getAmount());

			//IF EXISTS NOTIFICATION, DELETE REGISTER ASSOC TO EXPENSE
			NotificationExpense notificationAssocToExpense = notifRepo.findByExpenseId(expenseAssocToTransaction.getId());
			if(notificationAssocToExpense != null) {
				notifRepo.deleteById(notificationAssocToExpense.getId());
			}

			accountRepo.save(accountAffected);
			expenseRepo.deleteById(expenseAssocToTransaction.getId());
		}

		if(expenseAssocToTransaction.getExpenseType().equals(ExpenseType.PAYMENT)) {
			Expense expensePayed = expenseRepo.findById(expenseAssocToTransaction.getIdExpenseToPay()).orElse(null);
			if(expensePayed == null) {
				LOG.info(messageLog + " ::: REGISTER TO UPDATE DON'T EXIST ::: ");
				throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_PAYREGISTER_DONT_EXIST);
			}

			expensePayed.setAmountToRecover(expensePayed.getAmountToRecover() + expenseAssocToTransaction.getAmount());
			expensePayed.setPendingPay(true);

			if(expensePayed.getAccount().getTypeCard().getName().toUpperCase().equals("CREDIT")) {
				accountAffected.setBalanceAvailable(accountAffected.getBalanceAvailable() + expenseAssocToTransaction.getAmount());
				expensePayed.setAmount((-1)*(Math.abs(expensePayed.getAmount()) + expenseAssocToTransaction.getAmount()));
			} else {
				accountAffected.setBalanceAvailable(accountAffected.getBalanceAvailable() - expenseAssocToTransaction.getAmount());
			}

			accountRepo.save(accountAffected);
			expenseRepo.save(expensePayed);
			expenseRepo.deleteById(expenseAssocToTransaction.getId());
		}

		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_DELETE_SUCCESS_MESSAGE);
		return response;
	}

	/*
	@Override
	public List<Expense> findExpensesByIdPeriodAndIStatusPay(Long idPeriod, boolean statusPay) {
		//Validate if not exists expenses pending pay
		List<Expense> listExpenses = expenseRepo.findExpensesBypIdPeriodAndIsPendingPay(idPeriod, statusPay);
		return listExpenses;
	}

	@Override
	public List<Expense> findExpensesByIdWorkspaceAndIdPeriod(Long idWorkspace, Long idPeriod) {
		List<Expense> listExpenses = expenseRepo.findExpensesByIdWorkspaceAndIdperiod(idWorkspace, idPeriod);
		return listExpenses;
	}
*/
	@Override
	public List<Expense> findExpensessByWorskpaceIdAndDateRange(Long idWorkspace, Date dateBegin, Date dateEnd) {
		List<Expense> listExpenses = expenseRepo.findExpensessByWorskpaceIdAndDateRange(idWorkspace, dateBegin,
				dateEnd);
		return listExpenses;
	}



}

package pe.com.erp.expensemanager.modules.transaction.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import pe.com.erp.expensemanager.exception.CustomException;
import pe.com.erp.expensemanager.modules.account.model.Account;
import pe.com.erp.expensemanager.modules.account.model.StatusAccountTransfer;
import pe.com.erp.expensemanager.modules.account.model.Transference;
import pe.com.erp.expensemanager.modules.account.model.TypeTransference;
import pe.com.erp.expensemanager.modules.account.repository.AccountRepository;
import pe.com.erp.expensemanager.modules.account.repository.TransferenceRepository;
import pe.com.erp.expensemanager.modules.partners.model.StatusInvitationsPartner;
import pe.com.erp.expensemanager.modules.partners.repository.PartnerRepository;
import pe.com.erp.expensemanager.modules.transaction.model.Reposition;
import pe.com.erp.expensemanager.modules.transaction.model.Tag;
import pe.com.erp.expensemanager.modules.transaction.model.Transaction;
import pe.com.erp.expensemanager.modules.transaction.model.TransactionType;
import pe.com.erp.expensemanager.modules.transaction.repository.RepositionRepo;
import pe.com.erp.expensemanager.modules.transaction.repository.TagRepository;
import pe.com.erp.expensemanager.modules.transaction.repository.TransactionRepository;
import pe.com.erp.expensemanager.modules.transaction.services.interfaz.ITransactionService;
import pe.com.erp.expensemanager.properties.PropertiesExtern;
import pe.com.erp.expensemanager.shared.model.Response;
import pe.com.erp.expensemanager.shared.model.Vouchers;
import pe.com.erp.expensemanager.shared.repository.VoucherRepository;
import pe.com.erp.expensemanager.utils.Utils;

@Service
public class TransactionServiceImpl implements ITransactionService {

	public static final Logger LOG = LoggerFactory.getLogger(TransactionServiceImpl.class);

	@Autowired
	PropertiesExtern properties;

	@Autowired
	TransactionRepository transactionRepository;
	@Autowired
	TagRepository tagRepo;

	@Autowired
	VoucherRepository voucherRepo;

	@Autowired
	RepositionRepo repositionRepo;

	@Autowired
	PartnerRepository partnerRepository;

	@Autowired
	TransferenceRepository transferenceRepository;
	@Autowired
	private AccountRepository accountRepository;

	@Override
	@Transactional(rollbackFor = {CustomException.class, ValidationException.class})
	public Response saveTransaction(Transaction transactionRequest, String messageLog) throws CustomException {

		Response response = new Response();
		boolean modifyAccount = true;
		Transaction transactionToSave = new Transaction();

		LOG.info(messageLog + "La transacción recibida es de tipo: " + TransactionType.EXPENSE);
		if(transactionRequest.getTransactionType().equals(TransactionType.EXPENSE)) {
			if(transactionRequest.getAmount() > transactionRequest.getAccount().getBalanceAvailable()){
				throw new CustomException(properties.RESPONSE_AMOUNT_TO_EXPENSE_IS_GRATHER_THAN_TO_AVAILABLE_AMOUNT_ACCOUNT);
			}

			if(transactionRequest.getAmountToRecover() > 0) {
				LOG.info(messageLog + "La transacción se maracará como PENDIENTE DE PAGO, ya que su monto a devolver es nayor que CERO");
				transactionRequest.setPendingPay(true);
				transactionRequest.setReposition(saveOrUpdateRepositions(transactionRequest, messageLog));
			}

			transactionRequest.setAmountToRecover(transactionRequest.getAmount());
			transactionRequest.setAmountPayed(0.0);
		}

		if(transactionRequest.getTransactionType().equals(TransactionType.REMINDER)) {
			LOG.info(messageLog + "La transacción se marcará como PENDIENTE DE PAGO, por ser recoradotorio");
			transactionRequest.setPendingPay(true);
			modifyAccount = false;
		}

		if(transactionRequest.getTransactionType().equals(TransactionType.PAYMENT)) {

			Transaction expenseAssocToPay = transactionRepository.findById(transactionRequest.getIdExpenseToPay()).orElse(null);

			if(expenseAssocToPay == null) {
				throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_EXPENSE_TO_PAY_DONT_EXIST);
			}

			if(transactionRequest.getAmount() > (expenseAssocToPay.getAmountToRecover() - expenseAssocToPay.getAmountPayed())){
				throw new CustomException(properties.RESPONSE_AMOUNT_TO_PAYMENT_IS_GRATHER_THAN_TO_AMOUNT_PENDING_EXPENSE);
			}

			expenseAssocToPay.setAmountPayed(expenseAssocToPay.getAmountPayed() + transactionRequest.getAmount());

			if(expenseAssocToPay.getAmountToRecover() == expenseAssocToPay.getAmountPayed()) {
				expenseAssocToPay.setPendingPay(false);
			}

			transactionRepository.save(expenseAssocToPay);
			LOG.info(messageLog + "El gasto asociado al pago ha sido actualizado correctamente");
		}

		transactionRequest.setTag(validateTagsAndSaveIfNotExists(transactionRequest, messageLog));
		transactionRequest.setVouchers(validateVouchersAndSaveIfNotExists(transactionRequest, messageLog));

		transactionToSave = transactionRepository.save(transactionRequest);

		LOG.info(messageLog + "Modificando los montos de las cuentas.");
		if(modifyAccount)
			modifyAmountAccountFromTransaction(transactionRequest, "SAVE");

		LOG.info(messageLog + properties.RESPONSE_GENERIC_SAVE_SUCCESS_MESSAGE);

		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_SAVE_SUCCESS_MESSAGE);
		response.setObject(transactionToSave);

		return response;
	}

	@Transactional(rollbackFor = {CustomException.class, ValidationException.class})
	private void modifyAmountAccountFromTransaction(Transaction transactionRequest, String action) {
		Account accountAssoc = new Account();
		accountAssoc = transactionRequest.getAccount();

		if(action.equals("SAVE")) {
			if(transactionRequest.getTransactionType().equals(TransactionType.EXPENSE)) {
				accountAssoc.setBalanceAvailable(accountAssoc.getBalanceAvailable() - transactionRequest.getAmount());
			}

			if(transactionRequest.getTransactionType().equals(TransactionType.PAYMENT)) {
				accountAssoc.setBalanceAvailable(accountAssoc.getBalanceAvailable() + transactionRequest.getAmount());
			}

			if(transactionRequest.getTransactionType().equals(TransactionType.INCOME) ) {
				accountAssoc.setBalanceAvailable(accountAssoc.getBalanceAvailable() + transactionRequest.getAmount());
				accountAssoc.setBalance(accountAssoc.getBalance() + transactionRequest.getAmount());
			}
		} else if(action.equals("DELETE") || action.equals("UPDATE")) {
			if(transactionRequest.getTransactionType().equals(TransactionType.EXPENSE)) {
				accountAssoc.setBalanceAvailable(accountAssoc.getBalanceAvailable() + transactionRequest.getAmount());
			}

			if(transactionRequest.getTransactionType().equals(TransactionType.PAYMENT)) {
				accountAssoc.setBalanceAvailable(accountAssoc.getBalanceAvailable() - transactionRequest.getAmount());
			}

			if(transactionRequest.getTransactionType().equals(TransactionType.INCOME) ) {
				accountAssoc.setBalanceAvailable(accountAssoc.getBalanceAvailable() - transactionRequest.getAmount());
				accountAssoc.setBalance(accountAssoc.getBalance() - transactionRequest.getAmount());
			}
		}

		accountRepository.save(accountAssoc);
	}

	@Transactional(rollbackFor = {CustomException.class, ValidationException.class})
	private void modifyAmountAccountFromTransference(Transference transferenceToDelete) {
		Account accountOrigin;
		Account accountDestiny;

		accountDestiny = transferenceToDelete.getAccountDestiny();
		accountOrigin = transferenceToDelete.getAccountOrigin();

		if(!transferenceToDelete.getTypeTransference().equals(TypeTransference.EXTERN)) {
			accountOrigin.setBalanceAvailable(accountOrigin.getBalanceAvailable() + transferenceToDelete.getAmount());
			accountDestiny.setBalanceAvailable(accountDestiny.getBalanceAvailable() - transferenceToDelete.getAmount());

			if (accountOrigin.getAccountType().getTypeName().equals("PARENT") &&
					accountDestiny.getAccountType().getTypeName().equals("PARENT")) {
				accountOrigin.setBalance(accountOrigin.getBalance() + transferenceToDelete.getAmount());
				accountDestiny.setBalance(accountDestiny.getBalance() - transferenceToDelete.getAmount());
			} else if (accountOrigin.getAccountType().getTypeName().equals("PARENT")) {
				accountDestiny.setBalance(accountDestiny.getBalance() - transferenceToDelete.getAmount());
			} else if (accountDestiny.getAccountType().getTypeName().equals("PARENT")) {
				accountOrigin.setBalance(accountOrigin.getBalance() + transferenceToDelete.getAmount());
			} else {
				accountOrigin.setBalance(accountOrigin.getBalance() + transferenceToDelete.getAmount());
				accountDestiny.setBalance(accountDestiny.getBalance() - transferenceToDelete.getAmount());
			}

			accountRepository.save(accountOrigin);
			accountRepository.save(accountDestiny);
		} else {
			accountDestiny.setBalance(accountDestiny.getBalance() - transferenceToDelete.getAmount());
			accountDestiny.setBalanceAvailable(accountDestiny.getBalanceAvailable() - transferenceToDelete.getAmount());
			accountRepository.save(accountDestiny);
		}

	}

	private List<Tag> validateTagsAndSaveIfNotExists(Transaction expenseRequest, String messageLog) {

		List<Tag> tagsSave = new ArrayList<>();
		if (expenseRequest.getTag().size() > 0) {
			for (Tag tag : expenseRequest.getTag()) {
				LOG.info(messageLog + " SAVING TAGS TO DB");
				tagsSave.add(tagRepo.save(tag));
			}
		}

		return tagsSave;
	}

	private List<Vouchers> validateVouchersAndSaveIfNotExists(Transaction expenseRequest, String messageLog) {
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

	private List<Reposition> saveOrUpdateRepositions(Transaction expenseRequest, String messageLog) {
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
	public Response savePay(Transaction payRequest, String messageLog) throws CustomException {
		return null;
	}

	@Override
	@Transactional(rollbackFor = {CustomException.class, ValidationException.class})
	public Response deleteTransactionById(Long idTransaction, String messageLog) {

		Response response = new Response();
		Transaction transactionDeleted = transactionRepository.findById(idTransaction).orElse(null);
		if(transactionDeleted == null) {
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_EXPENSE_TO_ASSOC_DONT_EXIST);
		}

		if(!transactionDeleted.getAccount().getTypeCard().getName().equals("CREDIT")) {

			if(transactionDeleted.getTransactionType().equals(TransactionType.EXPENSE)) {
				if((transactionDeleted.isPendingPay() && transactionDeleted.getAmountPayed() > 0 ) || !transactionDeleted.isPendingPay() ) {
					List<Transaction> paymentsAssocToExpenseToDelete = transactionRepository.findPaymentsAssocToExpenseDeleteByExpenseId(idTransaction, transactionDeleted.getPeriod().getId());
					for ( Transaction paymentAssocToDelete : paymentsAssocToExpenseToDelete) {
						modifyAmountAccountFromTransaction(paymentAssocToDelete, "DELETE");
						transactionRepository.deleteById(paymentAssocToDelete.getId());
					}
				}
			}

			if(transactionDeleted.getTransactionType().equals(TransactionType.PAYMENT)) {
				Transaction expenseAssocToPayment = transactionRepository.findExpenseAssocToPaymentByIdExpenseToPayment(transactionDeleted.getIdExpenseToPay(), transactionDeleted.getPeriod().getId());
				expenseAssocToPayment.setAmountPayed(expenseAssocToPayment.getAmountPayed() - transactionDeleted.getAmount());
				expenseAssocToPayment.setPendingPay(true);
				transactionRepository.save(expenseAssocToPayment);
			}

		} else {
			List<Transference> transferencesAssocToExpenseDeleteWithAccCreditCard = transactionRepository.findTransferencesAssocExpenseToDeleteWithAccountCreditCardByExpenseId(idTransaction, transactionDeleted.getPeriod().getId());
			for ( Transference transferenceAssocToDelete : transferencesAssocToExpenseDeleteWithAccCreditCard) {
				modifyAmountAccountFromTransference(transferenceAssocToDelete);
				transferenceRepository.deleteById(transferenceAssocToDelete.getId());
			}
		}

		modifyAmountAccountFromTransaction(transactionDeleted, "DELETE");
		transactionRepository.deleteById(transactionDeleted.getId());

		LOG.info(messageLog + properties.RESPONSE_GENERIC_DELETE_SUCCESS_MESSAGE);

		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_DELETE_SUCCESS_MESSAGE);

		return response;
	}

	@Override
	@Transactional(rollbackFor = {CustomException.class, ValidationException.class})
	public Response updateTransactionById(Transaction transactionRequest, Long idTransaction, String messageLog) {

		Response response = new Response();
		Transaction transactionToSave = new Transaction();

		Transaction transactionFounded = transactionRepository.findById(idTransaction).orElse(null);

		if(transactionFounded == null) {
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_EXPENSE_TO_ASSOC_DONT_EXIST);
		}

		modifyAmountAccountFromTransaction(transactionFounded, "UPDATE");

		if(transactionFounded.getTransactionType().equals(TransactionType.EXPENSE)) {
			if(transactionRequest.getAmount() < transactionFounded.getAmountPayed()) {
				throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_NEW_AMOUNT_IS_GRATHER_THAN_AMOUNT_PAYED_TO_UPDATE);
			}

			if(transactionRequest.getAmount() > transactionRequest.getAccount().getBalanceAvailable()){
				throw new CustomException(properties.RESPONSE_AMOUNT_TO_EXPENSE_IS_GRATHER_THAN_TO_AVAILABLE_AMOUNT_ACCOUNT);
			}

			transactionRequest.setAmountToRecover(transactionRequest.getAmount());

			if(transactionRequest.getAmountToRecover() > transactionFounded.getAmountPayed()) {
				LOG.info(messageLog + "La transacción se maracará como PENDIENTE DE PAGO, ya que su monto a devolver es mayor que el monto pagado.");
				transactionRequest.setPendingPay(true);
				transactionRequest.setReposition(saveOrUpdateRepositions(transactionRequest, messageLog));
			}
		}

		if(transactionFounded.getTransactionType().equals(TransactionType.PAYMENT)) {
			Transaction expenseAssocToPayment = transactionRepository.findExpenseAssocToPaymentByIdExpenseToPayment(transactionFounded.getIdExpenseToPay(), transactionFounded.getPeriod().getId());
			expenseAssocToPayment.setAmountPayed(expenseAssocToPayment.getAmountPayed() - transactionFounded.getAmount());
			expenseAssocToPayment.setPendingPay(true);
			transactionRepository.save(expenseAssocToPayment);

			Response responseEditPayment = saveTransaction(transactionFounded, messageLog);
			responseEditPayment.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
			responseEditPayment.setMessage(properties.RESPONSE_GENERIC_UPDATE_SUCCESS_MESSAGE);
			return response;
		}

		transactionRequest.setTag(validateTagsAndSaveIfNotExists(transactionRequest, messageLog));
		transactionRequest.setVouchers(validateVouchersAndSaveIfNotExists(transactionRequest, messageLog));

		modifyAmountAccountFromTransaction(transactionRequest, "SAVE");

		transactionToSave = transactionRepository.save(transactionRequest);
		LOG.info(messageLog + properties.RESPONSE_GENERIC_SAVE_SUCCESS_MESSAGE);

		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_UPDATE_SUCCESS_MESSAGE);
		response.setObject(transactionToSave);

		return response;
	}

	@Override
	public List<Transaction> findTransactionByWorskpaceIdAndDateRange(Long idWorkspace, String dateBegin, String dateEnd) {

		return transactionRepository.findTransactionByWorskpaceIdAndDateRange(idWorkspace,
				Utils.convertStringToDate(dateBegin), Utils.convertStringToDate(dateEnd));
	}

	@Override
	public List<Transaction> findTransactionByAccountIdAndPeriodId(Long idAccount, Long idPeriod) {
		return transactionRepository.findTransactionByAccountIdAndPeriodId(idAccount, idPeriod);
	}
}

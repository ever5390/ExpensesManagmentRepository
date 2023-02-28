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
import pe.com.erp.expensemanager.modules.categories.model.Category;
import pe.com.erp.expensemanager.modules.categories.repository.CategoryRepository;
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
	@Autowired
	private CategoryRepository categoryRepository;

	@Override
	@Transactional(rollbackFor = {CustomException.class, ValidationException.class})
	public Response saveTransaction(Transaction transactionRequest, String messageLog) throws CustomException {

		Response response = new Response();
		boolean modifyAccount = true;
		Transaction transactionToSave = new Transaction();
		Transaction expenseAssocToPay = new Transaction();

		LOG.info(messageLog + "La transacción recibida es de tipo: " + transactionRequest.getTransactionType());
		LOG.info(messageLog + "transaction request: " + transactionRequest.toString());

		try {

			if(transactionRequest.getIdExpenseToPay() != 0) {
				expenseAssocToPay = transactionRepository.findById(transactionRequest.getIdExpenseToPay()).orElse(null);
				if(expenseAssocToPay == null) {
					throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_EXPENSE_TO_PAY_DONT_EXIST);
				}

				if(expenseAssocToPay != null && expenseAssocToPay.getAccount() != null
						&& expenseAssocToPay.getAccount().getTypeCard().getName().toUpperCase().equals("CREDIT")) {
					LOG.info(messageLog + "El gasto asociado al  pago en cuestión se realizó con una cuenta de tipo TARJETA DE CREDITO");
					LOG.info(messageLog + "Se registrará una transacción de tipo gasto adicional al pago.");
					LOG.info(messageLog + "Procesango registro de gasto para la cuenta recibida; " + transactionRequest.getAccount().getFinancialEntity().getName());
					transactionRequest.setTransactionType(TransactionType.EXPENSE);
					transactionRequest.setAmountToRecover(0.0);
					transactionRequest.setAmountPayed(0.0);
					transactionRequest.setReposition(new ArrayList<Reposition>());
				}
			}

			if(transactionRequest.getTransactionType().equals(TransactionType.EXPENSE)) {
				LOG.info(messageLog + "Validando parámetros de cuenta y montos");
				if(transactionRequest.getAmount() > transactionRequest.getAccount().getBalanceAvailable()){
					throw new CustomException(properties.RESPONSE_AMOUNT_TO_EXPENSE_IS_GRATHER_THAN_TO_AVAILABLE_AMOUNT_ACCOUNT);
				}
				transactionRequest.setPendingPay(false);
				
				transactionRequest = validRepositionIfExist(transactionRequest, messageLog);

				if(transactionRequest.getAccount().getTypeCard().getName().equals("CREDIT") && transactionRequest.getAmountToRecover() == 0) {
					LOG.info(messageLog + "Se recibió el registro de gasto con Tarjeta de Crédito Y monto a reponer:0 , se procede a actualizar al monto gastado. " + transactionRequest.getAmount());
					transactionRequest.setPendingPay(true);
					transactionRequest.setAmountToRecover(transactionRequest.getAmount());
					Reposition repositionOwner = new Reposition();
					repositionOwner.setAmountToRepo(transactionRequest.getAmount());
					repositionOwner.setAmountToRepoPayed(0.0);
					transactionRequest.setReposition(saveOrUpdateRepositions(transactionRequest, messageLog));
				}

				if(expenseAssocToPay != null && expenseAssocToPay.getAccount()!= null
						&& expenseAssocToPay.getAccount().getTypeCard().getName().toUpperCase().equals("CREDIT")) {
					LOG.info(messageLog + "Seteando tag si existieran y actualizando montos de cuentas por el gasto a realizar");
					transactionRequest.setTag(validateTagsAndSaveIfNotExists(transactionRequest, messageLog));
					String description = "";
					description = "Pago TC: " + transactionRequest.getDescription();
					transactionRequest.setDescription(description);
					transactionRequest.setPendingPay(false);
					LOG.info(messageLog + "Validación de montos y cuentas pasaron correctamente");
					modifyAmountAccountFromTransaction(transactionRequest, "SAVE");

					transactionRepository.save(transactionRequest);
					LOG.info(messageLog + "Registro de gasto producto del pago de TC fue realizado correctamente");
					LOG.info(messageLog + "----------------------------------------------------------------------");
					LOG.info(messageLog + "Procesango registro de PAGO para la cuenta TC; ");
					transactionRequest.setTransactionType(TransactionType.PAYMENT);
				}
			}

			if(transactionRequest.getTransactionType().equals(TransactionType.REMINDER)) {
				LOG.info(messageLog + "La transacción se marcará como PENDIENTE DE PAGO, por ser recoradotorio");
				transactionRequest.setPendingPay(true);
				modifyAccount = false;
			}

			if(transactionRequest.getTransactionType().equals(TransactionType.INCOME)) {
				transactionRequest.setPendingPay(false);
				transactionRequest = validRepositionIfExist(transactionRequest, messageLog);
			}

			if(transactionRequest.getTransactionType().equals(TransactionType.PAYMENT)) {
				LOG.info(messageLog + "validando parámetros del pago ");
				double expensePendingAmount = expenseAssocToPay.getAmountToRecover() - expenseAssocToPay.getAmountPayed();
				if(transactionRequest.getAmount() > expensePendingAmount){
					throw new CustomException(properties.RESPONSE_AMOUNT_TO_PAYMENT_IS_GRATHER_THAN_TO_AMOUNT_PENDING_EXPENSE + " [ de S./" + expensePendingAmount + "]");
				}
				LOG.info(messageLog + "Actualización del monto pagado de la cuenta TC " + expenseAssocToPay.getAccount().getFinancialEntity().getName());
				expenseAssocToPay.setAmountPayed(expenseAssocToPay.getAmountPayed() + transactionRequest.getAmount());

				if(expenseAssocToPay.getAmountToRecover() == expenseAssocToPay.getAmountPayed()) {
					LOG.info(messageLog + "Monto PAGADO de TC es IGUAL al monto a reponer ");
					LOG.info(messageLog + "El gasto se maracará como RESPUESTO en su totalidad ");
					expenseAssocToPay.setPendingPay(false);
				}

				transactionRepository.save(expenseAssocToPay);
				LOG.info(messageLog + "Se actualizó el monto pagado correctamente");
				transactionRequest.setAmountToRecover(0.0);
				transactionRequest.setAmountPayed(0.0);

				if(expenseAssocToPay.getTransactionType().equals(TransactionType.INCOME)) {
					transactionRequest.setTransactionType(TransactionType.EXPENSE);
				}

				if(expenseAssocToPay.getAccount().getTypeCard().getName().toUpperCase().equals("CREDIT")) {
					transactionRequest.setAccount(expenseAssocToPay.getAccount());
				}
			}

			if(transactionRequest.getCategory().getId() == 0){
				LOG.info(messageLog + "Se encontró una categoría nueva, se procede a registrarla.");
				Category newCategorySave = transactionRequest.getCategory();
				transactionRequest.setCategory(categoryRepository.save(newCategorySave));
				LOG.info(messageLog + "Registro de la categoría realizada correctamente.");
			}

			transactionRequest.setTag(validateTagsAndSaveIfNotExists(transactionRequest, messageLog));

			LOG.info(messageLog + "Se procede a modificar los montos de las cuentas afectadas de la transacción principal de tipo : " +  transactionRequest.getTransactionType());
			if(modifyAccount)
				modifyAmountAccountFromTransaction(transactionRequest, "SAVE");

			LOG.info(messageLog + "Se actualizaron los montos de las cuentas correctamente");
			transactionToSave = transactionRepository.save(transactionRequest);

			LOG.info(messageLog + properties.RESPONSE_GENERIC_SAVE_SUCCESS_MESSAGE);
		} catch( Exception e) {
			LOG.info(messageLog + e);
			throw new CustomException(e.getMessage());
		}

		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_SAVE_SUCCESS_MESSAGE);
		response.setObject(transactionToSave);

		return response;
	}

	private Transaction validRepositionIfExist(Transaction transactionRequest, String messageLog) {
		if(transactionRequest.getAmount() < transactionRequest.getAmountToRecover()) {
			LOG.info(messageLog + "El monto es menor al monto a reponer");
			throw new CustomException("El monto es menor al monto a reponer, ingrese un monto mayor o no genere reposición.");
		}

		if(transactionRequest.getAmountToRecover() < transactionRequest.getAmountPayed()) {
			LOG.info(messageLog + "El monto a reponer es menor al monto pagado");
			throw new CustomException("El monto a reponer es menor al monto pagado, ingrese un monto mayor.");
		}

		if(transactionRequest.getAmountToRecover() > transactionRequest.getAmountPayed()) { //> 0 si es nuevo, else::
			LOG.info(messageLog + "La transacción se marcará como PENDIENTE DE PAGO, ya que su monto a devolver es mayor que CERO");
			transactionRequest.setPendingPay(true);
			transactionRequest.setReposition(saveOrUpdateRepositions(transactionRequest, messageLog));
		}

		return transactionRequest;
	}

	@Transactional(rollbackFor = {CustomException.class, ValidationException.class})
	private Account modifyAmountAccountFromTransaction(Transaction transactionRequest, String action) {
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
			}
		}

		return accountRepository.save(accountAssoc);
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
				//reposition.setTransaction(expenseSaved);
				reposition.setPartnerToPay(partnerRepository.save(reposition.getPartnerToPay()));
				//LOG.info(messageLog + " SAVING NOTIFICATIONS WHEN PARTNER ITS SENDED STATUS TO DB");
				//if(reposition.getPartnerToPay().getStatusRequest().equals(StatusInvitationsPartner.SENDED)) {
					//notifRepo.save();
				//}
				repositions.add(repositionRepo.save(reposition));
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
		LOG.info(messageLog + " Inicio de ELIMINACIÓN de registro");
		Response response = new Response();
		try {
			Transaction transactionDeleted = transactionRepository.findById(idTransaction).orElse(null);
			if(transactionDeleted == null) {
				LOG.info(messageLog + " No se encontró el registro a eliminar por ID obtenido: " + idTransaction);
				throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_EXPENSE_TO_ASSOC_DONT_EXIST);
			}
			LOG.info(messageLog + " El registro a eliminar es el siguiente: " + transactionDeleted.toString());
			LOG.info(messageLog + " Registro a eliminar es de tipo: " + transactionDeleted.getTransactionType());

			if(transactionDeleted.getTransactionType().equals(TransactionType.EXPENSE)) {
				LOG.info(messageLog + " Se procede a efectuar las devoluciones de los montos gastados a sus respectivas cuentas");

				if(transactionDeleted.getAmountPayed() > 0 ) {
					LOG.info(messageLog + " Se encontró que el monto pagado es mayor CERO, por tanto existen pagos asociados, se procede a ubicarlos y eliminarlos");
					List<Transaction> paymentsAssocToExpenseToDelete = transactionRepository.findPaymentsAssocToExpenseDeleteByExpenseIdAndPeriodId(idTransaction, transactionDeleted.getPeriod().getId());
					if(paymentsAssocToExpenseToDelete.size() == 0){
						paymentsAssocToExpenseToDelete = transactionRepository.findPaymentsAssocToExpenseDeleteByExpenseIdAndWorkspaceId(idTransaction, transactionDeleted.getPeriod().getWorkspace().getId());
					}

					if(paymentsAssocToExpenseToDelete.size() == 0) {
						throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_NOTFOUND_PAYMENTS_ASSOC_TO_EXPENSE_TO_DELETE);
					}

					for ( Transaction paymentAssocToDelete : paymentsAssocToExpenseToDelete) {
						modifyAmountAccountFromTransaction(paymentAssocToDelete, "DELETE");
						transactionRepository.deleteById(paymentAssocToDelete.getId());
					}
					LOG.info(messageLog + " Los pagos asociados fueron procesados y eliminados correctamente. el monto fue descontado de sus cuentas respectivamente");

				}
			}

			if(transactionDeleted.getTransactionType().equals(TransactionType.PAYMENT)) {
				Transaction expenseAssocToPayment = transactionRepository.findExpenseAssocToPaymentByIdExpenseToPaymentAndPeriodId(transactionDeleted.getIdExpenseToPay(), transactionDeleted.getPeriod().getId());

				if(expenseAssocToPayment == null){
					expenseAssocToPayment = transactionRepository.findExpenseAssocToPaymentByIdExpenseToPaymentAndWorkspaceId(transactionDeleted.getIdExpenseToPay(), transactionDeleted.getPeriod().getWorkspace().getId());
				}

				if(expenseAssocToPayment == null) {
					throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_NOTFOUND_EXPENSE_ASSOC_TO_PAYMENT_TO_DELETE);
				}

				expenseAssocToPayment.setAmountPayed(expenseAssocToPayment.getAmountPayed() - transactionDeleted.getAmount());
				expenseAssocToPayment.setPendingPay(true);
				transactionRepository.save(expenseAssocToPayment);
				LOG.info(messageLog + " Se realizó el descuento del monto pagado al registro de gasto asociado, ya que el pago no existirá más. ");
				if(transactionDeleted.getAccount().getBalanceAvailable() < transactionDeleted.getAmount()) {
					throw new CustomException("La cuenta no tiene fondos suficiente para procesar el descuento correspondiente producto del intento de eliminación de la transacción, aumente el saldo de la cuenta.");
				}
			}

			if(transactionDeleted.getTransactionType().equals(TransactionType.INCOME)) {
				if(transactionDeleted.getAccount().getBalanceAvailable() < transactionDeleted.getAmount()) {
					throw new CustomException("La cuenta no tiene fondos suficiente para procesar el descuento correspondiente producto del intento de eliminación de la transacción, aumente el saldo de la cuenta.");
				}
			}

			modifyAmountAccountFromTransaction(transactionDeleted, "DELETE");
			transactionRepository.deleteById(transactionDeleted.getId());

			LOG.info(messageLog + properties.RESPONSE_GENERIC_DELETE_SUCCESS_MESSAGE);

		} catch(Exception e) {
			LOG.error(e.getMessage());
			throw new CustomException(e.getMessage());
		}


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
		boolean bothAccountsEquals = false;
		LOG.info(messageLog + "=====================================");
		LOG.info(messageLog + " Inicio de ACTUALIZACIÓN de registro");
		LOG.info(messageLog + "======================================");

		Transaction transactionFounded = transactionRepository.findById(idTransaction).orElse(null);

		if(transactionFounded == null) {
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_EXPENSE_TO_ASSOC_DONT_EXIST);
		}
		LOG.info(messageLog + " La tipología de la transacción a actualizar es: " + transactionFounded.getTransactionType());
		LOG.info(messageLog + " La Transacción a modificar es: " + transactionFounded.getAccount().getBalanceAvailable());

		if(transactionRequest.getAccount().getId() == transactionFounded.getAccount().getId()) {
			bothAccountsEquals = true;
		}

		LOG.info(messageLog + "Se procede a realizar las devoluciones o descuentos previos a actualizar los nuevos parámetros al registro.");

		if(transactionFounded.getTransactionType().equals(TransactionType.EXPENSE)) {
			if(bothAccountsEquals) {
				transactionRequest.setAccount(modifyAmountAccountFromTransaction(transactionFounded, "UPDATE"));
			} else {
				modifyAmountAccountFromTransaction(transactionFounded, "UPDATE");
			}

			Transaction paymentAssocToExpenseSelected = null;

			if(transactionFounded.getIdExpenseToPay() != 0) {
				paymentAssocToExpenseSelected = transactionRepository.findExpenseAssocToPaymentByIdExpenseToPaymentAndWorkspaceId(transactionFounded.getIdExpenseToPay(), transactionFounded.getPeriod().getWorkspace().getId());

				if(paymentAssocToExpenseSelected == null) {
					throw new CustomException("El pago asociado no pudo ser ubicado. Valide si existe por favor.");
				}

				if(paymentAssocToExpenseSelected.getAccount().getTypeCard().getName().toUpperCase().equals("CREDIT")) {

					LOG.info(messageLog + "Se procede a ubicar al gasto TC asociado al pago seleccionado");
					Transaction expenseTCAssocPaymentByTransactionId = transactionRepository.findExpenseAssocToPaymentByIdExpenseToPaymentAndPeriodId(paymentAssocToExpenseSelected.getIdExpenseToPay(), paymentAssocToExpenseSelected.getPeriod().getId());

					if(expenseTCAssocPaymentByTransactionId == null){
						expenseTCAssocPaymentByTransactionId = transactionRepository.findExpenseAssocToPaymentByIdExpenseToPaymentAndWorkspaceId(paymentAssocToExpenseSelected.getIdExpenseToPay(), paymentAssocToExpenseSelected.getPeriod().getWorkspace().getId());
					}

					if(expenseTCAssocPaymentByTransactionId == null) {
						throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_NOTFOUND_EXPENSE_ASSOC_TO_PAYMENT_TO_DELETE);
					}

					LOG.info(messageLog + "Se procede a modificar los montos al gasto pagado y el estado del gasto TC asociado al pago seleccionado");

					double finalAmountAvailableAccountAssocToExpendFounded = paymentAssocToExpenseSelected.getAccount().getBalanceAvailable() + paymentAssocToExpenseSelected.getAmount() - transactionRequest.getAmount();
					if(finalAmountAvailableAccountAssocToExpendFounded < 0) {
						throw new CustomException("La cuenta asociada al pago[" + paymentAssocToExpenseSelected.getId() +"] producto del pago del gasto TC, no cuenta con saldo suficiente para actualizar el nuevo monto a modificar.");
					}

					double totalAmountModifiedToPayed = expenseTCAssocPaymentByTransactionId.getAmountPayed() - paymentAssocToExpenseSelected.getAmount() + transactionRequest.getAmount();
					if(totalAmountModifiedToPayed > expenseTCAssocPaymentByTransactionId.getAmountToRecover()) {
						throw new CustomException("El monto total pagado de la cuenta TC luego de modificar supera al monto a reponer, ingrese un monto menor");
					}
					expenseTCAssocPaymentByTransactionId.setAmountPayed(expenseTCAssocPaymentByTransactionId.getAmountPayed() - paymentAssocToExpenseSelected.getAmount() + transactionRequest.getAmount());
					expenseTCAssocPaymentByTransactionId.setPendingPay(true);
					transactionRepository.save(expenseTCAssocPaymentByTransactionId);
					LOG.info(messageLog + "Se realizó la actualización del monto pagado del gasto TC");
					LOG.info(messageLog + "-----------------------------------------------------------");
					LOG.info(messageLog + "Se procede a modificar los montos del pago asociado asociado al gasto TC y la cuenta TC");
					paymentAssocToExpenseSelected.setAmount(transactionRequest.getAmount());
					paymentAssocToExpenseSelected.getAccount().setBalanceAvailable(finalAmountAvailableAccountAssocToExpendFounded);
					transactionRepository.save(paymentAssocToExpenseSelected);
					LOG.info(messageLog + "Se realizó la actualización de montos del gasto asociado al pago por pago de TC y su respectiva cuenta.");
				}
			}
		}

		if(transactionFounded.getTransactionType().equals(TransactionType.INCOME)) {
			if(transactionRequest.getTransactionType().equals(TransactionType.INCOME)) {
				if(bothAccountsEquals) {
					if ((transactionRequest.getAmount() - transactionFounded.getAmount()) > 0 ){
						transactionRequest.setAccount(modifyAmountAccountFromTransaction(transactionFounded, "UPDATE"));
					} else if(transactionFounded.getAccount().getBalanceAvailable() >= Math.abs(transactionRequest.getAmount() - transactionFounded.getAmount()) ) {
						transactionRequest.setAccount(modifyAmountAccountFromTransaction(transactionFounded, "UPDATE"));
					} else {
						throw new CustomException("La cuenta no tiene fondos suficiente para procesar el nuevo monto, ingrese un nuevo monto de ingreso mayor.");
					}
				} else {
					if(transactionFounded.getAccount().getBalanceAvailable() >= transactionFounded.getAmount()) {
						modifyAmountAccountFromTransaction(transactionFounded, "UPDATE");
					} else {
						throw new CustomException("La cuenta no tiene fondos suficiente para procesar el nuevo monto, ingrese un nuevo monto de ingreso mayor.");
					}
				}
			} else {
				if(transactionFounded.getAccount().getBalanceAvailable() >= transactionFounded.getAmount()) {
					if(bothAccountsEquals) {
						transactionRequest.setAccount(modifyAmountAccountFromTransaction(transactionFounded, "UPDATE"));
					} else {
						modifyAmountAccountFromTransaction(transactionFounded, "UPDATE");
					}
				} else {
					throw new CustomException("La cuenta no tiene fondos suficiente para procesar la actualización.");
				}
			}
		}

		if(transactionFounded.getTransactionType().equals(TransactionType.PAYMENT)) {
			LOG.info(messageLog + "Se procede a ubicar al gasto TC asociado al pago seleccionado");
			Transaction expenseTCAssocPaymentByTransactionId = transactionRepository.findExpenseAssocToPaymentByIdExpenseToPaymentAndPeriodId(transactionFounded.getIdExpenseToPay(), transactionFounded.getPeriod().getId());

			if(expenseTCAssocPaymentByTransactionId == null){
				expenseTCAssocPaymentByTransactionId = transactionRepository.findExpenseAssocToPaymentByIdExpenseToPaymentAndWorkspaceId(transactionFounded.getIdExpenseToPay(), transactionFounded.getPeriod().getWorkspace().getId());
			}

			if(expenseTCAssocPaymentByTransactionId == null) {
				throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_NOTFOUND_EXPENSE_ASSOC_TO_PAYMENT_TO_DELETE);
			}

			LOG.info(messageLog + "Se procede a modificar los montos al gasto pagado y el estado del gasto TC asociado al pago seleccionado, se aplican descuetnos correspondientes.");

			expenseTCAssocPaymentByTransactionId.setAmountPayed(expenseTCAssocPaymentByTransactionId.getAmountPayed() - transactionFounded.getAmount());
			expenseTCAssocPaymentByTransactionId.setPendingPay(true);
			transactionRepository.save(expenseTCAssocPaymentByTransactionId);

			if(expenseTCAssocPaymentByTransactionId.getAccount().getTypeCard().getName().toUpperCase().equals("CREDIT")) {
				LOG.info(messageLog + "Se procede a ubicar el gasto asociado al pago e intentar modificar los montos del gasto y cuenta asociadas a este.");
				Transaction expenseAssocToPaymentID = transactionRepository.findTransactionAssocByIdTransactionAndWorkspaceId(transactionFounded.getId(), transactionFounded.getPeriod().getWorkspace().getId());

				if(expenseAssocToPaymentID == null) {
					throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_NOTFOUND_EXPENSE_ASSOC_TO_PAYMENT_TO_DELETE);
				}

				double finalAmountAvailableAccountAssocToExpendFounded = expenseAssocToPaymentID.getAccount().getBalanceAvailable() + expenseAssocToPaymentID.getAmount() - transactionRequest.getAmount();
				if(finalAmountAvailableAccountAssocToExpendFounded < 0) {
					throw new CustomException("La cuenta asociada al gasto[" + expenseAssocToPaymentID.getId() +"] producto del pago del gasto TC, no cuenta con saldo suficiente para actualizar el nuevo monto a modificar.");
				}

				expenseAssocToPaymentID.setAmount(transactionRequest.getAmount());
				expenseAssocToPaymentID.getAccount().setBalanceAvailable(finalAmountAvailableAccountAssocToExpendFounded);
				transactionRepository.save(expenseAssocToPaymentID);
				LOG.info(messageLog + "Se realizó la actualización de montos del gasto asociado al pago por pago de TC y su respectiva cuenta.");
			}
			LOG.info(messageLog + "Se inicia el proceo de actualización del nuevo monto del pago seleccionado.");
			transactionRequest = processingPayment(transactionRequest, bothAccountsEquals, transactionFounded);
		}


		LOG.info(messageLog + "Devoluciones y descuentos realizados existosamente.");
		LOG.info(messageLog + "Se inicia con el registro y actualización de los nuevos valores de la transacción inicial seleccionada para sus edición.");
		Response responseSave = saveTransaction(transactionRequest, messageLog);
		if(!responseSave.getStatus().equals("success")) {
			throw new CustomException(response.getMessage());
		}

		LOG.info(messageLog + "ACTUALIZACIÓN COMPLETA..");
		response.setObject(responseSave.getObject());

		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_UPDATE_SUCCESS_MESSAGE);

		return response;
	}

	private Transaction processingPayment(Transaction transactionRequest, boolean bothAccountsEquals, Transaction transactionFounded) {
		if(transactionRequest.getTransactionType().equals(TransactionType.PAYMENT) ) {
			if(bothAccountsEquals) {
				if ((transactionRequest.getAmount() - transactionFounded.getAmount()) > 0 ){
					transactionRequest.setAccount(modifyAmountAccountFromTransaction(transactionFounded, "UPDATE"));
				} else if(transactionFounded.getAccount().getBalanceAvailable() >= Math.abs(transactionRequest.getAmount() - transactionFounded.getAmount()) ) {
					transactionRequest.setAccount(modifyAmountAccountFromTransaction(transactionFounded, "UPDATE"));
				} else {
					throw new CustomException("La cuenta no tiene fondos suficiente para procesar el nuevo monto, ingrese un nuevo monto de ingreso mayor.");
				}
			} else {
				if(transactionFounded.getAccount().getBalanceAvailable() >= transactionFounded.getAmount()) {
					modifyAmountAccountFromTransaction(transactionFounded, "UPDATE");
				} else {
					throw new CustomException("La cuenta no tiene fondos suficiente para procesar el nuevo monto, ingrese un nuevo monto de ingreso mayor.");
				}
			}
		} else {
			if(transactionFounded.getAccount().getBalanceAvailable() >= transactionFounded.getAmount()) {
				if(bothAccountsEquals) {
					transactionRequest.setAccount(modifyAmountAccountFromTransaction(transactionFounded, "UPDATE"));
				} else {
					modifyAmountAccountFromTransaction(transactionFounded, "UPDATE");
				}

				transactionRequest.setIdExpenseToPay(0L);
				transactionRequest.setAmountToRecover(0.0);
				transactionRequest.setAmountPayed(0.0);
			} else {
				throw new CustomException("La cuenta no tiene fondos suficiente para procesar la actualización.");
			}
		}

		return transactionRequest;
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

	@Override
	public Transaction updateVouchers(Transaction transactionUpdateVoucher, String messageLog) {
		LOG.info(messageLog + " Se procede a almacenar los vouchers y asignarlos a la transacción previamente almacenada");
		LOG.info("request: " + transactionUpdateVoucher.getVouchers());
		List<Vouchers> vouchersSaved = new ArrayList<>();
		for ( Vouchers voucherToSave : transactionUpdateVoucher.getVouchers()) {
			vouchersSaved.add(voucherRepo.save(voucherToSave));
		}
		transactionUpdateVoucher.setVouchers(vouchersSaved);
		return transactionRepository.save(transactionUpdateVoucher);
	}
}

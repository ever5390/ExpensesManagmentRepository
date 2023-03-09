package pe.com.erp.expensemanager.modules.transaction.services.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import pe.com.erp.expensemanager.exception.CustomException;
import pe.com.erp.expensemanager.modules.account.model.Account;
import pe.com.erp.expensemanager.modules.account.model.Transference;
import pe.com.erp.expensemanager.modules.account.model.TypeTransference;
import pe.com.erp.expensemanager.modules.account.repository.AccountRepository;
import pe.com.erp.expensemanager.modules.account.repository.TransferenceRepository;
import pe.com.erp.expensemanager.modules.categories.model.Category;
import pe.com.erp.expensemanager.modules.categories.repository.CategoryRepository;
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
		boolean orderExecuteModifyAccount = true;
		boolean expenseAssocToPayIsIncome = false;
		boolean registerPaymentTC = false;
		Account accountTCdetected = null;
		Transaction transactionToSave = new Transaction();
		Transaction expenseAssocToPay = new Transaction();

		LOG.info(messageLog + "La transacción recibida es de tipo: " + transactionRequest.getTransactionType());
		LOG.info(messageLog + "transaction request: " + transactionRequest.toString());

		try {

			if(transactionRequest.getTransactionType().equals(TransactionType.EXPENSE)) {
				if (transactionRequest.getId() == 0){
					transactionRequest.setIdExpenseToPay(0L);
				}
				transactionRequest.setPendingPay(false);
				LOG.info(messageLog + "Proceso de validación de monto disponible de la cuenta asociada al registro del gasto.");
				if(transactionRequest.getAmount() > transactionRequest.getAccount().getBalanceAvailable()){
					throw new CustomException(properties.RESPONSE_AMOUNT_TO_EXPENSE_IS_GRATHER_THAN_TO_AVAILABLE_AMOUNT_ACCOUNT);
				}
				LOG.info(messageLog + "Proceso de validación de tipo de gasto TC");
				if(transactionRequest.getAccount().getTypeCard().getName().equals("CREDIT") &&
						transactionRequest.getAmountToRecover() < transactionRequest.getAmount()) {
					throw new CustomException("El registro corresponde a un gasto TC, el monto a reponer debe ser igual al monto gastado.");
				}

				LOG.info(messageLog + "Proceso de validación de reposición y montos");
				transactionRequest = validRepositionIfExist(transactionRequest, messageLog);
			}

			if(transactionRequest.getTransactionType().equals(TransactionType.REMINDER)) {
				LOG.info(messageLog + "La transacción se marcará como PENDIENTE DE PAGO, por ser recoradotorio");
				transactionRequest.setPendingPay(true);
				transactionRequest.setIdExpenseToPay(0L);
				orderExecuteModifyAccount = false;
			}

			if(transactionRequest.getTransactionType().equals(TransactionType.INCOME)) {
				transactionRequest.setPendingPay(false);
				transactionRequest.setIdExpenseToPay(0L);
				transactionRequest = validRepositionIfExist(transactionRequest, messageLog);
				if(transactionRequest.getAmount() + transactionRequest.getAccount().getBalanceAvailable() < 0){
					throw new CustomException("El proceso de actualizar el monto de la cuenta genera un monto disponible menor a 0");
				}

				if(transactionRequest.getAccount().getTypeCard().getName().equalsIgnoreCase("CREDIT")) {
					throw new CustomException("No es posible registrar una transacción de tipo INGRESO con una cuenta de tipo CRÉDITO, por favor seleccione otro tipo de registro u otra cuenta.");
				}

			}

			if(transactionRequest.getTransactionType().equals(TransactionType.PAYMENT)) {

				if(transactionRequest.getId() == 0 && transactionRequest.getAccount().getTypeCard().getName().equalsIgnoreCase("CREDIT")) {
					throw new CustomException("No es posible registrar una transacción de tipo PAGO con una cuenta de tipo CRÉDITO, por favor seleccione otro tipo de registro u otra cuenta.");
				}

				expenseAssocToPay = transactionRepository.findById(transactionRequest.getIdExpenseToPay()).orElse(null);

				if(expenseAssocToPay == null)
					throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_EXPENSE_TO_PAY_DONT_EXIST);

				if(!expenseAssocToPay.getTransactionType().equals(TransactionType.INCOME) && transactionRequest.getAmount() + transactionRequest.getAccount().getBalanceAvailable() < 0)
					throw new CustomException("El proceso de actualizar el monto de la cuenta genera un monto disponible menor a 0");

				LOG.info(messageLog + "Actualizando el monto pagado de la transacción asociada.");
				double expensePendingAmount = expenseAssocToPay.getAmountToRecover() - expenseAssocToPay.getAmountPayed();
				if(transactionRequest.getAmount() > expensePendingAmount)
					throw new CustomException(properties.RESPONSE_AMOUNT_TO_PAYMENT_IS_GRATHER_THAN_TO_AMOUNT_PENDING_EXPENSE + " [ de S./" + expensePendingAmount + "]");

				expenseAssocToPay.setAmountPayed(Utils.roundTwoDecimalWithBigDecimal(expenseAssocToPay.getAmountPayed() + transactionRequest.getAmount()));

				if(expenseAssocToPay.getAmountToRecover() == expenseAssocToPay.getAmountPayed()) {
					LOG.info(messageLog + "El monto pagado de la transacción asociada es igual a su monto a reponer, se marcará con status: pagado");
					expenseAssocToPay.setPendingPay(false);
				}

				transactionRepository.save(expenseAssocToPay);
				LOG.info(messageLog + "Se actualizó el monto pagado de la transacción asociada correctamente");

				transactionRequest.setAmountToRecover(0.0);
				transactionRequest.setAmountPayed(0.0);

				if(expenseAssocToPay.getTransactionType().equals(TransactionType.INCOME)) {
					if(transactionRequest.getAccount().getBalanceAvailable() < transactionRequest.getAmount())
						throw new CustomException("La cuenta no tiene saldo disponible para efecutar el pago del préstamos seleccionado.");

					LOG.info(messageLog + "La transacción a pagar es de tipo INCOME");
					transactionRequest.setTransactionType(TransactionType.EXPENSE);
					expenseAssocToPayIsIncome = true;
				}

				if (transactionRequest.getId() == 0) {
					if(expenseAssocToPay.getTransactionType().equals(TransactionType.EXPENSE) &&
							expenseAssocToPay.getAccount().getTypeCard().getName().equalsIgnoreCase("CREDIT")) {
						LOG.info(messageLog + "La transacción a pagar es de tipo EXPENSE y es TC");
						Account accountRequest = transactionRequest.getAccount();
						transactionRequest.setAccount(expenseAssocToPay.getAccount());
						accountTCdetected = modifyAmountAccountFromTransaction(transactionRequest, "SAVE", true);
						LOG.info(messageLog + "El pago de un TC resulta en u descuenta de la cuenta seleccionada para pagar, se actualiza el tipo  EXPENSE y posteriormente se guardará un registro tipo PAYMENT para TC");
						transactionRequest.setAccount(accountRequest);
						transactionRequest.setTransactionType(TransactionType.EXPENSE);
						registerPaymentTC = true;
					}
				}
			}

			//BEGIN :: Funciones comunes
			transactionRequest.setCategory(validateCategoryNewIfExist(transactionRequest.getCategory(), messageLog));
			transactionRequest.setTag(validateTagsAndSaveIfNotExists(transactionRequest, messageLog));
			modifyAmountAccountFromTransaction(transactionRequest, "SAVE", orderExecuteModifyAccount);
			transactionRequest = RedondeandoMontosPrevSaveObject(transactionRequest);
			transactionRequest.setAccount(roundTwoDecimalBalanceAvailablePrevSaveObject(transactionRequest.getAccount()));
			//END :: Funciones comunes

			if(expenseAssocToPayIsIncome)
				transactionRequest.setTransactionType(TransactionType.PAYMENT);

			transactionToSave = transactionRepository.save(transactionRequest);

			if(registerPaymentTC) {
				transactionRequest.setTransactionType(TransactionType.PAYMENT);
				transactionRequest.setAccount(accountTCdetected);
				Transaction paymentTC = transactionRepository.save(transactionRequest);
				LOG.info(messageLog + "Se añade el SEGUNDO registro tipo payment de TC asociada al pago inicial del TC");
				transactionToSave.setIdExpenseToPay(paymentTC.getId());
				transactionRepository.save(transactionToSave);
				LOG.info(messageLog + "Se asocia el pagoTC al expense de donde salió el pago para el gasto original TC");
			}

		} catch (CustomException e) {
			throw new CustomException(e.getMessage());
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

	private Category validateCategoryNewIfExist(Category category, String messageLog) {
		if(category.getId() == 0){
			LOG.info(messageLog + "Se encontró una categoría nueva, se procede a registrarla.");
			category = categoryRepository.save(category);
			LOG.info(messageLog + "Se registró la categoría encontrada.");
		}
		return category;
	}

	private Transaction RedondeandoMontosPrevSaveObject(Transaction transactionToSave) {
		transactionToSave.setAmount(Utils.roundTwoDecimalWithBigDecimal(transactionToSave.getAmount()));
		transactionToSave.setAmountPayed(Utils.roundTwoDecimalWithBigDecimal(transactionToSave.getAmountPayed()));
		transactionToSave.setAmountToRecover(Utils.roundTwoDecimalWithBigDecimal(transactionToSave.getAmountToRecover()));
		return transactionToSave;
	}

	private Account roundTwoDecimalBalanceAvailablePrevSaveObject(Account account) {
		account.setBalanceAvailable(Utils.roundTwoDecimalWithBigDecimal(account.getBalanceAvailable()));
		return account;
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

		if(transactionRequest.getAmountToRecover() > transactionRequest.getAmountPayed()) {
			LOG.info(messageLog + "La transacción se marcará como PENDIENTE DE PAGO, ya que su monto a devolver es mayor que CERO");
			transactionRequest.setPendingPay(true);
			transactionRequest.setReposition(saveOrUpdateRepositions(transactionRequest, messageLog));
		}

		return transactionRequest;
	}

	@Transactional(rollbackFor = {CustomException.class, ValidationException.class})
	private Account modifyAmountAccountFromTransaction(Transaction transactionRequest, String action, boolean orderExecuteModify) {
		Account accountAssoc = new Account();
		accountAssoc = transactionRequest.getAccount();

		if(!orderExecuteModify) return accountAssoc;

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

		accountAssoc = roundTwoDecimalBalanceAvailablePrevSaveObject(accountAssoc);

		return accountRepository.save(accountAssoc);
	}

	private List<Tag> validateTagsAndSaveIfNotExists(Transaction expenseRequest, String messageLog) throws CustomException	{
		List<Tag> tagsSave = new ArrayList<>();
		try {
			if (expenseRequest.getTag().size() > 0) {
				for (Tag tag : expenseRequest.getTag()) {
					LOG.info(messageLog + " SAVING TAGS TO DB");
					tagsSave.add(tagRepo.save(tag));
				}
			}
		} catch (Exception e) {
			throw new CustomException(e.getMessage());
		}
		return tagsSave;
	}

	private List<Vouchers> validateVouchersAndSaveIfNotExists(Transaction expenseRequest, String messageLog) throws CustomException	{
		List<Vouchers> voucherToSave = new ArrayList<>();
		try {
			if(expenseRequest.getVouchers().size() > 0) {
				for ( Vouchers voucher: expenseRequest.getVouchers()) {
					//if(voucher.getId() == 0) {
					LOG.info(messageLog + " SAVING VOUCHERS TO DB");
					voucherToSave.add(voucherRepo.save(voucher));
					//}
				}
			}
		} catch (Exception e) {
			throw new CustomException(e.getMessage());
		}
		return voucherToSave;
	}

	private List<Reposition> saveOrUpdateRepositions(Transaction expenseRequest, String messageLog) throws CustomException	{
		List<Reposition> repositions = new ArrayList<>();
		try {
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
		} catch (Exception e) {
			throw new CustomException(e.getMessage());
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
		boolean isExpenseTC = false;

		try {
			Transaction transactionDeleted = transactionRepository.findById(idTransaction).orElse(null);
			if(transactionDeleted == null) {
				LOG.info(messageLog + " No se encontró el registro a eliminar por ID obtenido: " + idTransaction);
				throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_EXPENSE_TO_ASSOC_DONT_EXIST);
			}

			LOG.info(messageLog + " Registro a eliminar es de tipo: " + transactionDeleted.getTransactionType());
			LOG.info(messageLog + " El registro a eliminar es el siguiente: " + transactionDeleted.toString());

			if(transactionDeleted.getTransactionType().equals(TransactionType.EXPENSE)) {

				//Si es gasto normal :: reverso validando previamente si resultado no es menor a 0 [esto se hace al final, para todos.]

				//Si es gasto resultado del pago realizado a un registro de TC. :: buscar su pago Asociado, reversarlo previa validación, buscar el gasto TC y restar su monto pagado, actualizarlo y eliminar pago y luego gasto seleccioando.
				if(transactionDeleted.getIdExpenseToPay() != 0 ) {
					LOG.info(messageLog + " El registro es de tipo gasto Pago :: originado por el pago de un gasto TC");
					LOG.info(messageLog + " - Se procede a ubicar el pago que está asociado a este registro");
					Transaction paymentAssocToExpenseSelected = transactionRepository.findExpenseAssocToPaymentByIdExpenseToPaymentAndWorkspaceId(transactionDeleted.getIdExpenseToPay(), transactionDeleted.getPeriod().getWorkspace().getId());
					if(paymentAssocToExpenseSelected == null) {
						throw new CustomException("El pago asociado no pudo ser ubicado. Valide si existe por favor.");
					}
					LOG.info(messageLog + " - Se procede a ubicar el gasto TC que está asociado al pago enlazado al registro seleccionado");
					Transaction expensePaymentAssocToPaymentFounded = transactionRepository.findExpenseAssocToPaymentByIdExpenseToPaymentAndWorkspaceId(paymentAssocToExpenseSelected.getIdExpenseToPay(), paymentAssocToExpenseSelected.getPeriod().getWorkspace().getId());
					expensePaymentAssocToPaymentFounded.setAmountPayed(expensePaymentAssocToPaymentFounded.getAmountPayed() - paymentAssocToExpenseSelected.getAmount());
					expensePaymentAssocToPaymentFounded.setPendingPay(true);
					transactionRepository.save(expensePaymentAssocToPaymentFounded);
					LOG.info(messageLog + " - Se actualiza el monto pagado y estado de pago a pendiente del gasto TC");
					LOG.info(messageLog + " - -----------------------------------------------------------------------");
					LOG.info(messageLog + " - Se procede a realizar el reverso del proceso de pago para eliminarlo posteriormente");
					Account accuontTC = modifyAmountAccountFromTransaction(paymentAssocToExpenseSelected, "DELETE", true);
					if(accuontTC.getBalanceAvailable() < 0)
						throw new CustomException("La cuenta TC asociada al registro seleccionado no cuenta con fondos para realizar el reverso de la operación producto del proceso de eliminación.");

					transactionRepository.deleteById(paymentAssocToExpenseSelected.getId());
					LOG.info(messageLog + " - Se eliminó el pago asociado al registro seleccionado");
				}

				//Si es gasto TC :: buscar su pago asociado, buscar su gasto pago reversarlo con validación previa y elimnarlo después, elimnar el pago y elimanar el tc finalmente previo reversado validado.
				//Si es gasto con amountRecover > 0 y amountPayed > 0 y estado pendiente o pagado  :: buscar los pagos, reversarlos con previa validación y elimnarlos cada uno.
				if(transactionDeleted.getAmountPayed() > 0 ) {
					LOG.info(messageLog + " El registro es de tipo gasto pendiente o pagado con monto pagado > 0");
					LOG.info(messageLog + " - Se procede a ubicar los pagos que asociados a este registro");
					List<Transaction> paymentsAssocToExpenseToDelete = transactionRepository.findPaymentsAssocToExpenseDeleteByExpenseIdAndPeriodId(idTransaction, transactionDeleted.getPeriod().getWorkspace().getId());

					if(paymentsAssocToExpenseToDelete.size() == 0) {
						throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_NOTFOUND_PAYMENTS_ASSOC_TO_EXPENSE_TO_DELETE);
					}

					LOG.info(messageLog + " - Se valida si el registro seleccionado corresponde a un gasto TC, para ubicar los gastos asociados post pago TC y eliminarlos post reverso");
					if(transactionDeleted.getAccount().getTypeCard().getName().toUpperCase().equals("CREDIT")) {
						LOG.info(messageLog + " - El registro seleccionado corresponde a un gasto TC");
					}
					LOG.info(messageLog + " - Se recorre en busca de los pagos asociados al registro seleccionado");
					for ( Transaction paymentAssocToDelete : paymentsAssocToExpenseToDelete) {
						if(paymentAssocToDelete.getAccount().getBalanceAvailable() - paymentAssocToDelete.getAmount() < 0){
							throw new CustomException("Alguna de las cuentas asociada a los pagos no pudo realizar el descuento por falta de fondos, no es posible eliminar el registro seleccionado.");
						}

						if(transactionDeleted.getAccount().getTypeCard().getName().toUpperCase().equals("CREDIT")) {
							LOG.info(messageLog + " - Se procede a ubicar el gasto registrado post pago de TC, de este pago asociado al TC");
							//isExpenseTC = true;
							Transaction expenseAssocPostPaymentToPaymentID = transactionRepository.findTransactionAssocByIdTransactionAndWorkspaceId(paymentAssocToDelete.getId(), paymentAssocToDelete.getPeriod().getWorkspace().getId());
							if(expenseAssocPostPaymentToPaymentID == null) {
								throw new CustomException("El gasto asociado al pago correspondiente al gasto TC no pudo ser ubicado.");
							}

							Account accountModifued = modifyAmountAccountFromTransaction(expenseAssocPostPaymentToPaymentID, "DELETE", true);
							if(accountModifued.getBalanceAvailable() < 0)
								throw new CustomException("La cuenta del pago asociada al registro seleccionado no cuenta con fondos para realizar el reverso de la operación producto del proceso de eliminación.");

							transactionRepository.deleteById(expenseAssocPostPaymentToPaymentID.getId());
							LOG.info(messageLog + " - Se eliminó uno de los gastos post pago de TC asociado al registro seleccionado");
						}

						LOG.info(messageLog + " - Se efectúa el reverso en las cuentas del pago encontrado asociado al registro");
						modifyAmountAccountFromTransaction(paymentAssocToDelete, "DELETE", true);
						LOG.info(messageLog + " - Se eliminó también el pago asociado al registro seleccionado");
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

				if(expenseAssocToPayment.getTransactionType().equals(TransactionType.INCOME)) {
					LOG.info(messageLog + " - El pago a eliminar corresponde a un tipo INCOME, por tanto ha restado en vez de pagado su reverso será suma, se pasa a EXPENSE.");
					transactionDeleted.setTransactionType(TransactionType.EXPENSE);
				}

				expenseAssocToPayment.setAmountPayed(Utils.roundTwoDecimalWithBigDecimal(expenseAssocToPayment.getAmountPayed() - transactionDeleted.getAmount()));
				expenseAssocToPayment.setPendingPay(true);
				transactionRepository.save(expenseAssocToPayment);
				LOG.info(messageLog + " Se realizó el descuento del monto pagado al registro de gasto asociado u Income asociado, ya que el pago no existirá más. ");

				if(transactionDeleted.getAccount().getTypeCard().getName().toUpperCase().equals("CREDIT")) {
					LOG.info(messageLog + " - El pago corresponde a un tipo de cuenta TC");
					LOG.info(messageLog + " - Se obtiene el gasto postPago asociado de este pago TC");
					Transaction expenseAssocPostPaymentToPaymentID = transactionRepository.findTransactionAssocByIdTransactionAndWorkspaceId(transactionDeleted.getId(), transactionDeleted.getPeriod().getWorkspace().getId());
					if(expenseAssocPostPaymentToPaymentID == null) {
						throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_NOTFOUND_EXPENSE_ASSOC_TO_PAYMENT_TO_DELETE);
					}
					modifyAmountAccountFromTransaction(expenseAssocPostPaymentToPaymentID, "DELETE", true);
					LOG.info(messageLog + " - Se aplicó el reverso del gastoPostPago asociado de este pago TC");
					transactionRepository.deleteById(expenseAssocPostPaymentToPaymentID.getId());
					LOG.info(messageLog + " - Se eliminó el gasto post pago asociado al registro seleccionado");
				}
			}

			if(transactionDeleted.getTransactionType().equals(TransactionType.INCOME)) {
				if(transactionDeleted.getAccount().getBalanceAvailable() < transactionDeleted.getAmount()) {
					throw new CustomException("La cuenta no tiene fondos suficiente para procesar el descuento correspondiente producto del intento de eliminación de la transacción, aumente el saldo de la cuenta.");
				}

				if(transactionDeleted.getAmountPayed() > 0 ) {
					LOG.info(messageLog + " El registro es de tipo gasto pendiente o pagado con monto pagado > 0");
					LOG.info(messageLog + " - Se procede a ubicar los pagos que asociados a este registro y elminarlos");
					List<Transaction> paymentsAssocToExpenseToDelete = transactionRepository.findPaymentsAssocToExpenseDeleteByExpenseIdAndPeriodId(idTransaction, transactionDeleted.getPeriod().getWorkspace().getId());

					if (paymentsAssocToExpenseToDelete.size() == 0) {
						throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_NOTFOUND_PAYMENTS_ASSOC_TO_EXPENSE_TO_DELETE);
					}

					LOG.info(messageLog + " - Se recorre en busca de los pagos asociados al registro seleccionado");
					for (Transaction paymentAssocToDelete : paymentsAssocToExpenseToDelete) {
						paymentAssocToDelete.setTransactionType(TransactionType.EXPENSE); //Para que sume al hacer el reverso y no reste como el pago común.
						modifyAmountAccountFromTransaction(paymentAssocToDelete, "DELETE", true);
						LOG.info(messageLog + " - Se efectuó el reverso en la cuenta del pago encontrado asociado al registro");
						transactionRepository.deleteById(paymentAssocToDelete.getId());
						LOG.info(messageLog + " - Se eliminó el pago de asociado al registro seleccionado");
					}
					LOG.info(messageLog + " Los pagos asociados fueron procesados y eliminados correctamente. el monto fue descontado de sus cuentas respectivamente");
				}
			}

			//if(!isExpenseTC) {
				Account accountPostModify = modifyAmountAccountFromTransaction(transactionDeleted, "DELETE", true);
				if (accountPostModify.getBalanceAvailable() < 0) {
					throw new CustomException("La cuenta asociada al registro seleccionado no cuenta con fondos para realizar el reverso de la operación producto del proceso de eliminación.");
				}
			//}

			transactionRepository.deleteById(transactionDeleted.getId());

		} catch (CustomException e) {
			throw new CustomException(e.getMessage());
		} catch(Exception e) {
			LOG.error(e.getMessage());
			throw new CustomException(e.getMessage());
		} finally {
			LOG.info(messageLog + properties.RESPONSE_GENERIC_DELETE_SUCCESS_MESSAGE);
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

		try {
			LOG.info(messageLog + "Ubicamos la transacción original a editar");
			Transaction transactionFounded = transactionRepository.findById(idTransaction).orElse(null);

			LOG.info(messageLog + "Ubicamos la cuenta de la nueva transacción y obtenemos su real info y la seteamos a la transacción");
			Account actualAccount = accountRepository.findById(transactionRequest.getAccount().getId()).orElse(null);
			transactionRequest.setAccount(actualAccount);

			if(transactionFounded == null) {
				throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_EXPENSE_TO_ASSOC_DONT_EXIST);
			}

			LOG.info(messageLog + " La tipología de la transacción a actualizar es: " + transactionFounded.getTransactionType());
			LOG.info(messageLog + " La Transacción a modificar es: " + transactionFounded.getAccount().getBalanceAvailable());

			if(transactionRequest.getAccount().getId() == transactionFounded.getAccount().getId()) {
				bothAccountsEquals = true;
			}

			LOG.info(messageLog + "Se procede a realizar las devoluciones o descuentos previos para actualizar los nuevos parámetros al registro.");

			if(transactionFounded.getTransactionType().equals(TransactionType.EXPENSE)) {
				//Se aplica el reverse
				if(transactionRequest.getAccount().getId() == transactionFounded.getAccount().getId()) {
					transactionRequest.setAccount(modifyAmountAccountFromTransaction(transactionFounded, "UPDATE", true));
				} else {
					modifyAmountAccountFromTransaction(transactionFounded, "UPDATE", true);
				}
				/* ::::: SOBRE TC PAYMENT ::::::*/
				Transaction paymentAssocToExpenseSelected = null;

				if(transactionFounded.getIdExpenseToPay() != 0) {
					paymentAssocToExpenseSelected = transactionRepository.findExpenseAssocToPaymentByIdExpenseToPaymentAndWorkspaceId(transactionFounded.getIdExpenseToPay(), transactionFounded.getPeriod().getWorkspace().getId());
					if(paymentAssocToExpenseSelected == null) {
						throw new CustomException("El pago asociado no pudo ser ubicado. Valide si existe por favor.");
					}

					LOG.info(messageLog + "Se procede a ubicar al gasto TC asociado al pago seleccionado");
					Transaction expenseTCAssocPaymentByTransactionId = transactionRepository.findExpenseAssocToPaymentByIdExpenseToPaymentAndPeriodId(paymentAssocToExpenseSelected.getIdExpenseToPay(), paymentAssocToExpenseSelected.getPeriod().getId());

					if(expenseTCAssocPaymentByTransactionId == null){
						expenseTCAssocPaymentByTransactionId = transactionRepository.findExpenseAssocToPaymentByIdExpenseToPaymentAndWorkspaceId(paymentAssocToExpenseSelected.getIdExpenseToPay(), paymentAssocToExpenseSelected.getPeriod().getWorkspace().getId());
					}

					if(expenseTCAssocPaymentByTransactionId == null) {
						throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_NOTFOUND_EXPENSE_ASSOC_TO_PAYMENT_TO_DELETE);
					}

					LOG.info(messageLog + "Se procede a modificar los montos al gasto pagado y el estado del gasto TC asociado al pago seleccionado");

					double finalAmountAvailableAccountAssocToExpendFounded = paymentAssocToExpenseSelected.getAccount().getBalanceAvailable() - paymentAssocToExpenseSelected.getAmount() + transactionRequest.getAmount();
					if(finalAmountAvailableAccountAssocToExpendFounded < 0) {
						throw new CustomException("La cuenta asociada al pago[" + paymentAssocToExpenseSelected.getId() +"] producto del pago del gasto TC, no cuenta con saldo suficiente para actualizar el nuevo monto a modificar.");
					}

					double totalAmountModifiedToPayed = expenseTCAssocPaymentByTransactionId.getAmountPayed() - paymentAssocToExpenseSelected.getAmount() + transactionRequest.getAmount();
					if(totalAmountModifiedToPayed > expenseTCAssocPaymentByTransactionId.getAmountToRecover()) {
						throw new CustomException("El monto total pagado de la cuenta TC luego de modificar supera al monto a reponer, ingrese un monto menor");
					}

					expenseTCAssocPaymentByTransactionId.setAmountPayed(Utils.roundTwoDecimalWithBigDecimal(totalAmountModifiedToPayed));
					expenseTCAssocPaymentByTransactionId.setPendingPay(true);

					if(expenseTCAssocPaymentByTransactionId.getAmountToRecover() == expenseTCAssocPaymentByTransactionId.getAmountPayed()) {
						expenseTCAssocPaymentByTransactionId.setPendingPay(false);
					}

					transactionRepository.save(expenseTCAssocPaymentByTransactionId);
					LOG.info(messageLog + "Se realizó la actualización del monto pagado del gasto TC");

					LOG.info(messageLog + "-----------------------------------------------------------");

					LOG.info(messageLog + "Se procede a modificar los montos del pago asociado asociado al gasto TC y la cuenta TC");
					paymentAssocToExpenseSelected.setAmount(transactionRequest.getAmount());
					paymentAssocToExpenseSelected.getAccount().setBalanceAvailable(Utils.roundTwoDecimalWithBigDecimal(finalAmountAvailableAccountAssocToExpendFounded));
					transactionRepository.save(paymentAssocToExpenseSelected);
					LOG.info(messageLog + "Se realizó la actualización de montos del gasto asociado al pago por pago de TC y su respectiva cuenta.");
				}
				/* ::::: SOBRE TC PAYMENT ::::::*/

			}

			if(transactionFounded.getTransactionType().equals(TransactionType.INCOME)) {
				transactionRequest = reverseProcessingAmountToAccountAssocForIncomeOrPayment(transactionRequest, bothAccountsEquals, transactionFounded);
				//La validación de los pagos y monto a reponer se dan al momento de guardar.
			}

			if(transactionFounded.getTransactionType().equals(TransactionType.PAYMENT)) {

				LOG.info(messageLog + "Se procede a ubicar la transacción de tipo GASTO or INCOME  asociada a la transaction seleccionada [PAGO] ");
				Transaction transactionAssocToTransactionFounded = transactionRepository.findExpenseAssocToPaymentByIdExpenseToPaymentAndPeriodId(transactionFounded.getIdExpenseToPay(), transactionFounded.getPeriod().getId());
				LOG.info(messageLog + "Se procede a actualizar el monto pagado del gasto asociado al pago [montopagado] ");
				updatePaymentAmountToExpenseAssoc(messageLog, transactionFounded, transactionAssocToTransactionFounded, null);
				LOG.info(messageLog + "Monto  pagado de gasto o income realizado corectamente");

				LOG.info(messageLog + "Se procede a realizar las devoluciones o el reverso correspondiente del monto original a la cuenta");

				if(transactionAssocToTransactionFounded.getTransactionType().equals(TransactionType.INCOME)) {
					transactionFounded.setTransactionType(TransactionType.EXPENSE);
					if(bothAccountsEquals) {
						transactionRequest.setAccount(modifyAmountAccountFromTransaction(transactionFounded, "UPDATE", true));
					} else {
						modifyAmountAccountFromTransaction(transactionFounded, "UPDATE", true);
					}

				} else {
					transactionRequest = reverseProcessingAmountToAccountAssocForIncomeOrPayment(transactionRequest, bothAccountsEquals, transactionFounded);
				}

				/* ::::: SOBRE TC PAYMENT ::::::*/
				if(transactionAssocToTransactionFounded.getAccount().getTypeCard().getName().toUpperCase().equals("CREDIT")) {
					LOG.info(messageLog + "El gasto asociado se dió de una Tarjeta de Crédito[TC], por lo que generó un gasto adiconal de la cuenta con la que se pagó el TC");
					LOG.info(messageLog + "Se procede a obtener la transacción de tipo GASTO que realizó el pago en sí del gasto en TC.");
					Transaction expenseAssocToPaymentID = transactionRepository.findTransactionAssocByIdTransactionAndWorkspaceId(transactionFounded.getId(), transactionFounded.getPeriod().getWorkspace().getId());

					if(expenseAssocToPaymentID == null) {
						throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_NOTFOUND_EXPENSE_ASSOC_TO_PAYMENT_TO_DELETE);
					}

					double newAmountAvailableForExpenseAssocToPaymentById = expenseAssocToPaymentID.getAccount().getBalanceAvailable() + expenseAssocToPaymentID.getAmount() - transactionRequest.getAmount();
					if(newAmountAvailableForExpenseAssocToPaymentById < 0) {
						throw new CustomException("La cuenta asociada al gasto[" + expenseAssocToPaymentID.getId() +"] producto del pago del gasto TC, no cuenta con saldo suficiente para actualizar el nuevo monto a modificar.");
					}

					expenseAssocToPaymentID.setAmount(transactionRequest.getAmount());
					expenseAssocToPaymentID.getAccount().setBalanceAvailable(Utils.roundTwoDecimalWithBigDecimal(newAmountAvailableForExpenseAssocToPaymentById));
					transactionRepository.save(expenseAssocToPaymentID);
					LOG.info(messageLog + "Se realizó la actualización de montos del gasto asociado al pago por pago de TC y su respectiva cuenta.");
				}
				/* ::::: SOBRE TC PAYMENT ::::::*/
			}

			LOG.info(messageLog + "Devoluciones y descuentos realizados existosamente.");
			LOG.info(messageLog + "Se inicia con el registro y actualización de los nuevos valores de la transacción inicial seleccionada para sus edición.");
			Response responseSave = saveTransaction(transactionRequest, messageLog);
			if(!responseSave.getStatus().equals("success"))
				throw new CustomException(response.getMessage());

			response.setObject(responseSave.getObject());
			LOG.info(messageLog + properties.RESPONSE_GENERIC_UPDATE_SUCCESS_MESSAGE);
		} catch (CustomException e) {
			throw new CustomException(e.getMessage());
		} catch (Exception e) {
			throw new CustomException(e.getMessage());
	    }

		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_UPDATE_SUCCESS_MESSAGE);
		return response;
	}

	private void updatePaymentAmountToExpenseAssoc(String messageLog, Transaction transactionFounded, Transaction transactionAssocToTransactionFounded, Transaction transactionRequest) throws CustomException {

		try {
			if(transactionAssocToTransactionFounded == null){
				transactionAssocToTransactionFounded = transactionRepository.findExpenseAssocToPaymentByIdExpenseToPaymentAndWorkspaceId(transactionFounded.getIdExpenseToPay(), transactionFounded.getPeriod().getWorkspace().getId());
			}

			if(transactionAssocToTransactionFounded == null) {
				throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_NOTFOUND_EXPENSE_ASSOC_TO_PAYMENT_TO_DELETE);
			}

			double newAmounPayed = transactionAssocToTransactionFounded.getAmountPayed() - transactionFounded.getAmount();
			if(transactionRequest != null) {
				newAmounPayed = newAmounPayed + transactionRequest.getAmount();
				if(newAmounPayed > transactionAssocToTransactionFounded.getAmountToRecover()) {
					throw new CustomException("El nuevo monto pagado del Gasto TC, es mayor al monto total a reponer, ingrese un monto menor.");
				}
			}

			LOG.info(messageLog + "Se procede a modificar los montos al gasto pagado y el estado del gasto asociado al pago seleccionado.");

			if(newAmounPayed < transactionAssocToTransactionFounded.getAmountToRecover()) {
				transactionAssocToTransactionFounded.setPendingPay(true);
			}
			transactionAssocToTransactionFounded.setAmountPayed(Utils.roundTwoDecimalWithBigDecimal(newAmounPayed));

			transactionRepository.save(transactionAssocToTransactionFounded);
		} catch (Exception e) {
			throw new CustomException(e.getMessage());
		}
	}

	private Transaction reverseProcessingAmountToAccountAssocForIncomeOrPayment(Transaction transactionRequest, boolean bothAccountsEquals, Transaction transactionFounded) throws CustomException{

		try {
			//Devolución :: proceso reverso [payment ==  resta]
			if(bothAccountsEquals) {
				transactionRequest.setAccount(modifyAmountAccountFromTransaction(transactionFounded, "UPDATE", true));
			} else {
				if(transactionFounded.getAccount().getBalanceAvailable() < transactionFounded.getAmount()) {
					throw new CustomException("La cuenta no tiene fondos suficiente para procesar el nuevo monto, ingrese un nuevo monto de ingreso mayor.");
				}
				modifyAmountAccountFromTransaction(transactionFounded, "UPDATE", true);
			}
		} catch (Exception e) {
			throw new CustomException(e.getMessage());
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
	public Transaction updateVouchers(Transaction transactionUpdateVoucher, String messageLog) throws CustomException{

		Transaction transactionUpdated = null;

		try {
			LOG.info(messageLog + " Se procede a almacenar los vouchers y asignarlos a la transacción previamente almacenada");
			LOG.info("request: " + transactionUpdateVoucher.getVouchers());
			List<Vouchers> vouchersSaved = new ArrayList<>();
			for ( Vouchers voucherToSave : transactionUpdateVoucher.getVouchers()) {
				vouchersSaved.add(voucherRepo.save(voucherToSave));
			}
			transactionUpdateVoucher.setVouchers(vouchersSaved);

			transactionUpdated =transactionRepository.save(transactionUpdateVoucher);

		} catch (Exception e) {
			throw new CustomException(e.getMessage());
		}

		return transactionUpdated;
	}
}

package pe.com.erp.expensemanager.modules.account.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.number.money.Jsr354NumberFormatAnnotationFormatterFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.com.erp.expensemanager.exception.CustomException;
import pe.com.erp.expensemanager.modules.account.model.*;
import pe.com.erp.expensemanager.modules.account.repository.AccountRepository;
import pe.com.erp.expensemanager.modules.account.repository.TransferenceRepository;
import pe.com.erp.expensemanager.modules.account.services.interfaz.ITransferenceService;
import pe.com.erp.expensemanager.modules.transaction.model.Transaction;
import pe.com.erp.expensemanager.modules.transaction.repository.TransactionRepository;
import pe.com.erp.expensemanager.properties.PropertiesExtern;
import pe.com.erp.expensemanager.shared.model.Response;
import pe.com.erp.expensemanager.utils.Utils;

import javax.xml.bind.ValidationException;

@Service
public class TransferenceServiceImpl implements ITransferenceService {

	private static final Logger LOG = LoggerFactory.getLogger(TransferenceServiceImpl.class);
	
	@Autowired
	PropertiesExtern properties;
	@Autowired
	TransferenceRepository transferRepo;
	
	@Autowired
	AccountRepository accountRepo;
	@Autowired
	private TransactionRepository transactionRepository;


	@Override
	@Transactional(rollbackFor = {CustomException.class, ValidationException.class})
	public Response saveTransference(Transference transferenceRequest, String messageLog) {

		Response response = new Response();
		Account accountDestiny = new Account();
		Account accountOrigin = new Account();
		Transference transferenceToSave = new Transference();

		LOG.info(messageLog + "BEGIN TRANSFERENCE");

		accountDestiny = transferenceRequest.getAccountDestiny();
		accountOrigin = transferenceRequest.getAccountOrigin();

		if(accountOrigin.getId() == 0) {
			LOG.info(messageLog + "TYPE TRANSFERENCE IS EXTERN WITH ORIGIN NULL, ONLY UPDATE DESTINY BALANCE AND AVAILABLE");
			accountDestiny.setBalanceAvailable(accountDestiny.getBalanceAvailable() + transferenceRequest.getAmount());
			//accountDestiny.setBalance(accountDestiny.getBalance() + transferenceRequest.getAmount());
			accountRepo.save(accountDestiny);

			transferenceRequest.setTypeTransference(TypeTransference.EXTERN);
			transferenceRequest.setAccountOrigin(null);

		} else {
			accountOrigin = accountRepo.findById(transferenceRequest.getAccountOrigin().getId()).orElse(null);
			accountDestiny = accountRepo.findById(transferenceRequest.getAccountDestiny().getId()).orElse(null);
			transferenceRequest.setTypeTransference(TypeTransference.INTERN);

			if(accountOrigin == null || accountDestiny == null) {
				LOG.info(messageLog + properties.RESPONSE_CUSTOMIZED_TRANSFER_INFO_SOMEACCOUNT_DONT_FOUND);
				throw new CustomException(properties.RESPONSE_CUSTOMIZED_TRANSFER_INFO_SOMEACCOUNT_DONT_FOUND);
			}

			if(transferenceRequest.getAmount() > accountOrigin.getBalanceAvailable()) {
				LOG.info(messageLog + properties.RESPONSE_CUSTOMIZED_TRANSFER_INFO_AMOUNTTOTRANSFER_BETTERTHAN_AVAILABLEAMOUNT_ORIGINACCOUNT);
				throw new CustomException(properties.RESPONSE_CUSTOMIZED_TRANSFER_INFO_AMOUNTTOTRANSFER_BETTERTHAN_AVAILABLEAMOUNT_ORIGINACCOUNT);
			}

			accountOrigin.setBalanceAvailable(accountOrigin.getBalanceAvailable() - transferenceRequest.getAmount());
			accountDestiny.setBalanceAvailable(accountDestiny.getBalanceAvailable() + transferenceRequest.getAmount());

			if(accountOrigin.getAccountType().getTypeName().equals("PARENT") && accountDestiny.getAccountType().getTypeName().equals("PARENT")) {
				transferenceRequest.setTypeTransference(TypeTransference.EXTERN);
			}

			/*
			if(accountOrigin.getAccountType().getTypeName().equals(accountDestiny.getAccountType().getTypeName())) {
				if(accountOrigin.getAccountType().getTypeName().equals("PARENT")) {
					transferenceRequest.setTypeTransference(TypeTransference.EXTERN);
				}
				accountOrigin.setBalance(accountOrigin.getBalance() - transferenceRequest.getAmount());
				accountDestiny.setBalance(accountDestiny.getBalance() + transferenceRequest.getAmount());
			} else if(accountOrigin.getAccountType().getTypeName().equals("PARENT")){
				accountDestiny.setBalance(accountDestiny.getBalance() + transferenceRequest.getAmount());
			} else if(accountDestiny.getAccountType().getTypeName().equals("PARENT")){
				accountOrigin.setBalance(accountOrigin.getBalance() - transferenceRequest.getAmount());
			}
			*/
			accountRepo.save(accountDestiny);
			accountRepo.save(accountOrigin);
		}

		transferenceToSave = transferRepo.save(transferenceRequest);

		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_SAVE_SUCCESS_MESSAGE);
		response.setObject(transferenceToSave);

		return response;
	}

	@Override
	public List<Transference> listTransferencesByIdPeriod(Long idPeriod) {
		return transferRepo.listTransferencesByIdPeriod(idPeriod);
	}

	@Override
	public List<Transference> listTransferencesByIdAccountAndIdPeriod(Long idAccount, Long idPeriod) {
		return transferRepo.listTransferencesByIdAccountAndIdPeriod(idAccount, idPeriod);
	}

	@Override
	@Transactional(rollbackFor = {CustomException.class, ValidationException.class})
	public Response deleteTransferenceByIdTransfer(Long idTransference, String messageLog) {

		Response response = new Response();
		Account accountOrigin = new Account();
		Account accountDestiny = new Account();
		Transference transferenceToDelete = new Transference();

		transferenceToDelete = transferRepo.findById(idTransference).orElse(null);

		if (transferenceToDelete == null) {
			LOG.error(messageLog + " ::: TRANSFERENCE TO DELETE DON'T EXIST  ::: ");
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_TRANSFERENCE_DONT_EXIST);
		}

		reversarTransferencesToAccountPrevDeleteTransfer(messageLog, transferenceToDelete);

		transferRepo.deleteById(idTransference);

		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_DELETE_SUCCESS_MESSAGE);
		response.setObject(null);

		return response;
	}

	@Transactional(rollbackFor = {CustomException.class, ValidationException.class})
	private void reversarTransferencesToAccountPrevDeleteTransfer(String messageLog, Transference transferenceToDelete) {
		Account accountOrigin;
		Account accountDestiny;
		accountDestiny = transferenceToDelete.getAccountDestiny();
		accountOrigin = transferenceToDelete.getAccountOrigin();

		accountOrigin = accountRepo.findById(transferenceToDelete.getAccountOrigin().getId()).orElse(null);
		accountDestiny = accountRepo.findById(transferenceToDelete.getAccountDestiny().getId()).orElse(null);

		if(!transferenceToDelete.getTypeTransference().equals(TypeTransference.EXTERN) && accountOrigin == null) {
			LOG.info(messageLog + properties.RESPONSE_CUSTOMIZED_TRANSFER_INFO_SOMEACCOUNT_DONT_FOUND);
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_TRANSFER_INFO_SOMEACCOUNT_DONT_FOUND);
		}

		if(accountDestiny == null) {
			LOG.info(messageLog + properties.RESPONSE_CUSTOMIZED_TRANSFER_INFO_SOMEACCOUNT_DONT_FOUND);
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_TRANSFER_INFO_SOMEACCOUNT_DONT_FOUND);
		}

		//Valida que el saldo disponible actual de la cuenta reversar[destino] sea el suficiente para poder aplicarle el descuento, de lo contrario no dejará hacerlo.
		if((accountDestiny.getBalanceAvailable() - transferenceToDelete.getAmount()) < 0) {
			LOG.info(messageLog + properties.RESPONSE_CUSTOMIZED_TRANSFER_INFO_AVAILABLE_ACCOUNT_ITS_INSUFFICIENT_FOR_DISCOUNT_AMOUNT_TRANSFERENCE_DONT_POSSIBLE_REVERSAL);
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_TRANSFER_INFO_AVAILABLE_ACCOUNT_ITS_INSUFFICIENT_FOR_DISCOUNT_AMOUNT_TRANSFERENCE_DONT_POSSIBLE_REVERSAL);
		}

		if(transferenceToDelete.getTypeTransference().equals(TypeTransference.INTERN)) {
			accountOrigin.setBalanceAvailable(accountOrigin.getBalanceAvailable() + transferenceToDelete.getAmount());
			accountDestiny.setBalanceAvailable(accountDestiny.getBalanceAvailable() - transferenceToDelete.getAmount());
/*
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
*/
			accountRepo.save(accountOrigin);
			accountRepo.save(accountDestiny);
		} else {
			accountDestiny.setBalanceAvailable(accountDestiny.getBalanceAvailable() - transferenceToDelete.getAmount());
			accountDestiny.setBalanceAvailable(accountDestiny.getBalanceAvailable() - transferenceToDelete.getAmount());
			accountRepo.save(accountDestiny);
		}
/*
		//REVERSAL CASE: CREDIT CARD EXPENSE
		if(transferenceToDelete.getIdExpenseAssoc() != null) {
			Transaction expenseByCreditCard = transactionRepository.findById(transferenceToDelete.getIdExpenseAssoc()).orElse(null);
			if (expenseByCreditCard == null) {
				LOG.error(messageLog + " ::: EXPENE CREDIT CARD ASSOC DONT EXIS'T  ::: ");
				throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_EXPENSE_ASSOC_CREDIT_CARD_DONT_EXIST);
			}

			expenseByCreditCard.setAmountPayed(expenseByCreditCard.getAmountPayed() - transferenceToDelete.getAmount());
			expenseByCreditCard.setPendingPay(true);
			transactionRepository.save(expenseByCreditCard);
		}

 */
	}

	@Override
	@Transactional(rollbackFor = {CustomException.class, ValidationException.class})
	public Response updateTransferenceByIdTransference(Transference transaferenceUpdateRequest, Long idTransference, String messageLog) {

		Response response = new Response();
		Account accountOrigin = new Account();
		Account accountDestiny = new Account();
		Transference transferenceToUpdateFounded = new Transference();

		transferenceToUpdateFounded = transferRepo.findById(idTransference).orElse(null);

		if (transferenceToUpdateFounded == null) {
			LOG.error(messageLog + " ::: TRANSFERENCE TO UPDATE DON'T EXIST  ::: ");
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_TRANSFERENCE_DONT_EXIST);
		}

		reversarTransferencesToAccountPrevDeleteTransfer(messageLog, transferenceToUpdateFounded);

		saveTransference(transaferenceUpdateRequest, messageLog);

		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_UPDATE_SUCCESS_MESSAGE);
		response.setObject(null);


		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_DELETE_SUCCESS_MESSAGE);
		response.setObject(null);

		return response;
	}

}

package pe.com.erp.expensemanager.modules.account.services.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.com.erp.expensemanager.exception.CustomException;
import pe.com.erp.expensemanager.modules.account.model.Account;
import pe.com.erp.expensemanager.modules.account.model.Transference;
import pe.com.erp.expensemanager.modules.account.repository.AccountRepository;
import pe.com.erp.expensemanager.modules.account.repository.TransferenceRepository;
import pe.com.erp.expensemanager.modules.account.services.interfaz.ITransferenceService;
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
	
	@Override
	@Transactional(rollbackFor = {CustomException.class, ValidationException.class})
	public Response save(Transference transferenceRequest, String messageLog) {
		
		Response response = new Response();
		Account accountDestiny = new Account();
		Account accountOrigin = new Account();
		Transference transferenceToSave = new Transference();

		LOG.info(messageLog + "BEGIN TRANSFERENCE");

		accountDestiny = transferenceRequest.getAccountDestiny();
		accountOrigin = transferenceRequest.getAccountOrigin();

		if(accountOrigin.getId() == 0) {
			LOG.info(messageLog + "TYPE TRANSFERENCE IS EXTERN WITH ORIGIN NULL, ONLY UPDATE DESTINY BALANCE AND AVAILABLE");
			accountDestiny = updatingAmountAccountByAccountType(accountDestiny, transferenceRequest.getAmount(), "destiny");
			accountRepo.save(accountDestiny);
	
			transferenceRequest.setTypeEntryExtern(true);
			transferenceRequest.setAccountOrigin(null);

		} else {
			accountOrigin = accountRepo.findById(transferenceRequest.getAccountOrigin().getId()).orElse(null);
			accountDestiny = accountRepo.findById(transferenceRequest.getAccountDestiny().getId()).orElse(null);

			if(accountOrigin == null || accountDestiny == null) {
				LOG.info(messageLog + properties.RESPONSE_CUSTOMIZED_TRANSFER_INFO_SOMEACCOUNT_DONT_FOUND);
				throw new CustomException(properties.RESPONSE_CUSTOMIZED_TRANSFER_INFO_SOMEACCOUNT_DONT_FOUND);
			}

			if(transferenceRequest.getAmount() > accountOrigin.getBalanceAvailable()) {
				LOG.info(messageLog + properties.RESPONSE_CUSTOMIZED_TRANSFER_INFO_AMOUNTTOTRANSFER_BETTERTHAN_AVAILABLEAMOUNT_ORIGINACCOUNT);
				throw new CustomException(properties.RESPONSE_CUSTOMIZED_TRANSFER_INFO_AMOUNTTOTRANSFER_BETTERTHAN_AVAILABLEAMOUNT_ORIGINACCOUNT);
			}

			LOG.info(messageLog + "UPDATING AMOUNT ACCORDING TYPE ACCOUNT");

			if(accountOrigin.getAccountType().getTypeName().equals(accountDestiny.getAccountType().getTypeName())) {

				if(accountOrigin.getAccountType().getTypeName().equals("CHILD")) {
					transferenceRequest.setTypeEntryExtern(false);
					LOG.info(messageLog + "TYPE TRANSFERENCE IS INTERN CHILD TO CHILD");
				} else {
					transferenceRequest.setTypeEntryExtern(true);
					LOG.info(messageLog + "TYPE TRANSFERENCE IS EXTERN PARENT TO PARENT");
				}
				accountDestiny = updatingAmountAccountByAccountType(accountDestiny, transferenceRequest.getAmount(), "destiny");
				accountOrigin = updatingAmountAccountByAccountType(accountOrigin, transferenceRequest.getAmount(), "origin");
			} else if(accountOrigin.getAccountType().getId() == 1) {
				LOG.info(messageLog + "TYPE TRANSFERENCE IS INTERN PARENT TO CHILD");
				accountOrigin.setBalanceAvailable(Utils.roundTwoDecimals(accountOrigin.getBalanceAvailable() - transferenceRequest.getAmount()));
				accountDestiny = updatingAmountAccountByAccountType(accountDestiny, transferenceRequest.getAmount(), "destiny");
			} else if(accountDestiny.getAccountType().getId() == 1) {
				LOG.info(messageLog + "TYPE TRANSFERENCE IS INTERN CHILD TO PARENT");
				accountDestiny.setBalanceAvailable(Utils.roundTwoDecimals(accountDestiny.getBalanceAvailable() + transferenceRequest.getAmount()));
				accountOrigin = updatingAmountAccountByAccountType(accountOrigin, transferenceRequest.getAmount(), "origin");
			}
			
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

	private Account updatingAmountAccountByAccountType(Account accountToUpdatingAmount, double amountTransfer, String accountTypeOriginOrDestiny) {
		if(accountTypeOriginOrDestiny.equals("origin")) {
			accountToUpdatingAmount.setBalance(Utils.roundTwoDecimals(accountToUpdatingAmount.getBalance() - amountTransfer));
			accountToUpdatingAmount.setBalanceAvailable(Utils.roundTwoDecimals(accountToUpdatingAmount.getBalanceAvailable() - amountTransfer));
		} else {
			accountToUpdatingAmount.setBalance(Utils.roundTwoDecimals(accountToUpdatingAmount.getBalance() + amountTransfer));
			accountToUpdatingAmount.setBalanceAvailable(Utils.roundTwoDecimals(accountToUpdatingAmount.getBalanceAvailable() + amountTransfer));
		}

		return accountToUpdatingAmount;
	}

	@Override
	public List<Transference> listTransferencesByIdPeriod(Long idPeriod) {
		return transferRepo.listTransferencesByIdPeriod(idPeriod);
	}

	@Override
	public Response deleteTransferenceById(Long id, String messageLog) {

		Response response = new Response();
		Account accountOrigin = new Account();
		Account accountDestiny = new Account();
		Transference transferenceToDelete = new Transference();

		LOG.error(messageLog + " ::: TRANSFERENCE DELETE BEGIN  ::: ");

		transferenceToDelete = transferRepo.findById(id).orElse(null);

		if (transferenceToDelete == null) {
			LOG.error(messageLog + " ::: TRANSFERENCE TO DELETE DON'T EXIST  ::: ");
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_TRANSFERENCE_DONT_EXIST);
		}
		accountOrigin = accountRepo.findById(transferenceToDelete.getAccountOrigin().getId()).orElse(null);
		accountDestiny = accountRepo.findById(transferenceToDelete.getAccountDestiny().getId()).orElse(null);

		if (accountOrigin == null) {
			LOG.error(messageLog + " ::: ACCOUNT ORIGIN DON'T EXIST  ::: ");
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_ORIGIN_DONT_EXIST);
		}
		if (accountDestiny == null) {
			LOG.error(messageLog + " ::: ACCOUNT DESTINY DON'T EXIST  ::: ");
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_DESTINY_DONT_EXIST);
		}

		LOG.info(messageLog + " ::: UPDATING AVAILABLE AMOUNT FROM ACCOUNT ORIGIN  AND DESTINY  ::: ");
		accountOrigin.setBalanceAvailable(accountOrigin.getBalanceAvailable() + transferenceToDelete.getAmount());
		accountDestiny.setBalanceAvailable(accountDestiny.getBalanceAvailable() - transferenceToDelete.getAmount());

		accountRepo.save(accountOrigin);
		accountRepo.save(accountDestiny);

		transferRepo.deleteById(id);

		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_DELETE_SUCCESS_MESSAGE);
		response.setObject(null);

		return response;
	}

}

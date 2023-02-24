package pe.com.erp.expensemanager.modules.account.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.com.erp.expensemanager.exception.CustomException;
import pe.com.erp.expensemanager.modules.account.model.Account;
import pe.com.erp.expensemanager.modules.account.model.Transference;
import pe.com.erp.expensemanager.modules.account.model.TypeStatusAccountOPC;
import pe.com.erp.expensemanager.modules.account.repository.AccountExpenseRepository;
import pe.com.erp.expensemanager.modules.account.repository.AccountRepository;
import pe.com.erp.expensemanager.modules.account.repository.AccountTransferencesRepository;
import pe.com.erp.expensemanager.modules.account.repository.TransferenceRepository;
import pe.com.erp.expensemanager.modules.account.services.interfaz.IAccountService;
import pe.com.erp.expensemanager.modules.categories.model.Category;
import pe.com.erp.expensemanager.modules.categories.repository.CategoryRepository;
import pe.com.erp.expensemanager.modules.partners.repository.PartnerRepository;
import pe.com.erp.expensemanager.modules.transaction.model.Transaction;
import pe.com.erp.expensemanager.properties.PropertiesExtern;
import pe.com.erp.expensemanager.shared.model.Response;
import pe.com.erp.expensemanager.utils.Utils;

import javax.xml.bind.ValidationException;

@Service
public class AccountServiceImpl implements IAccountService {

	private static final Logger LOG = LoggerFactory.getLogger(AccountServiceImpl.class);

	@Autowired
	PropertiesExtern properties;
	@Autowired()
	AccountRepository accountRepo;
	@Autowired()
	AccountExpenseRepository accountExpenseRepo;
	@Autowired
	AccountTransferencesRepository accountTransferencesRepo;
	@Autowired
	CategoryRepository categoryRepo;

	@Autowired
	TransferenceRepository transferRepo;

	@Autowired
	private PartnerRepository partnerRepository;

	@Override
	@Transactional(rollbackFor = {CustomException.class, ValidationException.class})
	public Response save(Account accountRequest, String messageLog) {

		LOG.info(messageLog + " ::: BEGIN SAVE ACCOUNT ::: ");

		Response response = new Response();
		Account accountToSave;
		Account accountParent;
		Account accountRepeat;

		// ============ BEGIN : VALIDATION NAME REPEATED =============
		LOG.info(messageLog + " ::: ACCOUNT TYPE : " + accountRequest.getAccountType().getTypeName() + " ::: ");
		LOG.info(messageLog + " ::: ACCOUNT VALIDATION NAME REPEATED ::: ");
		validateNameRepeated(accountRequest, messageLog);

		// ============ END : VALIDATION NAME REPEATED =============

		accountRequest.setStatusAccount(TypeStatusAccountOPC.INITIAL);
		accountRequest.setAccountNumber(String.valueOf(getGeneratorNumber()));
		accountRequest.setBalanceAvailable(Utils.roundTwoDecimals(accountRequest.getBalance()));
		accountRequest.setBalanceOnlyInitial(Utils.roundTwoDecimals(accountRequest.getBalance()));
		accountRequest.setEnabled(true);

		// ============ BEGIN : Case Account CHILD =============

		if(accountRequest.getAccountType().getTypeName().equals("CHILD")) {

			accountParent = accountRepo.findById(accountRequest.getAccountParentId()).orElse(null);
			if(accountParent == null) {
				LOG.info(messageLog + " ::: ACCOUNT PARENT DON'T EXIST ::: ");
				throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_PARENT_DONT_EXIST);
			}

			//Validate if amount child > available amount of parent account
			if (accountRequest.getBalance() > accountParent.getBalanceAvailable()) {
				LOG.info(messageLog + " ::: ACCOUNT BALANCE IS BETTER THAN AMOUNT PARENT AVAILABLE ::: ");
				throw new CustomException(properties.RESPONSE_CUSTOMIZED_ACCOUNT_BALANCECHILD_MAYOR_BALANCEDISPONIBLEPARENT.replace("{0}", accountParent.getBalanceAvailable().toString()));
			}

			//Update available amount of parent account
			accountParent.setBalanceAvailable(Utils.roundTwoDecimals(accountParent.getBalanceAvailable() - accountRequest.getBalance()));
			accountRepo.save(accountParent);

			accountRequest.setAccountParentId(accountParent.getId());
			LOG.info(messageLog + " ::: ACCOUNT PARENT UPDATED SUCCESSFULLY ::: ");
		}

		// ============ END : Case Account CHILD =============

		accountToSave = accountRepo.save(accountRequest);

		if(accountRequest.getAccountType().getTypeName().equals("PARENT")) {
			LOG.info(messageLog + " ::: ACCOUNT UPDATING AMOUNT PARENT WITH ID ITSELF ::: ");
			accountToSave.setAccountParentId(accountToSave.getId());
			accountToSave = accountRepo.save(accountToSave);
		}

		LOG.info(messageLog + " ::: ACCOUNT SAVED SUCCESSFULLY ::: ");

		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_SAVE_SUCCESS_MESSAGE);
		response.setObject(accountToSave);
		return response;
	}

	private void validateNameRepeated(Account accountRequest, String messageLog) throws CustomException {
		//case parent : Don't repeat in self period
		List<Account> accountRepeat = new ArrayList<>();
		if(accountRequest.getAccountType().getTypeName().equals("PARENT")) {
			LOG.info(messageLog + " ::: ACCOUNT PARENT ::: ");
			accountRepeat = accountRepo.findParentAccountByPeriodIdAndFinancialEntity(accountRequest.getAccountName(),
					accountRequest.getPeriod().getId(), accountRequest.getFinancialEntity().getName());
		}

		//case child : Don´t repeat in self period and self parent account
		if(accountRequest.getAccountType().getTypeName().equals("CHILD")) {
			LOG.info(messageLog + " ::: ACCOUNT CHILD ::: ");
			accountRepeat = accountRepo.findAccountByPeriodIdAndAccountParentId(accountRequest.getAccountName(),
					accountRequest.getPeriod().getId(), accountRequest.getAccountParentId());
		}

		if(accountRepeat.size() > 0) {
			if(accountRequest.getId() == 0) {
				responseMessageDuplicated(accountRequest, messageLog, accountRepeat);
			} else if(accountRequest.getId() != accountRepeat.get(0).getId()){
				responseMessageDuplicated(accountRequest, messageLog, accountRepeat);
			}
		}
	}

	private void responseMessageDuplicated(Account accountRequest, String messageLog, List<Account> accountRepeat) throws CustomException{
		LOG.info(messageLog + " ::: ACCOUNT NAME DUPLICATED ::: ");
		LOG.info(messageLog + accountRepeat.get(0).getAccountName());
		if(accountRequest.getAccountType().getTypeName().equals("PARENT")) {
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_NAME_REPEAT.replace("{0}", "en su lista de cuentas principales de tipo " + accountRepeat.get(0).getFinancialEntity().getName()));
		} else {
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_NAME_REPEAT.replace("{0}", "en su lista de presupuestos para la cuenta origen"));
		}
	}

	public int getGeneratorNumber() {
		return (int) (Math.random() * (999999999 - 100000000 + 1) + 100000000);
	}

	@Override
	@Transactional(rollbackFor = {CustomException.class, ValidationException.class})
	public Response update( Account accountRequestToUpdate, Long idAccountActualRequest, String messageLog) throws CustomException {
		Response response = new Response();
		Account accountParent;
		Account accountActual;
		Account accountUpdated;

		LOG.info(messageLog + " ::: BEGIN UPDATE ACCOUNT  ::: ");
		LOG.info(messageLog + " ::: ACCOUNT TYPE : " + accountRequestToUpdate.getAccountType().getTypeName() + " ::: ");

		//Get account actual by ID
		accountActual = accountRepo.findById(idAccountActualRequest).orElse(null);

		if(accountActual == null) {
			LOG.error(messageLog + " ::: ACCOUNT ACTUAL DON'T EXIST  ::: ");
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_DONT_EXIST);
		}

		validateNameRepeated(accountRequestToUpdate, messageLog);

		//Update Name account
		accountActual.setAccountName(accountRequestToUpdate.getAccountName());
		//accountUpdated = accountRepo.save(accountActual);
		LOG.info(messageLog + " ::: ACCOUNT NAME UPDATED SUCCESSFULLY  ::: ");

		if(!accountRequestToUpdate.getStatusAccount().equals(TypeStatusAccountOPC.INITIAL)) {
			LOG.error(messageLog + " ::: ACCOUNT DON´T POSSIBLE UPDATE - ONLY INITIAL STATUS  ::: ");
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_STATUS_INITIAL_DONT_POSIBLE_UPDATED);
		}

		//Get actual parent account
		accountParent = accountRepo.findById(accountRequestToUpdate.getAccountParentId()).orElse(null);

		if(accountParent == null) {
			LOG.error(messageLog + " ::: ACCOUNT PARENT DON´T EXIST  ::: ");
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_PARENT_DONT_EXIST);
		}

		double differentAmount = Math.abs(accountActual.getBalanceAvailable() - accountRequestToUpdate.getBalanceAvailable());

		LOG.info(messageLog + " ::: ACTUAL AMOUNT BALANCE = " + accountActual.getBalanceAvailable());
		LOG.info(messageLog + " ::: REQUEST AMOUNT AVAILABLE = " + accountRequestToUpdate.getBalanceAvailable());
		LOG.info(messageLog + " ::: DIFFERENT AMOUNT = " + differentAmount);

		if (accountRequestToUpdate.getBalanceAvailable() > accountActual.getBalanceAvailable()) {
			// subtract money to parent available
			accountActual.setBalance(accountActual.getBalance() + differentAmount);
		} else {
			accountActual.setBalance(accountActual.getBalance() - differentAmount);
		}

		if(accountRequestToUpdate.getAccountType().getTypeName().equals("CHILD")) {
			if (accountRequestToUpdate.getBalanceAvailable() > accountActual.getBalanceAvailable()) {
				if(differentAmount > accountParent.getBalanceAvailable()) {
					LOG.error(messageLog + " ::: ACCOUNT AMOUNT GRATHER THAN AMOUNT MAX ALLOWED ::: ");
					throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_AMOUNT_BETTER_THAN_AMOUNT_ALLOWED.replace("{0}", String.valueOf(accountParent.getBalanceAvailable())));
				}
				accountParent.setBalanceAvailable(accountParent.getBalanceAvailable() - differentAmount);
			} else {
				accountParent.setBalanceAvailable(accountParent.getBalanceAvailable() + differentAmount);
			}
			accountRepo.save(accountParent);
		}

		if(accountRequestToUpdate.getAccountType().getTypeName().equals("PARENT")) {
			List<Account> accountChildsByParent = accountRepo.findAccountsByParentIdAndTypeChild(accountParent.getId(), "CHILD" );
			LOG.error(messageLog + " ::: LIST ACCOUNT CHILDS ::: " + accountChildsByParent.size());
			if(accountRequestToUpdate.getTypeCard().getId() != accountActual.getTypeCard().getId()) {
				for ( Account accountChild : accountChildsByParent) {
					accountChild.setTypeCard(accountRequestToUpdate.getTypeCard());
					accountRepo.save(accountChild);
				}
			}

			if(accountRequestToUpdate.getFinancialEntity().getId() != accountActual.getFinancialEntity().getId()) {
				for ( Account accountChild : accountChildsByParent) {
					accountChild.setFinancialEntity(accountRequestToUpdate.getFinancialEntity());
					accountRepo.save(accountChild);
				}
			}
		}

		//Update amounts actual account : PARENT OR CHILD
		accountActual.setBalanceAvailable(accountRequestToUpdate.getBalanceAvailable());
		accountActual.setTypeCard(accountRequestToUpdate.getTypeCard());
		accountActual.setFinancialEntity(accountRequestToUpdate.getFinancialEntity());
		//Update categories assoc
		accountActual.setCategories(null);
		accountActual.setCategories(accountRequestToUpdate.getCategories());

		accountUpdated = accountRepo.save(accountActual);
		LOG.info(messageLog + " ::: ACCOUNT ACTUAL UPDATED SUCCESSFULLY ::: ");

		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_UPDATE_SUCCESS_MESSAGE);
		response.setObject(accountUpdated);

		return response;
	}
	/*@Override
	@Transactional(rollbackFor = {CustomException.class, ValidationException.class})
	public Response update( Account accountRequestToUpdate, Long idAccountActualRequest, String messageLog) throws CustomException {
		Response response = new Response();
		Account accountParent;
		Account accountActual;
		Account accountUpdated;
		double usedAmount;

		LOG.info(messageLog + " ::: BEGIN UPDATE ACCOUNT  ::: ");
		LOG.info(messageLog + " ::: ACCOUNT TYPE : " + accountRequestToUpdate.getAccountType().getTypeName() + " ::: ");

		//Get account actual by ID
		accountActual = accountRepo.findById(idAccountActualRequest).orElse(null);

		if(accountActual == null) {
			LOG.error(messageLog + " ::: ACCOUNT ACTUAL DON'T EXIST  ::: ");
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_DONT_EXIST);
		}

		validateNameRepeated(accountRequestToUpdate, messageLog);

		//Update Name account
		accountActual.setAccountName(accountRequestToUpdate.getAccountName());
		accountUpdated = accountRepo.save(accountActual);
		LOG.info(messageLog + " ::: ACCOUNT NAME UPDATED SUCCESSFULLY  ::: ");

		if(!accountRequestToUpdate.getStatusAccount().equals(TypeStatusAccountOPC.INITIAL)) {
			LOG.error(messageLog + " ::: ACCOUNT DON´T POSSIBLE UPDATE - ONLY INITIAL STATUS  ::: ");
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_STATUS_INITIAL_DONT_POSIBLE_UPDATED);
		}

		//Get actual parent account
		accountParent = accountRepo.findById(accountRequestToUpdate.getAccountParentId()).orElse(null);

		if(accountParent == null) {
			LOG.error(messageLog + " ::: ACCOUNT PARENT DON´T EXIST  ::: ");
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_PARENT_DONT_EXIST);
		}

		//Get used amount
		usedAmount = accountActual.getBalance() - accountActual.getBalanceAvailable();
		LOG.info(messageLog + " ::: ACTUAL AMOUNT BALANCE = " + accountActual.getBalance());
		LOG.info(messageLog + " ::: ACTUAL AMOUNT AVAILABLE = " + accountActual.getBalanceAvailable());
		LOG.info(messageLog + " ::: USED AMOUNT = " + usedAmount);
		//Validate new amount >= used amount
		if(accountRequestToUpdate.getBalance() < usedAmount) {
			LOG.error(messageLog + " ::: ACCOUNT AMOUNT LOWER THAN AMOUNT ACTUAL USED ::: ");
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_AMOUNT_LOWER_THAN_AMOUNTUSED.replace("{0}", String.valueOf(usedAmount)));
		}

		//Only Child case

		if(accountRequestToUpdate.getAccountType().getTypeName().equals("CHILD")) {

			double amountMaxAllowed = accountParent.getBalanceAvailable() + accountActual.getBalanceAvailable();
			//Validate new amount >= amount allowed
			if (accountRequestToUpdate.getBalance() > amountMaxAllowed) {
				LOG.error(messageLog + " ::: ACCOUNT AMOUNT GRATHER THAN AMOUNT MAX ALLOWED ::: ");
				throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_AMOUNT_BETTER_THAN_AMOUNT_ALLOWED.replace("{0}", String.valueOf(amountMaxAllowed)));
			}

			//ITS SUBTRACT BALANCE TO REQUEST BECAUSE REPRESENT THE AMOUNT AVAILABLE FROM FRONT.
			double differentAmount = Math.abs(accountActual.getBalanceAvailable() - accountRequestToUpdate.getBalance());
			LOG.info(messageLog + " ::: ACTUAL AMOUNT BALANCE = " + accountActual.getBalanceAvailable());
			LOG.info(messageLog + " ::: ACTUAL AMOUNT AVAILABLE = " + accountActual.getBalanceAvailable());
			LOG.info(messageLog + " ::: DIFFERENT AMOUNT = " + differentAmount);
			if (accountRequestToUpdate.getBalance() > accountActual.getBalance()) {
				// subtract money to parent available
				accountParent.setBalanceAvailable(accountParent.getBalanceAvailable() - differentAmount);
			} else {
				// return money to parent available
				accountParent.setBalanceAvailable(accountParent.getBalanceAvailable() + differentAmount);
			}

			accountRepo.save(accountParent);
			LOG.info(messageLog + " ::: ACCOUNT AMOUNT AVAILABLE PARENT UPDATED SUCCESSFULLY ::: ");
		}

		//Update amounts actual account : PARENT OR CHILD
		accountActual.setBalance(accountRequestToUpdate.getBalance());
		accountActual.setBalanceOnlyInitial(accountRequestToUpdate.getBalance());
		accountActual.setBalanceAvailable(accountRequestToUpdate.getBalance() - usedAmount);

		//Update categories assoc
		accountActual.setCategories(null);
		accountActual.setCategories(accountRequestToUpdate.getCategories());

		accountUpdated = accountRepo.save(accountActual);
		LOG.info(messageLog + " ::: ACCOUNT ACTUAL UPDATED SUCCESSFULLY ::: ");

		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_GENERIC_UPDATE_SUCCESS_MESSAGE);
		response.setObject(accountUpdated);

		return response;
	}*/

	@Override
	@Transactional(rollbackFor = {CustomException.class, ValidationException.class})
	public Response deleteAccountById(Long idAccount, String messageLog) {

		Response response = new Response();
		Account accountToDeleteFound = null;
		String messageException = "";

		LOG.info(messageLog + " ::: BEGIN DELETE ACCOUNT  ::: ");

		accountToDeleteFound = accountRepo.findById(idAccount).orElse(null);
		if(accountToDeleteFound == null) {
			LOG.error(messageLog + " ::: ACCOUNT TO DELETE DON'T EXIST  ::: ");
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_DONT_EXIST);
		}
		LOG.info(messageLog + " ::: ACCOUNT FOUNDED  ::: " + accountToDeleteFound);
		LOG.info(messageLog + " ::: VALIDATING IF EXIST´S EXPENSE OR TRANSFERENCE FOR THIS ACCOUNT SPECIFICALLY ::: ");
		if(accountToDeleteFound.getAccountType().getTypeName().equals("CHILD")) {

			Account accountParent = accountRepo.findById(accountToDeleteFound.getAccountParentId()).orElse(null);

			if(accountParent == null) {
				LOG.error(messageLog + " ::: PARENT ACCOUNT DON'T EXIST  ::: ");
				throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_PARENT_DONT_EXIST);
			}

			messageException = properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_EXPENSES_EXIST_BY_ACCOUNT_SELECTED_DELETE_ITS_IMPOSSIBLE;
			validExistsExpensesByAccountId(accountToDeleteFound, messageLog, messageException);

			messageException = properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_TRANSFERENCE_EXIST_BY_ACCOUNT_SELECTED_DELETE_ITS_IMPOSSIBLE;
			validExistsTransferencesByAccountId(accountToDeleteFound, messageLog, messageException);

			LOG.info(messageLog + " ::: VALIDATING OK, PROCEED TO UPDATING AVAILABLE AMOUNT TO ACCOUNT PARENT ::: ");
			accountParent.setBalanceAvailable(accountParent.getBalanceAvailable() + accountToDeleteFound.getBalanceAvailable());
			accountRepo.save(accountParent);

			LOG.info(messageLog + " ::: VALIDATING OK, PROCEED TO DELETE ::: ");
			accountToDeleteFound.setCategories(null);
			accountRepo.deleteById(accountToDeleteFound.getId());
		}

		if(accountToDeleteFound.getAccountType().getTypeName().equals("PARENT") ) {

			List<Account> accountsBudgetByParent = accountRepo.findAccountsByParentId(accountToDeleteFound.getAccountParentId());
			for (Account accountChild : accountsBudgetByParent) {
				accountChild.setCategories(null);
				messageException = properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_EXPENSES_EXIST_FOR_BUDGT_SELECTED_DELETE_ITS_IMPOSSIBLE;
				validExistsExpensesByAccountId(accountChild, messageLog, messageException);
			}

			for (Account accountChild : accountsBudgetByParent) {
				accountChild.setCategories(null);
				messageException = properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_TRANSFERENCES_EXIST_FOR_BUDGT_SELECTED_DELETE_ITS_IMPOSSIBLE;
				validExistsTransferencesByAccountId(accountChild, messageLog, messageException);
			}

			LOG.info(messageLog + " ::: VALIDATING OK, PROCEED TO DELETE ::: ");
			accountRepo.deleteAllByAccountParentId(accountToDeleteFound.getAccountParentId());
		}

		response.setTitle(properties.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(properties.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(properties.RESPONSE_CUSTOMIZED_ACCOUNT_SUCCESS_DELETE);
		response.setObject(accountToDeleteFound.getPeriod());

		return response;
	}

	private void validExistsTransferencesByAccountId(Account accountChild, String messageLog, String meessageException) throws CustomException {
		List<Transference> transfersFounded = accountTransferencesRepo.findTransferencesByAccountIdAndPeriodId(accountChild.getId(), accountChild.getPeriod().getId());
		if(transfersFounded.size() == 0) return;

		for (Transference transferenceToDelete : transfersFounded) {
			transferRepo.deleteById(transferenceToDelete.getId());
		}
	}

	private void validExistsExpensesByAccountId(Account accountChild, String messageLog, String meessageException) throws CustomException {
		List<Transaction> expenseRowsForAccountSelected = accountExpenseRepo.findTransactionByAccountId(accountChild.getId());
		if(expenseRowsForAccountSelected.size() > 0) {
			LOG.error(messageLog + " ::: EXIST´S EXPENSES FOR ALL ACCOUNT´S BELONG TO THE SELECTED BUDGET, DELETE IT´S IMPOSSIBLE ::: ");
			throw new CustomException(meessageException);
		}
	}

	@Override
	public List<Account> listAccountByIdPeriod(Long idPeriod) {
		return accountRepo.listAccountByIdPeriod(idPeriod);
	}

	@Override
	public Account findById(Long id) {
		return accountRepo.findById(id).orElse(null);
	}

	@Override
	public List<Account> listAccountByIdAccountType(Long idAccountType) {
		return accountRepo.listAllAccountByIdTypeAccount(idAccountType);
	}

	@Override
	public List<Category> findCategoriesNotAssocToAccount(Long idAccountParent, String messageLog) {

		Account accountParent;
		List<Category> allCategoriesAssocToRemove = new ArrayList<>();
		List<Category> categoriesFull;
		List<Category> categoriesDontAssocResponse;
		List<Account> accountChildList;

		accountParent = accountRepo.findById(idAccountParent).orElse(null);

		if(accountParent == null) {
			LOG.error(messageLog + " ::: ACCOUNT PARENT DON´T EXIST  ::: ");
			throw new CustomException(properties.RESPONSE_CUSTOMIZED_MESSAGE_ACCOUNT_PARENT_DONT_EXIST);
		}

		accountChildList = accountRepo.findAccountsByParentIdAndTypeChild(idAccountParent, "CHILD");
		categoriesFull = categoryRepo.findAll();

		if(accountChildList.size() == 0 ) {
			LOG.info(messageLog + " ::: ACCOUNTS CHILD'S DON'T EXIST, RETURN ALL CATEGORIES TO SELECT   ::: ");
			return categoriesFull;
		}

		for ( Account accountChild : accountChildList ) {
			if(accountChild.getCategories().size() > 0) {
				allCategoriesAssocToRemove.addAll(accountChild.getCategories());
			}
		}

		if(allCategoriesAssocToRemove.size() == 0) {
			LOG.info(messageLog + " ::: ACCOUNTS CATEGORIES DON'T FOUND, RETURN ALL CATEGORIES TO SELECT  ::: ");
			return categoriesFull;
		}

		categoriesFull.removeAll(allCategoriesAssocToRemove);

		categoriesDontAssocResponse = categoriesFull;

		return categoriesDontAssocResponse;
	}

}

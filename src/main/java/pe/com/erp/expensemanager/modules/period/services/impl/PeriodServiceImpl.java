package pe.com.erp.expensemanager.modules.period.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.com.erp.expensemanager.exception.CustomException;
import pe.com.erp.expensemanager.modules.account.model.Account;
import pe.com.erp.expensemanager.modules.account.model.TypeStatusAccountOPC;
import pe.com.erp.expensemanager.modules.account.repository.AccountRepository;
import pe.com.erp.expensemanager.modules.categories.model.Category;
import pe.com.erp.expensemanager.modules.categories.repository.CategoryRepository;
import pe.com.erp.expensemanager.modules.transaction.repository.TransactionRepository;
import pe.com.erp.expensemanager.modules.period.dao.PeriodDetailDao;
import pe.com.erp.expensemanager.modules.period.model.Period;
import pe.com.erp.expensemanager.modules.period.repository.PeriodRepository;
import pe.com.erp.expensemanager.modules.period.services.interfaz.IPeriodService;
import pe.com.erp.expensemanager.properties.PropertiesExtern;
import pe.com.erp.expensemanager.shared.model.Response;
import pe.com.erp.expensemanager.utils.Utils;

@Service
@Transactional
public class PeriodServiceImpl implements IPeriodService {

	private static final Logger LOG = LoggerFactory.getLogger(PeriodServiceImpl.class);
	
	@Autowired
	PeriodRepository periodoRepo;
	
	@Autowired
	AccountRepository accountRepo;
	
	@Autowired
	CategoryRepository categRepo;
	
	@Autowired
    TransactionRepository expensRepo;
	
	@Autowired
	PropertiesExtern propertiesExtern;
	
	public Period update(Period period) {
		return periodoRepo.save(period);
	}
	
	@Override
	@Transactional
	public Period save(Period period) {
		Period periodSummarySave = new Period();
		periodSummarySave.setStartDate(new Date());
		periodSummarySave.setFinalDate(Utils.getNextLocalDate(new Date(),"final", 1));
		periodSummarySave.setActive(false);
		periodSummarySave.setStatusPeriod(true);
		periodSummarySave.setWorkspace(period.getWorkspace());
		return periodoRepo.save(periodSummarySave);
	}
	
	@Override
	public List<PeriodDetailDao> listPeriodDetailsHeaderByIdWorkspace(Long idWorkspace) {
		
		List<PeriodDetailDao> periodDetailList = new ArrayList<PeriodDetailDao>();
		List<Period> periodList = new ArrayList<Period>();
		PeriodDetailDao periodDetail = null;
		Account account = null;
		double totalSpent = 0;
		
		periodList = periodoRepo.listAllPeriodSummaryByWorkspaceId(idWorkspace);

		for (Period periodSummary : periodList) {
			account = new Account();
			account = accountRepo.findAccountByTypeAccountAndStatusAccountAndPeriodId(
					1L, TypeStatusAccountOPC.INITIAL, periodSummary.getId());

			//totalSpent = expensRepo.totalSpentedByDatePeriodId(periodSummary.getId());
			 
			periodDetail = new PeriodDetailDao();
			periodDetail.setPeriod(periodSummary);
			periodDetail.setTotalSpent(totalSpent);
			periodDetail.setAmountEstimado(account!=null?account.getBalance():totalSpent);
			periodDetailList.add(periodDetail);
		}
		return periodDetailList;
	}
	
	@Override
	public List<Period> listPeriodByIdWorkspace(Long idWorkspace) {
		return periodoRepo.listAllPeriodSummaryByWorkspaceId(idWorkspace);
	}

	@Override
	public Period findByIdPeriod(Long idPeriod) {
		return periodoRepo.findById(idPeriod).orElseGet(null);
	}

	@Override
	public Period periodByWorkspaceIdAndStatusPeriod(Long workspaceId, boolean status) {
		return periodoRepo.periodByWorkspaceIdAndStatusPeriod(workspaceId, status);
	}

	@Override
	@Transactional
	public PeriodDetailDao periodDetailsHeaderByIdPeriod(Long idPeriod, Long idOwner) {
		
		Account account = new Account();
		Period periodSummary = new Period();
		PeriodDetailDao periodDetailHeader = new PeriodDetailDao();
		double totalSpent = 0;
		
		periodSummary = periodoRepo.findByPeriodIdAndOwnerId(idPeriod, idOwner);
		
		if(periodSummary == null)
			throw new CustomException(propertiesExtern.RESPONSE_CUSTOMIZED_MESSAGE_PERIOD_NOT_FOUND);
		
		account = accountRepo.findAccountByTypeAccountAndStatusAccountAndPeriodId(1L, TypeStatusAccountOPC.INITIAL, periodSummary.getId());
		//totalSpent = expensRepo.totalSpentedByDatePeriodId(periodSummary.getId());
		periodDetailHeader.setPeriod(periodSummary);
		periodDetailHeader.setTotalSpent(totalSpent);
		periodDetailHeader.setAmountEstimado(account!=null?account.getBalance():totalSpent);
		
		return periodDetailHeader;
	}

	@Override
	@Transactional
	public Response closePeriod(Period periodRequest) {
		
		Response response = new Response();
		Period newPeriod = new Period();
		List<Account> listAccountToUpdate = new ArrayList<>();
		List<Category> listCategories = null;
		Double sumatoriaTotalBalanceSecundaryAccounts = 0.0;
		Double totalSpentPendingIfExist = 0.0;

		//Update status period :: closed
		//periodRequest.setActivate(true);
		periodRequest.setStatusPeriod(false);
		periodRequest.setFinalDate(periodRequest.getFinalDate());
		periodoRepo.save(periodRequest);
		
		//Get list account by period & owner
		listAccountToUpdate = accountRepo.findListAccountByStatusAccountAndPeriodId(TypeStatusAccountOPC.PROCESS, periodRequest.getId());
		// Begin Open nexts period
		newPeriod = generateNewPeriod(periodRequest);
		newPeriod = periodoRepo.save(newPeriod);
		
		//Validate if not exists expenses pending pay
		//List<Expense> expenses = expensRepo.findExpensesBypIdPeriodAndIsPendingPay(periodRequest.getId(), true);
		
		if(listAccountToUpdate.size() == 0) {
			response.setTitle(propertiesExtern.RESPONSE_GENERIC_SUCCESS_TITLE);
			response.setStatus(propertiesExtern.RESPONSE_GENERIC_SUCCESS_STATUS);
			response.setMessage(propertiesExtern.RESPONSE_CUSTOMIZED_PERIOD_CLOSE_SUCCESS);
			response.setObject(newPeriod);
			return response;
		}

		//Update status to CLOSED Account List to origin period
		for (Account account : listAccountToUpdate) {
			account.setStatusAccount(TypeStatusAccountOPC.CLOSED);
			accountRepo.save(account);
		}

		//Get sum total secundary balance
		for (Account account : listAccountToUpdate) {
			if(!account.getAccountType().getTypeName().equals("PARENT"))
				  sumatoriaTotalBalanceSecundaryAccounts+= account.getBalance();
		}

		//Initial status account created
		for (Account account : listAccountToUpdate) {
		  Account accountCreated = new Account(); 
		  accountCreated =  getNewAccount(account, sumatoriaTotalBalanceSecundaryAccounts, TypeStatusAccountOPC.INITIAL);
		  accountCreated.setPeriod(newPeriod);
		  
		  listCategories = new ArrayList<Category>();
		  
		  //Quitando referencias compartidas obteniendo los valores desde DB y agregando a la lista de categ nueva.
		  for (Category category : account.getCategories()) {
			  Category categ = categRepo.findById(category.getId()).orElse(null);
			  listCategories.add(categ);
		  }
    	
    	  accountCreated.setCategories(listCategories);
    	  accountCreated = accountRepo.save(accountCreated);
		}

		response.setTitle(propertiesExtern.RESPONSE_GENERIC_SUCCESS_TITLE);
		response.setStatus(propertiesExtern.RESPONSE_GENERIC_SUCCESS_STATUS);
		response.setMessage(propertiesExtern.RESPONSE_CUSTOMIZED_PERIOD_CLOSE_SUCCESS);
		response.setObject(newPeriod);
		
		return response;
	}
	
	private Period generateNewPeriod(Period periodOrigin) {
		
		Period newPeriod = new Period();
		
		int difMonth = Utils.extracted(periodOrigin.getFinalDate()) - Utils.extracted(periodOrigin.getStartDate());
		int difMonthSend = (difMonth==0)?1:(difMonth);
		
		//newPeriod.setActivate(true);
		newPeriod.setStatusPeriod(true);
		newPeriod.setWorkspace(periodOrigin.getWorkspace());
		newPeriod.setStartDate(Utils.getNextLocalDate(periodOrigin.getFinalDate(),"start", 5));
		newPeriod.setFinalDate(Utils.getNextLocalDate(periodOrigin.getFinalDate(),"final", difMonthSend));
		
		return newPeriod;
	}

	private Account getNewAccount(Account account, Double sumatoriaTotalBalanceSecundaryAccounts, TypeStatusAccountOPC status) {
				
		 Account accountCreated = new Account();
		 accountCreated.setAccountName(account.getAccountName());
		 accountCreated.setAccountNumber(account.getAccountNumber());
		 accountCreated.setAccountType(account.getAccountType());
		 accountCreated.setStatusAccount(status);
		 accountCreated.setEnabled(true);
		 accountCreated.setBalance(account.getBalance());
		 
		 if(!account.getAccountType().getTypeName().equals("PARENT")) {
			 accountCreated.setBalanceAvailable(account.getBalance());
		 } else {
			 accountCreated.setBalanceAvailable(Utils.roundTwoDecimals(account.getBalance() - sumatoriaTotalBalanceSecundaryAccounts));
		 }
		 
		return accountCreated;
	}
	
}

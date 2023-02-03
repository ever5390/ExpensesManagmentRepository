package pe.com.erp.expensemanager.modules.notifications.services.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.com.erp.expensemanager.modules.account.model.Account;
import pe.com.erp.expensemanager.modules.account.model.TypeStatusAccountOPC;
import pe.com.erp.expensemanager.modules.account.repository.AccountRepository;
import pe.com.erp.expensemanager.modules.expense.model.According;
import pe.com.erp.expensemanager.modules.expense.model.Expense;
import pe.com.erp.expensemanager.modules.expense.repository.AccordingRepository;
import pe.com.erp.expensemanager.modules.expense.repository.ExpenseRepository;
import pe.com.erp.expensemanager.modules.notifications.model.NotificationExpense;
import pe.com.erp.expensemanager.modules.notifications.model.TypeStatusNotificationExpense;
import pe.com.erp.expensemanager.modules.notifications.repository.NotificationRepository;
import pe.com.erp.expensemanager.modules.notifications.services.interfaz.INotificationService;
import pe.com.erp.expensemanager.shared.repository.VoucherRepository;
import pe.com.erp.expensemanager.utils.Utils;

@Service
@Transactional
public class NotificationServiceImpl implements INotificationService {
	
	private static final Logger LOG = LoggerFactory.getLogger(NotificationServiceImpl.class);

	@Autowired
	NotificationRepository notifRepo;
	
	@Autowired
	AccordingRepository accordingRepo;
	
	@Autowired
	ExpenseRepository expenseRepo;
	
	@Autowired
	VoucherRepository voucherRepo;
	
	@Autowired
	AccountRepository accountRepo;
	/*
	@Override
	public List<NotificationExpense> findNotificationStatusByUserIdAndTypeUser(Long idUser) {
		return notifRepo.findByUserEmisorAndStatus(idUser);
	}
*/
	@Override
	@Transactional
	public NotificationExpense updateNotificationExpense(NotificationExpense notificationExpenseRequest ) {
	    // 1 = APROBAR, :: STATUS = CANCELADO => Emisor: Actualiza STATUS expense a PAGADO, ACCORDING PROPIO y QUIEN PAGA = MYSELF
	    // 2 = PAGAR,  :: STATUS = POR_CONFIRMAR => Receptor: Guarda gasto, la mitad del monto(compartido)
	    // 3 = CONFIRMAR,  :: STATUS = PAGADO =>  Emisor: Actualiza STATUS expense a PAGADO y montos la mitad (compartido)
	    // 4 = RECHAZAR,  :: STATUS = RECHAZADO =>
	    // 5 = RECLAMAR :: STATUS = RECLAMADO => 
		
		NotificationExpense notificationExpenseUpdate = new NotificationExpense();
		Expense expenseReq = new Expense();
		According according = new According();

		notificationExpenseUpdate = notifRepo.findById(notificationExpenseRequest.getId()).orElse(null);
		if(notificationExpenseUpdate == null) return notificationExpenseUpdate;
		notificationExpenseUpdate.setComentarios(notificationExpenseRequest.getComentarios());
		notificationExpenseUpdate.setCreateAt(notificationExpenseRequest.getCreateAt());
		notificationExpenseUpdate.setExpenseShared(notificationExpenseRequest.getExpenseShared());
		notificationExpenseUpdate.setPayer(notificationExpenseRequest.getPayer());
		notificationExpenseUpdate.setStatusNotification(notificationExpenseRequest.getStatusNotification());
		notificationExpenseUpdate.setVouchers(notificationExpenseRequest.getVouchers());
		notificationExpenseUpdate = notifRepo.save(notificationExpenseUpdate);
		
		LOG.info(notificationExpenseUpdate.toString());

		if(notificationExpenseRequest.getStatusNotification().equals(TypeStatusNotificationExpense.CANCELADO)) {
			according = accordingRepo.findById(1L).orElse(new According(1L, "PROPIO","",""));
			LOG.info("According");
			LOG.info(according.toString());
			expenseReq = notificationExpenseRequest.getExpenseShared();
			//expenseReq.setPendingPayment(false);
			//expenseReq.setAccordingType(according);
			//expenseReq.setPayer(expenseReq.getWorkspace().getOwner().getName());
			expenseRepo.save(expenseReq);
		}
		
		if(notificationExpenseRequest.getStatusNotification().equals(TypeStatusNotificationExpense.PAGADO)) {
			expenseReq = notificationExpenseRequest.getExpenseShared();
			//expenseReq.setPendingPayment(false);

			
			//ACCOUNT UPDATE
			updateAccountIfExists(expenseReq);
			
			//El valor del amount es el que contabilizará, por tanto si ya se repuso, NO EXISTE ESE GASTO

			
			expenseRepo.save(expenseReq);
		}

		return notificationExpenseUpdate;
	}
	
	@Transactional
	public void updateAccountIfExists(Expense expenseUpdateReq) {
		
		if(expenseUpdateReq.getAccount() == null) {
			//caso account de expense sea NULL
			Account accountMainExist = accountRepo.findAccountByTypeAccountAndStatusAccountAndPeriodId(1L, TypeStatusAccountOPC.PROCESS, expenseUpdateReq.getPeriod().getId());
			if(accountMainExist == null) return;
			expenseUpdateReq.setAccount(accountMainExist);	
		}
		
		Account accountUpdate = new Account();
		accountUpdate = expenseUpdateReq.getAccount();
		double amountBalanceFlowUpdate = expenseUpdateReq.getAccount().getBalanceAvailable();
		double amountSpentReq = expenseUpdateReq.getAmount();
		accountUpdate.setBalanceAvailable(Utils.roundTwoDecimals(amountBalanceFlowUpdate + amountSpentReq));
		accountRepo.save(accountUpdate);
	}

}

package pe.com.erp.expensemanager.modules.partners.services.impl;

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
import pe.com.erp.expensemanager.modules.partners.model.Partner;
import pe.com.erp.expensemanager.modules.partners.repository.PartnerRepository;
import pe.com.erp.expensemanager.modules.partners.services.interfaz.IPartnerService;
import pe.com.erp.expensemanager.shared.repository.VoucherRepository;
import pe.com.erp.expensemanager.utils.Utils;

import java.util.List;

@Service
@Transactional
public class PartnerServiceImpl implements IPartnerService {
	
	private static final Logger LOG = LoggerFactory.getLogger(PartnerServiceImpl.class);

	@Autowired
	PartnerRepository partnerRepository;

	@Override
	public List<Partner> findPartnersByOwnerId(Long idOwner) {
		return partnerRepository.findPartnersByOwnerId(idOwner);
	}
}

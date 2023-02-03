package pe.com.erp.expensemanager.modules.notifications.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.com.erp.expensemanager.modules.notifications.model.NotificationExpense;
import pe.com.erp.expensemanager.modules.notifications.services.interfaz.INotificationService;
import pe.com.erp.expensemanager.properties.PropertiesExtern;

@RestController
@EnableTransactionManagement
@CrossOrigin(origins=  {"http://localhost:4200", "*"})
@RequestMapping(path="/api/v1")
public class NotificationExpenseController {
	
	public static final Logger logger = LoggerFactory.getLogger(NotificationExpenseController.class);
	
	@Autowired 
	PropertiesExtern properties;
	
	@Autowired
	INotificationService inotificationService;
	
	@PutMapping("/notification")
	public NotificationExpense updateNotificationExpense(@RequestBody NotificationExpense notificationExpenseRequest) {
		return inotificationService.updateNotificationExpense(notificationExpenseRequest);
	}
	/*
	@GetMapping("owner/{idOwner}/notifications")
	public List<NotificationExpense> findNotificationStatusByUserIdAndTypeUser(@PathVariable Long idOwner) {
		return inotificationService.findNotificationStatusByUserIdAndTypeUser(idOwner);
	}
*/
}

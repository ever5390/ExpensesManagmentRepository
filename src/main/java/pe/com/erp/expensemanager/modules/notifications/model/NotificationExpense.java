package pe.com.erp.expensemanager.modules.notifications.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import pe.com.erp.expensemanager.modules.expense.model.Expense;
import pe.com.erp.expensemanager.modules.owner.model.Owner;
import pe.com.erp.expensemanager.shared.model.Vouchers;

@Entity
@Table(name = "notification_expense")
public class NotificationExpense {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne
	private Expense expenseShared;
	
	@Enumerated(value = EnumType.STRING)
	private TypeStatusNotificationExpense statusNotification;
	
    @ManyToMany
	@JoinColumn(name = "FK_VOUCHERS_ID")
    private List<Vouchers> vouchers;
    
    private String comentarios;
    
    @ManyToOne
    private Owner payer;
    
	@Temporal(value=TemporalType.TIMESTAMP)
    @Column(name="CREATE_AT")
	private Date createAt;

	public NotificationExpense() {}
    
	public NotificationExpense(Long id, Expense expenseShared, TypeStatusNotificationExpense statusNotification,
			List<Vouchers> vouchers, String comentarios, Owner payer, Date createAt) {
		super();
		this.id = id;
		this.expenseShared = expenseShared;
		this.statusNotification = statusNotification;
		this.vouchers = vouchers;
		this.comentarios = comentarios;
		this.payer = payer;
		this.createAt = createAt;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Expense getExpenseShared() {
		return expenseShared;
	}

	public void setExpenseShared(Expense expenseShared) {
		this.expenseShared = expenseShared;
	}

	public List<Vouchers> getVouchers() {
		return vouchers;
	}

	public void setVouchers(List<Vouchers> vouchers) {
		this.vouchers = vouchers;
	}

	public String getComentarios() {
		return comentarios;
	}

	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}

	public TypeStatusNotificationExpense getStatusNotification() {
		return statusNotification;
	}

	public void setStatusNotification(TypeStatusNotificationExpense statusNotification) {
		this.statusNotification = statusNotification;
	}

	public Owner getPayer() {
		return payer;
	}

	public void setPayer(Owner payer) {
		this.payer = payer;
	}

	@Override
	public String toString() {
		return "NotificationExpense [id=" + id + ", expenseShared=" + expenseShared.getId() + ", statusNotification="
				+ statusNotification + ", vouchers=" + vouchers + ", comentarios=" + comentarios + ", payer=" + payer.getName()
				+ "]";
	}

	
	
    
    
}

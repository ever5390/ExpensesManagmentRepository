package pe.com.erp.expensemanager.modules.expense.model;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.com.erp.expensemanager.modules.account.model.Account;
import pe.com.erp.expensemanager.modules.categories.model.Category;
import pe.com.erp.expensemanager.modules.partners.model.Partner;
import pe.com.erp.expensemanager.modules.period.model.Period;
import pe.com.erp.expensemanager.shared.model.Vouchers;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "expense")
public class Expense {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Double amount;

	@Column(nullable = false)
	private Double amountShow;

	private boolean pendingPay;

	private boolean enabled;

	private double amountToRecover;

	@Enumerated(value = EnumType.STRING)
	private ExpenseType expenseType;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "CREATE_AT")
	private Date createAt;

	private String description;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "FK_VOUCHERS_ID")
	private List<Vouchers> vouchers;


	@ManyToOne
	@JoinColumn(name = "FK_CATEGORY_ID")
	private Category category;

	@ManyToOne
	@JoinColumn(name = "FK_ACCOUNT_ID", updatable = true)
	private Account account;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "reposition_id")
	private List<Reposition> reposition;

	@ManyToMany
	@JoinTable(name = "Tag_Expenses", joinColumns = @JoinColumn(name = "FK_EXPENSE"), inverseJoinColumns = @JoinColumn(name = "FK_TAG"))
	private List<Tag> tag;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_PERIOD_ID", nullable = true)
	private Period period;

	@ManyToOne
	@JoinColumn(name = "fk_partner_id")
	private Partner partner;

	private Long idExpenseToPay;


	@Override
	public String toString() {
		return "Expense{" +
				"id=" + id +
				", amount=" + amount +
				", amountShow=" + amountShow +
				", pendingPay=" + pendingPay +
				", enabled=" + enabled +
				", amountToRecover=" + amountToRecover +
				", expenseType=" + expenseType +
				", createAt=" + createAt +
				", description='" + description + '\'' +
				", vouchers=" + vouchers +
				", category=" + category +
				", account=" + account +
				", tag=" + tag +
				", period=" + period +
				", partner=" + partner +
				", idExpenseToPay=" + idExpenseToPay +
				'}';
	}}

package pe.com.erp.expensemanager.modules.transaction.model;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import pe.com.erp.expensemanager.modules.account.model.Account;
import pe.com.erp.expensemanager.modules.categories.model.Category;
import pe.com.erp.expensemanager.modules.period.model.Period;
import pe.com.erp.expensemanager.shared.model.Vouchers;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private double amount;

	@Column(nullable = false)
	private double amountPayed;

	private double amountToRecover;

	private boolean pendingPay;

	private boolean enabled;

	@Enumerated(value = EnumType.STRING)
	private TransactionType transactionType;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "CREATE_AT")
	private Date createAt;

	private String description;

	@OneToMany
	@JoinColumn(name = "FK_TRANSACTION_ID")
	private List<Vouchers> vouchers;

	@ManyToOne
	@JoinColumn(name = "FK_CATEGORY_ID")
	private Category category;

	@ManyToOne
	@JoinColumn(name = "FK_ACCOUNT_ID", updatable = true)
	private Account account;

	@OneToMany
	@JoinColumn(name = "FK_TRANSACTION_ID")
	private List<Reposition> reposition;

	@ManyToMany
	@JoinTable(name = "Tag_Expenses", joinColumns = @JoinColumn(name = "FK_EXPENSE"), inverseJoinColumns = @JoinColumn(name = "FK_TAG"))
	private List<Tag> tag;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_PERIOD_ID", nullable = true)
	private Period period;

	private Long idExpenseToPay;

	@Override
	public String toString() {
		return "Transaction{" +
				"id=" + id + '\n' +
				", amount=" + amount + '\n' +
				", amountPayed=" + amountPayed + '\n' +
				", amountToRecover=" + amountToRecover + '\n' +
				", pendingPay=" + pendingPay + '\n' +
				", enabled=" + enabled + '\n' +
				", transactionType=" + transactionType + '\n' +
				", createAt=" + createAt + '\n' +
				", description='" + description + '\'' + '\n' +
				", vouchers=" + vouchers.size() + '\n' +
				", category=" + category.getName() + '\n' +
				", account=" + account + '\n' +
				", reposition=" + reposition + '\n' +
				", tag=" + tag + '\n' +
				", period=" + period + '\n' +
				", idExpenseToPay=" + idExpenseToPay + '\n' +
				'}';
	}
}

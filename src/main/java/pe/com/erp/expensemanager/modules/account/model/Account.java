package pe.com.erp.expensemanager.modules.account.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import pe.com.erp.expensemanager.modules.categories.model.Category;
import pe.com.erp.expensemanager.modules.period.model.Period;

@Entity
@Table(name = "account")
public class Account {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String accountNumber;
	
	@NotNull(message = "El campo nombre no puede ser nulo")
	private String accountName;
	
	@NotNull(message = "El campo nombre no puede ser nulo")
	private Double balance;
	
	private Double balanceOnlyInitial;

	private Double balanceAvailable;
	
	private boolean enabled;
	
	@Enumerated(value = EnumType.STRING)
	private TypeStatusAccountOPC statusAccount;
	
	@Temporal(value=TemporalType.TIMESTAMP)
    @Column(name="CREATE_AT")
	private Date createAt;
	
	@ManyToOne
	@NotNull(message = "El campo tipo de cuenta no puede ser nulo")
	@JoinColumn(name = "FK_ACCOUNTTYPE_ID", updatable = true, nullable = false)
	private AccountType accountType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull(message = "El campo periodo no puede ser nulo")
	@JoinColumn(name = "FK_PERIOD_ID", updatable = true, nullable = false)
	private Period period;

	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull(message = "El campo financialEntity no puede ser nulo")
	@JoinColumn(name = "FK_FINANCIAL_ENTITY_ID", updatable = true, nullable = true)
	private FinancialEntity financialEntity;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull(message = "El campo tipo de tarjeta no puede ser nulo")
	@JoinColumn(name = "FK_TYPE_CARD_ID", updatable = true, nullable = true)
	private TypeCard typeCard;

	private Long accountParentId;

	@ManyToMany
	private List<Category> categories;

	public Long getAccountParentId() {
		return accountParentId;
	}

	public void setAccountParentId(Long accountParentId) {
		this.accountParentId = accountParentId;
	}

	@PrePersist
	public void prePersistCreateAt() {
		this.createAt = new Date();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Double getBalanceOnlyInitial() {
		return balanceOnlyInitial;
	}

	public void setBalanceOnlyInitial(Double balanceOnlyInitial) {
		this.balanceOnlyInitial = balanceOnlyInitial;
	}

	public Double getBalanceAvailable() {
		return balanceAvailable;
	}

	public void setBalanceAvailable(Double balanceAvailable) {
		this.balanceAvailable = balanceAvailable;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public TypeStatusAccountOPC getStatusAccount() {
		return statusAccount;
	}

	public void setStatusAccount(TypeStatusAccountOPC statusAccount) {
		this.statusAccount = statusAccount;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public FinancialEntity getFinancialEntity() {
		return financialEntity;
	}

	public void setFinancialEntity(FinancialEntity financialEntity) {
		this.financialEntity = financialEntity;
	}

	public TypeCard getTypeCard() {
		return typeCard;
	}

	public void setTypeCard(TypeCard typeCard) {
		this.typeCard = typeCard;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	@Override
	public String toString() {
		return "Account{" +
				"id=" + id +
				", accountNumber='" + accountNumber + '\n' +
				", accountName='" + accountName + '\n' +
				", balance=" + balance + '\n' +
				", balanceOnlyInitial=" + balanceOnlyInitial + '\n' +
				", balanceAvailable=" + balanceAvailable + '\n' +
				", statusAccount=" + statusAccount + '\n' +
				", createAt=" + createAt + '\n' +
				", accountType=" + accountType.getTypeName() + '\n' +
				", financialEntity=" + financialEntity.getName() + '\n' +
				", typeCard=" + typeCard.getName() + '\n' +
				", accountParentId=" + accountParentId + '\n' +
				'}';
	}
}
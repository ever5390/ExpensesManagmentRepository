package pe.com.erp.expensemanager.modules.account.model;

import java.util.Date;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pe.com.erp.expensemanager.modules.period.model.Period;
import pe.com.erp.expensemanager.modules.transaction.model.Transaction;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "transference")
public class Transference {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_ACCOUNT_ORIGIN_ID", updatable = true)
	private Account accountOrigin;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_ACCOUNT_DESTINY_ID", updatable = true, nullable = true)
	private Account accountDestiny;

	private Double amount;

	private String reason;

	private boolean enabled;

	@Enumerated(value = EnumType.STRING)
	private TypeTransference typeTransference;

	@ManyToOne(fetch = FetchType.LAZY)
	private Period period;

	@Column(name = "CREATED_AT", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	private Long idExpenseAssoc;
		
}

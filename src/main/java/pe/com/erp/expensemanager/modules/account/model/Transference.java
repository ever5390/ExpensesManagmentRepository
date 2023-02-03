package pe.com.erp.expensemanager.modules.account.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pe.com.erp.expensemanager.modules.period.model.Period;

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
	
	private boolean typeEntryExtern;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Period period;
	
	@Column(name = "CREATED_AT", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
		
}

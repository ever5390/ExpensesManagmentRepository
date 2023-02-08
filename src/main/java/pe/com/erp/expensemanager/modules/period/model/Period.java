package pe.com.erp.expensemanager.modules.period.model;
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
import pe.com.erp.expensemanager.modules.workspace.model.WorkSpace;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "period")
public class Period {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	//statusPeriod :: 0 CERRADO , 1 ABIERTO
	private boolean statusPeriod;
	
	private boolean active;
	@Temporal(value=TemporalType.TIMESTAMP)
    @Column(name="START_DATE")
	private Date startDate;
	
	@Temporal(value=TemporalType.TIMESTAMP)
    @Column(name="FINAL_DATE")
	private Date finalDate;
	
	//@JsonIgnoreProperties(value={"account", "hi0.bernateLazyInitializer", "handler"}, allowSetters=true)
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_WORKSPACE_ID", nullable = false)
	private WorkSpace workspace;

	@Override
	public String toString() {
		return "\n AccountingPeriodSummary \n[Id=" + id + ",\n statusPeriod=" + statusPeriod + ",\n startDate=" + startDate
				+ ",\n finalDate=" + finalDate + ",\n owner=" + workspace.toString() + "]";
	}
}

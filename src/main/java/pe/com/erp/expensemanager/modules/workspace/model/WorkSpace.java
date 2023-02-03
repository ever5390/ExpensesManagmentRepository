package pe.com.erp.expensemanager.modules.workspace.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pe.com.erp.expensemanager.modules.owner.model.Owner;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "workspace")
public class WorkSpace {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
		
	private String name;
	
	private boolean active;
	
	@ManyToOne
	@JoinColumn(name = "FK_TYPE_ID", nullable = false)
	private TypeWorkSpace typeWSPC;
	
	@ManyToOne
	@JoinColumn(name = "FK_OWNER_ID", updatable = false, nullable = false)
	private Owner owner;

	@Override
	public String toString() {
		return "WorkSpace [id=" + id + ", name=" + name + ", typeWSPC=" + typeWSPC + ", owner=" + owner + "]";
	}
	
}

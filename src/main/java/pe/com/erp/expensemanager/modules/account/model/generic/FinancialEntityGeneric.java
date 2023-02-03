package pe.com.erp.expensemanager.modules.account.model.generic;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "financial_entity_generic")
public class FinancialEntityGeneric {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Boolean enabled;
	
	@NotBlank(message = "El campo nombre no puede estar vacío")
	@NotNull(message = "El campo nombre no puede ser nulo")
	@Column( unique = true)
	private String name;
	
	private String image;
	
}

package pe.com.erp.expensemanager.modules.categories.model.generic;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import pe.com.erp.expensemanager.modules.owner.model.Owner;

@Entity
@Table(name = "category_generic")
public class CategoryGeneric {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "El campo nombre no puede estar vacío")
	@NotNull(message = "El campo nombre no puede ser nulo")
	@Column(unique = true)
	private String name;
	
	@Column(name = "active")
	private boolean active;
	
	@NotBlank
	private String image;

	@Override
	public String toString() {
		return "Category [id=" + id + ", name=" + name + ", active=" + active + ", image=" + image + "]";
	}

	public CategoryGeneric(Long id,
			@NotBlank(message = "El campo nombre no puede estar vacío") @NotNull(message = "El campo nombre no puede ser nulo") String name,
			boolean active, @NotBlank String image) {
		super();
		this.id = id;
		this.name = name;
		this.active = active;
		this.image = image;
	}

	public CategoryGeneric() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	 
}

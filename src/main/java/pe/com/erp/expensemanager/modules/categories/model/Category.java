package pe.com.erp.expensemanager.modules.categories.model;

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
@Table(name = "category")
public class Category {

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

	@ManyToOne
	@JoinColumn(name = "group_category_id")
	private GroupCategory groupCategory;

	@ManyToOne
	@JoinColumn(name = "sub_category_id")
	private SubCategory subCategory;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_OWNER_ID", updatable = false, nullable = false)
	private Owner owner;

	public SubCategory getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(SubCategory subCategory) {
		this.subCategory = subCategory;
	}

	public GroupCategory getGroupCategory() {
		return groupCategory;
	}

	public void setGroupCategory(GroupCategory groupCategory) {
		this.groupCategory = groupCategory;
	}

	@Override
	public String toString() {
		return "Category [id=" + id + ", name=" + name + ", active=" + active + ", image=" + image + ", owner=" + owner + "]";
	}

	public Category(Long id, String name, boolean active, @NotBlank String image, Owner owner) {
		this.id = id;
		this.name = name;
		this.active = active;
		this.image = image;
		this.owner = owner;
	}

	public Category() {
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

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	 
}

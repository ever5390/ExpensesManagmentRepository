package pe.com.erp.expensemanager.modules.owner.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

@Table(name ="owner")
@Entity
public class Owner {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "El campo nombre no puede estar vacío")
	@NotNull(message = "El campo nombre no puede ser nulo")
	private String name;
	
	@NotBlank(message = "El campo email no puede estar vacío")
	@NotNull(message = "El campo email no puede ser nulo")
	@Column(unique= true)
	@Email
	private String email;
	
	private String image;
	
	@Temporal(value=TemporalType.TIMESTAMP)
    @Column(name="CREATE_AT")
	private Date createAt;
	
	@NotBlank(message = "El campo username no puede estar vacío")
	@NotNull(message = "El campo username no puede ser nulo")
	@Column(unique= true)
	private String username;

	private String password;

	private Boolean enabled;

	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinTable(name="owner_roles", joinColumns = @JoinColumn(name="owner_id"), 
			inverseJoinColumns = @JoinColumn(name="role_id"))
	private List<Role> roles;
	
	@PrePersist
	public void prePersistCreateAt() {
		this.createAt = new Date();
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}


	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	
	@Override
	public String toString() {
		return " \n Owner [id=" + id + ", name=" + name + ", email=" + email + ", image=" + image + ", createAt=" + createAt
				+ ", username=" + username + ", password=" + password + ", enabled=" + enabled + ", roles=" + roles
				+ "]";
	}

}

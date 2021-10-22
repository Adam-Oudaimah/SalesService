package colourmyplate.data.client;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import colourmyplate.data.product.Product;
import colourmyplate.data.sale.Sale;

@Entity
@Table(name = "clients")
public class Client {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name", columnDefinition = "VARCHAR(20)")
	private String name;

	@Column(name = "last_name", columnDefinition = "VARCHAR(20)")
	private String lastName;

	@Column(name = "mobile", columnDefinition = "VARCHAR(20)", unique = true)
	private String mobile;

	@OneToMany(mappedBy = "seller")
	private Collection<Sale> sellers = new ArrayList<Sale>();

	@OneToMany(mappedBy = "client")
	private Collection<Sale> clients = new ArrayList<Sale>();

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
	private Collection<Product> products = new ArrayList<Product>();

	// Hibernate requires a no-arg constructor
	public Client() {
	}

	public Client(Long id, String name, String lastName, String mobile) {
		this.id = id;
		this.name = name;
		this.lastName = lastName;
		this.mobile = mobile;
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

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Collection<Sale> getSellers() {
		return sellers;
	}

	public void setSellers(Collection<Sale> sellers) {
		this.sellers = sellers;
	}

	public Collection<Sale> getClients() {
		return clients;
	}

	public void setClients(Collection<Sale> clients) {
		this.clients = clients;
	}

	public Collection<Product> getProducts() {
		return products;
	}

	public void setProducts(Collection<Product> products) {
		this.products = products;
	}
}

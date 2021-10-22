package colourmyplate.data.product;

import java.sql.Date;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import colourmyplate.data.client.Client;
import colourmyplate.data.sale.SaleOrder;

@Entity
@Table(name = "products")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name", columnDefinition = "VARCHAR(20)")
	private String name;

	@Column(name = "quantity", columnDefinition = "INT")
	private int quantity;

	@Column(name = "price", columnDefinition = "FLOAT")
	private float price;

	@Column(name = "description", columnDefinition = "VARCHAR(200)")
	private String description;

	@Column(name = "category", columnDefinition = "VARCHAR(20)")
	private String category;

	@Column(name = "creation_date", columnDefinition = "DATETIME")
	private Date creationDate;

	@ManyToOne(optional = false)
	@JoinColumn(name = "owner", nullable = false)
	private Client owner;

	@OneToMany(mappedBy = "product")
	private Collection<SaleOrder> saleOrders;

	// Hibernate requires a no-arg constructor
	public Product() {
	}

	public Product(Long id, String name, int quantity, float price, String description, String category,
			Date creationDate, Client owner) {
		this.id = id;
		this.name = name;
		this.quantity = quantity;
		this.price = price;
		this.description = description;
		this.category = category;
		this.creationDate = creationDate;
		this.owner = owner;
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

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Collection<SaleOrder> getSaleOrders() {
		return saleOrders;
	}

	public void setSaleOrders(Collection<SaleOrder> saleOrders) {
		this.saleOrders = saleOrders;
	}

	public Client getOwner() {
		return owner;
	}

	public void setOwner(Client owner) {
		this.owner = owner;
	}
}

package colourmyplate.data.sale;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
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

@Entity
@Table(name = "sales")
public class Sale {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "creation_date", columnDefinition = "DATETIME")
	private Date creationDate;

	@ManyToOne(optional = false)
	@JoinColumn(name = "seller", nullable = false)
	private Client seller;

	@ManyToOne(optional = false)
	@JoinColumn(name = "client", nullable = false)
	private Client client;

	@OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
	private Collection<SaleOrder> saleOrder = new ArrayList<SaleOrder>();

	public Sale() {
	}

	public Sale(Long id, Date creationDate, Client seller, Client client) {
		this.id = id;
		this.creationDate = creationDate;
		this.seller = seller;
		this.client = client;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Client getSeller() {
		return seller;
	}

	public void setSeller(Client seller) {
		this.seller = seller;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Collection<SaleOrder> getSaleOrder() {
		return saleOrder;
	}

	public void setSaleOrder(Collection<SaleOrder> saleOrder) {
		this.saleOrder = saleOrder;
	}
}

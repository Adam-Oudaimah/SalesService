package colourmyplate.data.sale;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "sale_update_logs")
public class SaleUpdateLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "old_quantity", columnDefinition = "INT")
	private int oldQuantity;

	@Column(name = "old_price", columnDefinition = "FLOAT")
	private float oldPrice;

	@Column(name = "new_quantity", columnDefinition = "INT")
	private int newQuantity;

	@Column(name = "new_price", columnDefinition = "FLOAT")
	private float newPrice;

	@Column(name = "date", columnDefinition = "DATETIME")
	private Date date;

	@ManyToOne
	@JoinColumn(name = "sale_order_id")
	private SaleOrder saleOrder;

	public SaleUpdateLog() {
	}

	public SaleUpdateLog(Long id, int oldQuantity, float oldPrice, int newQuantity, float newPrice, Date date,
			SaleOrder saleOrder) {
		this.id = id;
		this.oldQuantity = oldQuantity;
		this.oldPrice = oldPrice;
		this.newQuantity = newQuantity;
		this.newPrice = newPrice;
		this.date = date;
		this.saleOrder = saleOrder;
	}

	public Long getId() {
		return id;
	}

	public int getOldQuantity() {
		return oldQuantity;
	}

	public float getOldPrice() {
		return oldPrice;
	}

	public int getNewQuantity() {
		return newQuantity;
	}

	public float getNewPrice() {
		return newPrice;
	}

	public SaleOrder getSaleOrder() {
		return saleOrder;
	}

	public Date getDate() {
		return date;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setOldQuantity(int oldQuantity) {
		this.oldQuantity = oldQuantity;
	}

	public void setOldPrice(float oldPrice) {
		this.oldPrice = oldPrice;
	}

	public void setNewQuantity(int newQuantity) {
		this.newQuantity = newQuantity;
	}

	public void setNewPrice(float newPrice) {
		this.newPrice = newPrice;
	}

	public void setSaleOrder(SaleOrder saleOrder) {
		this.saleOrder = saleOrder;
	}

	public void setDateCreated(Date date) {
		this.date = date;
	}
}

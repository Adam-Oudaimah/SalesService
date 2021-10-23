package colourmyplate.data;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import colourmyplate.data.client.Client;
import colourmyplate.data.client.ClientRepository;
import colourmyplate.data.product.Product;
import colourmyplate.data.product.ProductRepository;
import colourmyplate.data.rest.server.v1.api.SalesApi;
import colourmyplate.data.rest.server.v1.model.OrderDetail;
import colourmyplate.data.rest.server.v1.model.SaleDAO;
import colourmyplate.data.sale.Sale;
import colourmyplate.data.sale.SaleOrder;
import colourmyplate.data.sale.SaleOrderRepository;
import colourmyplate.data.sale.SaleRepository;
import colourmyplate.data.sale.SaleUpdateLog;
import colourmyplate.data.sale.SaleUpdateLogRepository;

@RestController
@RequestMapping("/v1")
public class SaleService implements SalesApi {

	// The logger object
	private static Logger LOG = LoggerFactory.getLogger(SaleService.class);

	@Autowired
	ProductRepository productsRepository;

	@Autowired
	ClientRepository clientsRepository;

	@Autowired
	SaleRepository salesRepository;

	@Autowired
	SaleOrderRepository saleOrdersRepository;

	@Autowired
	SaleUpdateLogRepository saleUpdateLogsRepository;

	@Override
	@Transactional
	public ResponseEntity<Void> createSaleOperation(@Valid SaleDAO saleDAO) {
		try {
			if (!clientsRepository.existsById(Long.parseLong(saleDAO.getClient()))) {
				LOG.error("Error while saving the new sale opertaion because the client could not be found");
				return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
			}
			if (!clientsRepository.existsById(Long.parseLong(saleDAO.getSeller()))) {
				LOG.error("Error while saving the new sale opertaion because the seller could not be found");
				return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
			}

			// Find the client and the seller
			Client client = clientsRepository.findById(Long.parseLong(saleDAO.getClient())).get();
			Client seller = clientsRepository.findById(Long.parseLong(saleDAO.getSeller())).get();

			// Create the new sale instance, and set the client and the seller
			Sale sale = new Sale();
			sale.setClient(client);
			sale.setSeller(seller);
			sale.setCreationDate(Date.valueOf(LocalDate.now()));
			// Create the new sale operation
			salesRepository.save(sale);

			// Loop over the sale order details
			for (OrderDetail saleOrderDAO : saleDAO.getOrder()) {
				// Check if the product exists
				Optional<Product> optionalProduct = productsRepository
						.findById(Long.parseLong(saleOrderDAO.getProductId()));
				if (optionalProduct.isEmpty()) {
					salesRepository.delete(sale);
					return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
				}

				// Get the product
				Product product = optionalProduct.get();

				// Update the product quantity is applicable
				int productRes = productsRepository.decreaseQuantity(product.getId(), saleOrderDAO.getQuantity());
				// Save the SaleOrder if applicable
				if (productRes == 1) {
					SaleOrder saleOrder = new SaleOrder();
					saleOrder.setSale(sale);
					saleOrder.setProduct(product);
					saleOrder.setPrice(saleOrderDAO.getProductPrice() == null ? product.getPrice()
							: saleOrderDAO.getProductPrice());
					saleOrder.setQuantity(saleOrderDAO.getQuantity());
					saleOrdersRepository.save(saleOrder);
					sale.getSaleOrder().add(saleOrder);
				} else {
					// Delete the new sale operation and its all related orders if one of the
					// products does not have enough quantity
					salesRepository.delete(sale);
					LOG.error(String.format("Not enough quantity for product: Id: %s, Name: %s",
							product.getId().toString(), product.getName()));
					return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
				}
			}
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch (Exception e) {
			LOG.error(String.format("Error while saving the new sale opertaion. ", e.getMessage()));
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<List<SaleDAO>> getSalesOperations() {
		try {
			List<SaleDAO> result = new ArrayList<SaleDAO>();

			// Get all the sale operations
			List<Sale> sales = salesRepository.findAll();

			if (sales.isEmpty()) {
				return new ResponseEntity<List<SaleDAO>>(HttpStatus.NOT_FOUND);
			}

			// Fill in the final result
			for (Sale sale : sales) {
				float totalAmount = 0F;
				SaleDAO saleDAO = new SaleDAO();
				saleDAO.setId(sale.getId().toString());
				saleDAO.setCreationDate(sale.getCreationDate().toLocalDate());
				saleDAO.setClient(sale.getClient().getId().toString());
				saleDAO.setSeller(sale.getSeller().getId().toString());
				List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
				for (SaleOrder saleOrder : sale.getSaleOrder()) {
					OrderDetail orderDetail = new OrderDetail();
					orderDetail.setId(saleOrder.getId().toString());
					orderDetail.setSaleId(sale.getId().toString());
					orderDetail.setProductId(saleOrder.getProduct().getId().toString());
					orderDetail.setProductPrice(saleOrder.getPrice());
					orderDetail.setQuantity(saleOrder.getQuantity());
					totalAmount += orderDetail.getQuantity() * orderDetail.getProductPrice();
					orderDetails.add(orderDetail);
				}
				saleDAO.setOrder(orderDetails);
				saleDAO.setTotal(totalAmount);
				result.add(saleDAO);
			}
			return new ResponseEntity<List<SaleDAO>>(result, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error(String.format("Error while saving the new sale opertaion. ", e.getMessage()));
			return new ResponseEntity<List<SaleDAO>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	@Transactional
	public ResponseEntity<Void> updateSaleOperation(Integer saleId, @Valid SaleDAO saleDAO) {
		if (!salesRepository.existsById(Long.valueOf(saleId))) {
			LOG.error("Error while updating the sale opertaion because it could not be found");
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
		// Loop over the sale order details
		for (OrderDetail saleOrderDAO : saleDAO.getOrder()) {
			if (!productsRepository.existsById(Long.parseLong(saleOrderDAO.getProductId()))) {
				LOG.error("Error while updating the sale opertaion because a product could not be found");
				return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
			}
			if (!saleOrdersRepository.existsById(Long.parseLong(saleOrderDAO.getId()))) {
				LOG.error("Error while updating the sale opertaion because a sale order could not be found");
				return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
			}
			// Get the product
			Product product = productsRepository.findById(Long.parseLong(saleOrderDAO.getProductId())).get();
			// Get the sale order
			SaleOrder saleOrder = saleOrdersRepository.findById(Long.parseLong(saleOrderDAO.getId())).get();

			// Continue if the quantity and the price is both the same
			if (saleOrderDAO.getQuantity() == saleOrder.getQuantity()) {
				if (saleOrderDAO.getProductPrice() == saleOrder.getPrice()) {
					continue;
				}
			}

			int productRes = 0;
			// Check if the new quantity is less than the old one
			if (saleOrderDAO.getQuantity() >= saleOrder.getQuantity()) {
				productRes = productsRepository.decreaseQuantity(product.getId(),
						(int) saleOrderDAO.getQuantity() - (int) saleOrder.getQuantity());
			} else {
				productRes = productsRepository.increaseQuantity(product.getId(),
						(int) saleOrder.getQuantity() - (int) saleOrderDAO.getQuantity());
			}

			// Save the SaleOrder if applicable
			if (productRes == 1) {
				SaleUpdateLog saleOrderLog = new SaleUpdateLog();
				saleOrderLog.setOldQuantity(saleOrder.getQuantity());
				saleOrderLog.setNewQuantity(saleOrderDAO.getQuantity());
				saleOrderLog.setOldPrice(saleOrder.getPrice());
				saleOrderLog.setNewPrice(
						saleOrderDAO.getProductPrice() == null ? saleOrder.getPrice() : saleOrderDAO.getProductPrice());
				saleOrderLog.setDateCreated(Date.valueOf(LocalDate.now()));

				saleOrder.setPrice(
						saleOrderDAO.getProductPrice() == null ? saleOrder.getPrice() : saleOrderDAO.getProductPrice());
				saleOrder.setQuantity(saleOrderDAO.getQuantity());
				saleOrdersRepository.save(saleOrder);

				saleOrderLog.setSaleOrder(saleOrder);
				saleUpdateLogsRepository.save(saleOrderLog);
			} else {
				return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
			}
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

}

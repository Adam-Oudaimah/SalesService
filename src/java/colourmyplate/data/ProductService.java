package colourmyplate.data;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import colourmyplate.data.client.ClientRepository;
import colourmyplate.data.product.Product;
import colourmyplate.data.product.ProductRepository;
import colourmyplate.data.rest.server.v1.api.ProductsApi;
import colourmyplate.data.rest.server.v1.model.ProductDAO;

@RestController
@RequestMapping("/v1")
public class ProductService implements ProductsApi {

	// The logger object
	private static Logger LOG = LoggerFactory.getLogger(ProductService.class);

	@Autowired
	ProductRepository productsRepository;

	@Autowired
	ClientRepository clientsRepository;

	@Override
	@Transactional
	public ResponseEntity<Void> createProduct(@Valid ProductDAO productDAO) {
		try {
			Product product = productsRepository.save(new Product(null, productDAO.getName(), productDAO.getQuantity(),
					productDAO.getPrice(), productDAO.getDescription(), productDAO.getCategory(),
					Date.valueOf(productDAO.getCreationDate()),
					clientsRepository.findById(Long.parseLong(productDAO.getOwnerId())).get()));
			if (product.getId() != null) {
				LOG.info("New product saved successfully");
				return new ResponseEntity<Void>(HttpStatus.OK);
			} else {
				LOG.error("Error while saving the new product.");
				return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			LOG.error(String.format("Error while saving the new product. ", e.getMessage()));
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<List<ProductDAO>> getProducts() {
		try {
			// Final result
			List<ProductDAO> result = new ArrayList<ProductDAO>();

			// Query result
			List<Product> products = productsRepository.findAll();

			if (products.isEmpty()) {
				return new ResponseEntity<List<ProductDAO>>(HttpStatus.NOT_FOUND);
			}

			// Loop over the query and fill in the final result
			for (Product product : products) {
				ProductDAO productDAO = new ProductDAO();
				productDAO.setId(product.getId().toString());
				productDAO.setName(product.getName());
				productDAO.setDescription(product.getDescription());
				productDAO.setQuantity(product.getQuantity());
				productDAO.setPrice(product.getPrice());
				productDAO.setCategory(product.getCategory());
				productDAO.setCreationDate(product.getCreationDate().toLocalDate());
				productDAO.setOwnerId(product.getOwner().getId().toString());
				result.add(productDAO);
			}

			LOG.info("All products fetched successfully");
			return new ResponseEntity<List<ProductDAO>>(result, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error(String.format("Error while saving the new product. ", e.getMessage()));
			return new ResponseEntity<List<ProductDAO>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	@Transactional
	public ResponseEntity<Void> updateProduct(Integer productId, @Valid ProductDAO productDAO) {
		try {
			if (!productsRepository.existsById(Long.valueOf(productId))) {
				return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
			}
			Product product = productsRepository.findById(Long.parseLong(productDAO.getId())).get();
			product.setName(productDAO.getName());
			product.setQuantity(productDAO.getQuantity());
			product.setPrice(productDAO.getPrice());
			product.setDescription(productDAO.getDescription());
			product.setCategory(productDAO.getCategory());
			productsRepository.saveAndFlush(product);
			LOG.info("New product saved successfully");
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch (Exception e) {
			LOG.error(String.format("Error while saving the new product. ", e.getMessage()));
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}

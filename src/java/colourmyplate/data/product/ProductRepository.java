package colourmyplate.data.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@Transactional
	@Modifying
	@Query(value = "UPDATE products SET quantity=quantity-:quantity WHERE id=:id AND quantity-:quantity>=0", nativeQuery = true)
	public int decreaseQuantity(@Param("id") Long id, @Param("quantity") int quantity);

	@Transactional
	@Modifying
	@Query(value = "UPDATE products SET quantity=quantity+:quantity WHERE id=:id", nativeQuery = true)
	public int increaseQuantity(@Param("id") Long id, @Param("quantity") int quantity);
}

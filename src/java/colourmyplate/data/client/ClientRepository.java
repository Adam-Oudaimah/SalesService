package colourmyplate.data.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ClientRepository extends JpaRepository<Client, Long> {

	@Transactional
	@Modifying
	@Query(value = "UPDATE clients SET balance=balance-:balance WHERE id=:id AND balance-:balance>=0", nativeQuery = true)
	public int updateBalance(@Param("id") Long id, @Param("balance") float balance);

}

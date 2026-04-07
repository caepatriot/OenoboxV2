package lu.caepatriot.oenobox.inventory.repository;

import lu.caepatriot.oenobox.inventory.entity.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
}

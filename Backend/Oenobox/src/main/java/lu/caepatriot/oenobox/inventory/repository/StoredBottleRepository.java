package lu.caepatriot.oenobox.inventory.repository;

import lu.caepatriot.oenobox.inventory.entity.StoredBottle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoredBottleRepository extends JpaRepository<StoredBottle, Long> {
    java.util.List<StoredBottle> findByAcquisitionLotItemIdAndStatusOrderByIdAsc(Long acquisitionLotItemId, String status);
}

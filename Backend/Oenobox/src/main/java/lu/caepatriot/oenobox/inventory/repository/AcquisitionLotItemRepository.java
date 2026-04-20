package lu.caepatriot.oenobox.inventory.repository;

import lu.caepatriot.oenobox.inventory.entity.AcquisitionLotItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcquisitionLotItemRepository extends JpaRepository<AcquisitionLotItem, Long> {
    List<AcquisitionLotItem> findByAcquisitionLotIdOrderByIdAsc(Long acquisitionLotId);
}

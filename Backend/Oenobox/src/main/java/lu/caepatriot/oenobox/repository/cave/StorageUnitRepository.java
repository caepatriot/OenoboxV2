package lu.caepatriot.oenobox.repository.cave;

import lu.caepatriot.oenobox.entity.cave.Cave;
import lu.caepatriot.oenobox.entity.cave.StorageUnit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageUnitRepository extends JpaRepository<StorageUnit, Long> {
}

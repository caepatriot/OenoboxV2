package lu.caepatriot.oenobox.cellar.repository;

import lu.caepatriot.oenobox.cellar.entity.StorageUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageUnitRepository extends JpaRepository<StorageUnit, Long> {
    List<StorageUnit> findByCaveIdOrderByDisplayOrderAscIdAsc(Long caveId);
}

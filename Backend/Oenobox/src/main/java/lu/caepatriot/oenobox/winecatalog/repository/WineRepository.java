package lu.caepatriot.oenobox.winecatalog.repository;

import lu.caepatriot.oenobox.winecatalog.entity.Wine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WineRepository extends JpaRepository<Wine, Long> {
    List<Wine> findByNameContainingIgnoreCaseOrRegionContainingIgnoreCaseOrderByNameAsc(String name, String region);
}

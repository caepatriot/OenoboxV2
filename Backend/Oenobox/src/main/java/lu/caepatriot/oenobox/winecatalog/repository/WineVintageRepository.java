package lu.caepatriot.oenobox.winecatalog.repository;

import lu.caepatriot.oenobox.winecatalog.entity.WineVintage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface WineVintageRepository extends JpaRepository<WineVintage, Long> {
    List<WineVintage> findByWineIdIn(Collection<Long> wineIds);
}

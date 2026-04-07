package lu.caepatriot.oenobox.winecatalog.repository;

import lu.caepatriot.oenobox.winecatalog.entity.WineVintage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WineVintageRepository extends JpaRepository<WineVintage, Long> {
}

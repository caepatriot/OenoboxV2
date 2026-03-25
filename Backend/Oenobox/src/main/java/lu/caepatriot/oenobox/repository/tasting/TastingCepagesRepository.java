package lu.caepatriot.oenobox.repository.tasting;

import lu.caepatriot.oenobox.entity.tasting.TastingCepages;
import lu.caepatriot.oenobox.entity.tasting.Tasting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TastingCepagesRepository extends JpaRepository<TastingCepages, Long> {
    List<TastingCepages> findByTastingId(Long tastingId);
}

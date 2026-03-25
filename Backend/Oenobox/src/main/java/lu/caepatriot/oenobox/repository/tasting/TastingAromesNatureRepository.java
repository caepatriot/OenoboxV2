package lu.caepatriot.oenobox.repository.tasting;

import lu.caepatriot.oenobox.entity.tasting.TastingAromesNatureId;
import lu.caepatriot.oenobox.entity.tasting.TastingAromesNature;
import lu.caepatriot.oenobox.entity.tasting.Tasting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TastingAromesNatureRepository extends JpaRepository<TastingAromesNature, TastingAromesNatureId> {
    List<TastingAromesNature> findByIdTastingId(Long tastingId);
}

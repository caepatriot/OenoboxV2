package lu.caepatriot.oenobox.tasting.repository;

import lu.caepatriot.oenobox.tasting.entity.TastingAromesNatureId;
import lu.caepatriot.oenobox.tasting.entity.TastingAromesNature;
import lu.caepatriot.oenobox.tasting.entity.Tasting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TastingAromesNatureRepository extends JpaRepository<TastingAromesNature, TastingAromesNatureId> {
    List<TastingAromesNature> findByIdTastingId(Long tastingId);
}


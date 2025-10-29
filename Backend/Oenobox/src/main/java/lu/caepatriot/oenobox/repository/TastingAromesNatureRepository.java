package lu.caepatriot.oenobox.repository;

import lu.caepatriot.oenobox.entity.TastingAromesNature;
import lu.caepatriot.oenobox.entity.TastingAromesNatureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TastingAromesNatureRepository extends JpaRepository<TastingAromesNature, TastingAromesNatureId> {
    List<TastingAromesNature> findByIdTastingId(Long tastingId);
}
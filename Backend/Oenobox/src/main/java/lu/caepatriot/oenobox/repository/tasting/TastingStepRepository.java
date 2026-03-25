package lu.caepatriot.oenobox.repository.tasting;

import lu.caepatriot.oenobox.entity.tasting.Tasting;
import lu.caepatriot.oenobox.entity.tasting.TastingStep;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TastingStepRepository extends JpaRepository<TastingStep, Long> {
    TastingStep findByName(String name);
}

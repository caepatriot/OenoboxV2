package lu.caepatriot.oenobox.tasting.repository;

import lu.caepatriot.oenobox.tasting.entity.Tasting;
import lu.caepatriot.oenobox.tasting.entity.TastingStep;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TastingStepRepository extends JpaRepository<TastingStep, Long> {
    TastingStep findByName(String name);
}


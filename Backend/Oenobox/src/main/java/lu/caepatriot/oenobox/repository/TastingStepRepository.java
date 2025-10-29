package lu.caepatriot.oenobox.repository;

import lu.caepatriot.oenobox.entity.TastingStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TastingStepRepository extends JpaRepository<TastingStep, Long> {
    TastingStep findByName(String name);
}
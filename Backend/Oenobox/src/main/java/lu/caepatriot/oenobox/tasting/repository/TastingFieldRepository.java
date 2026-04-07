package lu.caepatriot.oenobox.tasting.repository;

import lu.caepatriot.oenobox.winecatalog.entity.Wine;

import lu.caepatriot.oenobox.winecatalog.entity.WineType;
import lu.caepatriot.oenobox.tasting.entity.Tasting;
import lu.caepatriot.oenobox.tasting.entity.TastingField;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TastingFieldRepository extends JpaRepository<TastingField, Long> {
    List<TastingField> findByStepIdOrderById(Long stepId);

    @Query("SELECT f FROM TastingField f LEFT JOIN FETCH f.options WHERE f.step.id = :stepId ORDER BY f.id")
    List<TastingField> findByStepIdWithOptions(@Param("stepId") Long stepId);

    @Query(value = "SELECT f.* FROM tasting_field f LEFT JOIN tasting_field_option o ON o.field_id = f.id WHERE f.wine_type_restriction::text LIKE '%' || :wineType || '%' ORDER BY f.step_id, f.id", nativeQuery = true)
    List<TastingField> findByWineTypeRestrictionContaining(@Param("wineType") String wineType);

}


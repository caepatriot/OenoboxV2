package lu.caepatriot.oenobox.repository;

import lu.caepatriot.oenobox.entity.TastingFieldOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TastingFieldOptionRepository extends JpaRepository<TastingFieldOption, Long> {
    List<TastingFieldOption> findByFieldIdOrderBySortOrder(Long fieldId);

    @Query("SELECT o FROM TastingFieldOption o LEFT JOIN FETCH o.aromaNotes WHERE o.field.id = :fieldId ORDER BY o.sortOrder")
    List<TastingFieldOption> findByFieldIdWithAromaNotes(@Param("fieldId") Long fieldId);

    @Query(value = "SELECT o.* FROM tasting_field_option o LEFT JOIN tasting_field_option_aroma_notes ta ON ta.tasting_field_option_id = o.id LEFT JOIN aroma_note a ON a.id = ta.aroma_notes_id WHERE o.field_id = :fieldId AND (o.wine_type_restriction IS NULL OR o.wine_type_restriction::text LIKE '%' || :wineType || '%') ORDER BY o.sort_order", nativeQuery = true)
    List<TastingFieldOption> findByFieldIdAndWineTypeWithAromaNotes(@Param("fieldId") Long fieldId, @Param("wineType") String wineType);
}
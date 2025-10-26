package lu.caepatriot.oenobox.repository;

import lu.caepatriot.oenobox.entity.Tasting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TastingRepository extends JpaRepository<Tasting, Long> {

    // Find tastings by wine type
    List<Tasting> findByWineType(String wineType);

    // Find tastings by region
    List<Tasting> findByRegion(String region);

    // Find tastings by cepage
    @Query("SELECT t FROM Tasting t WHERE :cepage MEMBER OF t.cepages")
    List<Tasting> findByCepage(@Param("cepage") String cepage);

    // Find tastings created after a certain date
    List<Tasting> findByCreatedAtAfter(LocalDateTime date);

    // Find tastings with specific note finale
    List<Tasting> findByNoteFinale(String noteFinale);

    // Find tastings by price range
    List<Tasting> findByPrixActuelBetween(Double minPrice, Double maxPrice);

    // Count tastings by wine type
    @Query("SELECT COUNT(t) FROM Tasting t WHERE t.wineType = :wineType")
    Long countByWineType(@Param("wineType") String wineType);

    // Find recent tastings
    @Query("SELECT t FROM Tasting t ORDER BY t.createdAt DESC")
    List<Tasting> findRecentTastings();

    // Find tastings with specific aromas - simplified query
    @Query("SELECT t FROM Tasting t WHERE t.aromesNature IS NOT NULL")
    List<Tasting> findByAromeNote(@Param("category") String category, @Param("note") String note);
}
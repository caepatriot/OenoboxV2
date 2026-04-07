package lu.caepatriot.oenobox.winecatalog.repository;

import lu.caepatriot.oenobox.winecatalog.entity.Wine;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WineRepository extends JpaRepository<Wine, Long> {

    @Query("""
        SELECT DISTINCT w
        FROM Wine w
        LEFT JOIN w.wineType wt
        LEFT JOIN w.cepages c
        WHERE (
                CAST(:query AS string) IS NULL
                OR LOWER(w.name) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%'))
                OR LOWER(COALESCE(w.region, '')) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%'))
                OR LOWER(COALESCE(w.producer, '')) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%'))
                OR LOWER(COALESCE(w.country, '')) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%'))
                OR LOWER(COALESCE(w.appellation, '')) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%'))
                OR LOWER(COALESCE(wt.name, '')) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%'))
                OR LOWER(COALESCE(c.name, '')) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%'))
            )
          AND (
                CAST(:region AS string) IS NULL
                OR LOWER(COALESCE(w.region, '')) = LOWER(CAST(:region AS string))
            )
          AND (
                CAST(:country AS string) IS NULL
                OR LOWER(COALESCE(w.country, '')) = LOWER(CAST(:country AS string))
            )
          AND (
                CAST(:producer AS string) IS NULL
                OR LOWER(COALESCE(w.producer, '')) = LOWER(CAST(:producer AS string))
            )
          AND (
                CAST(:wineType AS string) IS NULL
                OR LOWER(COALESCE(wt.name, '')) = LOWER(CAST(:wineType AS string))
            )
        ORDER BY w.name ASC
        """)
    List<Wine> searchCatalog(
            @Param("query") String query,
            @Param("region") String region,
            @Param("country") String country,
            @Param("producer") String producer,
            @Param("wineType") String wineType
    );

    @Query("""
        SELECT w
        FROM Wine w
        WHERE LOWER(w.name) = LOWER(:name)
          AND (:year IS NULL OR w.year = :year)
          AND (CAST(:region AS string) IS NULL OR LOWER(w.region) = LOWER(CAST(:region AS string)))
        ORDER BY w.id ASC
        """)
    List<Wine> findPotentialDuplicates(@Param("name") String name, @Param("year") Integer year, @Param("region") String region);
}


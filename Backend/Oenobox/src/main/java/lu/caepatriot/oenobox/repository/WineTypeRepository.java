package lu.caepatriot.oenobox.repository;

import lu.caepatriot.oenobox.entity.WineType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WineTypeRepository extends JpaRepository<WineType, Long> {

    // Find wine type by exact name
    Optional<WineType> findByName(String name);

    // Find wine types by color code
    List<WineType> findByColorCode(String colorCode);

    // Find wine types by name containing (case-insensitive)
    @Query("SELECT wt FROM WineType wt WHERE LOWER(wt.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<WineType> findByNameContainingIgnoreCase(@Param("name") String name);

    // Find wine types by color code containing
    @Query("SELECT wt FROM WineType wt WHERE LOWER(wt.colorCode) LIKE LOWER(CONCAT('%', :colorCode, '%'))")
    List<WineType> findByColorCodeContainingIgnoreCase(@Param("colorCode") String colorCode);
}
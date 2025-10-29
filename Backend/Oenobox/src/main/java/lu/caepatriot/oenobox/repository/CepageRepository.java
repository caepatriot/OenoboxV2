package lu.caepatriot.oenobox.repository;

import lu.caepatriot.oenobox.entity.Cepage;
import lu.caepatriot.oenobox.entity.WineType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CepageRepository extends JpaRepository<Cepage, Long> {

    /**
     * Find cepages by wine type
     * @param wineType the wine type entity
     * @return list of cepages for the given wine type
     */
    List<Cepage> findByWineType(WineType wineType);

    /**
     * Find cepages by wine type ID
     * @param wineTypeId the wine type ID
     * @return list of cepages for the given wine type ID
     */
    List<Cepage> findByWineTypeId(Long wineTypeId);

    /**
     * Find cepages by name containing (case insensitive)
     * @param name the name to search for
     * @return list of cepages containing the name
     */
    List<Cepage> findByNameContainingIgnoreCase(String name);

    /**
     * Find cepages by wine type and name containing (case insensitive)
     * @param wineType the wine type entity
     * @param name the name to search for
     * @return list of cepages for the given wine type containing the name
     */
    List<Cepage> findByWineTypeAndNameContainingIgnoreCase(WineType wineType, String name);

    /**
     * Find cepages by wine type ID and name containing (case insensitive)
     * @param wineTypeId the wine type ID
     * @param name the name to search for
     * @return list of cepages for the given wine type ID containing the name
     */
    List<Cepage> findByWineTypeIdAndNameContainingIgnoreCase(Long wineTypeId, String name);
}

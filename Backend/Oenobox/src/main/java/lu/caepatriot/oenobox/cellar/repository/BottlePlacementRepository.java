package lu.caepatriot.oenobox.cellar.repository;

import lu.caepatriot.oenobox.cellar.entity.BottlePlacement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BottlePlacementRepository extends JpaRepository<BottlePlacement, Long> {
    List<BottlePlacement> findBySpaceId(Long spaceId);
}

package lu.caepatriot.oenobox.cellar.repository;

import lu.caepatriot.oenobox.cellar.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {
    List<Space> findByUnitIdOrderByRowAscColumnAsc(Long unitId);
    List<Space> findByUnitCaveIdOrderByUnitDisplayOrderAscUnitIdAscSlotIndexAsc(Long caveId);
    List<Space> findByIdIn(List<Long> ids);
}

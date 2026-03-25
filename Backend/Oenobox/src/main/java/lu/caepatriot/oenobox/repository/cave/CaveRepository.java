package lu.caepatriot.oenobox.repository.cave;

import lu.caepatriot.oenobox.entity.cave.Cave;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaveRepository extends JpaRepository<Cave, Long> {
}

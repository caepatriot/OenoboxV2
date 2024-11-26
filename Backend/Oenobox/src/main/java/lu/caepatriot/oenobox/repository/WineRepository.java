package lu.caepatriot.oenobox.repository;

import lu.caepatriot.oenobox.entity.Wine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WineRepository extends JpaRepository<Wine, Long> {
}

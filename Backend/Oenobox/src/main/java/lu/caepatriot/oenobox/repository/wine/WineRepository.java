package lu.caepatriot.oenobox.repository.wine;

import lu.caepatriot.oenobox.entity.wine.Wine;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WineRepository extends JpaRepository<Wine, Long> {
}

package lu.caepatriot.oenobox.cellar.repository;

import lu.caepatriot.oenobox.cellar.entity.BottlePlacement;
import lu.caepatriot.oenobox.cellar.entity.Cave;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BottlePlacementRepository extends JpaRepository<BottlePlacement, Long> {
}


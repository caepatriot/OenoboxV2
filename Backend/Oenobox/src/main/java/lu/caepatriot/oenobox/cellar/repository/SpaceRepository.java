package lu.caepatriot.oenobox.cellar.repository;

import lu.caepatriot.oenobox.cellar.entity.Cave;
import lu.caepatriot.oenobox.cellar.entity.Space;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {
}


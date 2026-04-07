package lu.caepatriot.oenobox.tasting.repository;

import lu.caepatriot.oenobox.tasting.entity.TastingCepages;
import lu.caepatriot.oenobox.tasting.entity.Tasting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TastingCepagesRepository extends JpaRepository<TastingCepages, Long> {
    List<TastingCepages> findByTastingId(Long tastingId);
}


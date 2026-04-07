package lu.caepatriot.oenobox.tasting.repository;

import lu.caepatriot.oenobox.tasting.entity.Tasting;
import lu.caepatriot.oenobox.tasting.entity.AromaNote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AromaNoteRepository extends JpaRepository<AromaNote, Long> {
    List<AromaNote> findByCategoryOptionIdOrderByName(Long categoryOptionId);
}


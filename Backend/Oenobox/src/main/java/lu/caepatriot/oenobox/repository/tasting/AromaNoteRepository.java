package lu.caepatriot.oenobox.repository.tasting;

import lu.caepatriot.oenobox.entity.tasting.Tasting;
import lu.caepatriot.oenobox.entity.tasting.AromaNote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AromaNoteRepository extends JpaRepository<AromaNote, Long> {
    List<AromaNote> findByCategoryOptionIdOrderByName(Long categoryOptionId);
}

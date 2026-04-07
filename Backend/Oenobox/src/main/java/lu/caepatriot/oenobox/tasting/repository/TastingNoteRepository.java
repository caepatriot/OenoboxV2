package lu.caepatriot.oenobox.tasting.repository;

import lu.caepatriot.oenobox.tasting.entity.TastingNote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TastingNoteRepository extends JpaRepository<TastingNote, Long> {
}

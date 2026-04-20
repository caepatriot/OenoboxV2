package lu.caepatriot.oenobox.winecatalog.repository;

import lu.caepatriot.oenobox.winecatalog.entity.CatalogSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogSubmissionRepository extends JpaRepository<CatalogSubmission, Long> {
}

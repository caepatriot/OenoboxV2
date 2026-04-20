package lu.caepatriot.oenobox.winecatalog.service;

import lu.caepatriot.oenobox.winecatalog.dto.CatalogSubmissionDto;
import lu.caepatriot.oenobox.winecatalog.dto.CreateCatalogSubmissionRequest;
import lu.caepatriot.oenobox.winecatalog.entity.CatalogSubmission;
import lu.caepatriot.oenobox.winecatalog.repository.CatalogSubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogSubmissionService {
    private final CatalogSubmissionRepository catalogSubmissionRepository;

    public CatalogSubmissionService(CatalogSubmissionRepository catalogSubmissionRepository) {
        this.catalogSubmissionRepository = catalogSubmissionRepository;
    }

    @Transactional
    public CatalogSubmissionDto createSubmission(CreateCatalogSubmissionRequest request) {
        CatalogSubmission submission = new CatalogSubmission();
        submission.setRawLabelText(request.getRawLabelText());
        submission.setSubmittedImagePath(request.getSubmittedImagePath());
        submission.setProposedDataJson(request.getProposedDataJson());
        submission.setNormalizedFingerprint(request.getNormalizedFingerprint());
        submission.setSourceType(request.getSourceType());
        submission.setConfidenceScore(request.getConfidenceScore());
        submission.setSubmitter(request.getSubmitter());
        submission.setNotes(request.getNotes());
        submission.setStatus("PENDING");

        return mapToDto(catalogSubmissionRepository.save(submission));
    }

    private CatalogSubmissionDto mapToDto(CatalogSubmission submission) {
        CatalogSubmissionDto dto = new CatalogSubmissionDto();
        dto.setId(submission.getId());
        dto.setRawLabelText(submission.getRawLabelText());
        dto.setSubmittedImagePath(submission.getSubmittedImagePath());
        dto.setProposedDataJson(submission.getProposedDataJson());
        dto.setNormalizedFingerprint(submission.getNormalizedFingerprint());
        dto.setSourceType(submission.getSourceType());
        dto.setConfidenceScore(submission.getConfidenceScore());
        dto.setSubmitter(submission.getSubmitter());
        dto.setStatus(submission.getStatus());
        dto.setNotes(submission.getNotes());
        dto.setCreatedAt(submission.getCreatedAt());
        if (submission.getMergedWine() != null) {
            dto.setMergedWineId(submission.getMergedWine().getId());
        }
        if (submission.getMergedWineVintage() != null) {
            dto.setMergedWineVintageId(submission.getMergedWineVintage().getId());
        }
        return dto;
    }
}

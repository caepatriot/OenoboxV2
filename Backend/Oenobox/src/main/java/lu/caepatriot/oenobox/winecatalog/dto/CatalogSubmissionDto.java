package lu.caepatriot.oenobox.winecatalog.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CatalogSubmissionDto {
    private Long id;
    private String rawLabelText;
    private String submittedImagePath;
    private String proposedDataJson;
    private String normalizedFingerprint;
    private String sourceType;
    private BigDecimal confidenceScore;
    private String submitter;
    private String status;
    private Long mergedWineId;
    private Long mergedWineVintageId;
    private String notes;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRawLabelText() { return rawLabelText; }
    public void setRawLabelText(String rawLabelText) { this.rawLabelText = rawLabelText; }

    public String getSubmittedImagePath() { return submittedImagePath; }
    public void setSubmittedImagePath(String submittedImagePath) { this.submittedImagePath = submittedImagePath; }

    public String getProposedDataJson() { return proposedDataJson; }
    public void setProposedDataJson(String proposedDataJson) { this.proposedDataJson = proposedDataJson; }

    public String getNormalizedFingerprint() { return normalizedFingerprint; }
    public void setNormalizedFingerprint(String normalizedFingerprint) { this.normalizedFingerprint = normalizedFingerprint; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public BigDecimal getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(BigDecimal confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getSubmitter() { return submitter; }
    public void setSubmitter(String submitter) { this.submitter = submitter; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getMergedWineId() { return mergedWineId; }
    public void setMergedWineId(Long mergedWineId) { this.mergedWineId = mergedWineId; }

    public Long getMergedWineVintageId() { return mergedWineVintageId; }
    public void setMergedWineVintageId(Long mergedWineVintageId) { this.mergedWineVintageId = mergedWineVintageId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

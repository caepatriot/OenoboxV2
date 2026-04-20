package lu.caepatriot.oenobox.winecatalog.dto;

import java.math.BigDecimal;

public class CreateCatalogSubmissionRequest {
    private String rawLabelText;
    private String submittedImagePath;
    private String proposedDataJson;
    private String normalizedFingerprint;
    private String sourceType;
    private BigDecimal confidenceScore;
    private String submitter;
    private String notes;

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

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

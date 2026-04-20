package lu.caepatriot.oenobox.winecatalog.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "catalog_submissions")
public class CatalogSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "raw_label_text")
    private String rawLabelText;

    @Column(name = "submitted_image_path")
    private String submittedImagePath;

    @Column(name = "proposed_data_json", columnDefinition = "TEXT")
    private String proposedDataJson;

    @Column(name = "normalized_fingerprint")
    private String normalizedFingerprint;

    @Column(name = "source_type")
    private String sourceType;

    @Column(name = "confidence_score", precision = 5, scale = 2)
    private BigDecimal confidenceScore;

    private String submitter;

    @Column(nullable = false)
    private String status = "PENDING";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merged_wine_id")
    private Wine mergedWine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merged_wine_vintage_id")
    private WineVintage mergedWineVintage;

    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

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

    public Wine getMergedWine() { return mergedWine; }
    public void setMergedWine(Wine mergedWine) { this.mergedWine = mergedWine; }

    public WineVintage getMergedWineVintage() { return mergedWineVintage; }
    public void setMergedWineVintage(WineVintage mergedWineVintage) { this.mergedWineVintage = mergedWineVintage; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

package lu.caepatriot.oenobox.inventory.dto;

import java.util.ArrayList;
import java.util.List;

public class InventoryOcrScanResponse {
    private String provider;
    private String rawText;
    private List<String> lines = new ArrayList<>();
    private String suggestedQuery;
    private Integer detectedVintage;
    private Double detectedAlcoholPercent;
    private Integer detectedVolumeMl;
    private String detectedAppellation;
    private List<InventoryOcrCandidateDto> extractedFields = new ArrayList<>();
    private List<IntakeSearchResultDto> candidates = new ArrayList<>();
    private Boolean needsUserConfirmation = Boolean.TRUE;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public String getSuggestedQuery() {
        return suggestedQuery;
    }

    public void setSuggestedQuery(String suggestedQuery) {
        this.suggestedQuery = suggestedQuery;
    }

    public Integer getDetectedVintage() {
        return detectedVintage;
    }

    public void setDetectedVintage(Integer detectedVintage) {
        this.detectedVintage = detectedVintage;
    }

    public Double getDetectedAlcoholPercent() {
        return detectedAlcoholPercent;
    }

    public void setDetectedAlcoholPercent(Double detectedAlcoholPercent) {
        this.detectedAlcoholPercent = detectedAlcoholPercent;
    }

    public Integer getDetectedVolumeMl() {
        return detectedVolumeMl;
    }

    public void setDetectedVolumeMl(Integer detectedVolumeMl) {
        this.detectedVolumeMl = detectedVolumeMl;
    }

    public String getDetectedAppellation() {
        return detectedAppellation;
    }

    public void setDetectedAppellation(String detectedAppellation) {
        this.detectedAppellation = detectedAppellation;
    }

    public List<InventoryOcrCandidateDto> getExtractedFields() {
        return extractedFields;
    }

    public void setExtractedFields(List<InventoryOcrCandidateDto> extractedFields) {
        this.extractedFields = extractedFields;
    }

    public List<IntakeSearchResultDto> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<IntakeSearchResultDto> candidates) {
        this.candidates = candidates;
    }

    public Boolean getNeedsUserConfirmation() {
        return needsUserConfirmation;
    }

    public void setNeedsUserConfirmation(Boolean needsUserConfirmation) {
        this.needsUserConfirmation = needsUserConfirmation;
    }
}

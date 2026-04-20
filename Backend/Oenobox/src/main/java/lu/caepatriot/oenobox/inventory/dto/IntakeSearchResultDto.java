package lu.caepatriot.oenobox.inventory.dto;

public class IntakeSearchResultDto {
    private String label;
    private String producer;
    private Integer vintage;
    private String appellation;
    private String region;
    private String country;
    private String wineType;
    private String image;
    private String source;
    private Double confidence;
    private String sourceReference;
    private Long wineId;
    private Long wineVintageId;
    private Boolean existsLocally;

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getProducer() { return producer; }
    public void setProducer(String producer) { this.producer = producer; }

    public Integer getVintage() { return vintage; }
    public void setVintage(Integer vintage) { this.vintage = vintage; }

    public String getAppellation() { return appellation; }
    public void setAppellation(String appellation) { this.appellation = appellation; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getWineType() { return wineType; }
    public void setWineType(String wineType) { this.wineType = wineType; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public String getSourceReference() { return sourceReference; }
    public void setSourceReference(String sourceReference) { this.sourceReference = sourceReference; }

    public Long getWineId() { return wineId; }
    public void setWineId(Long wineId) { this.wineId = wineId; }

    public Long getWineVintageId() { return wineVintageId; }
    public void setWineVintageId(Long wineVintageId) { this.wineVintageId = wineVintageId; }

    public Boolean getExistsLocally() { return existsLocally; }
    public void setExistsLocally(Boolean existsLocally) { this.existsLocally = existsLocally; }
}

package lu.caepatriot.oenobox.winecatalog.dto;

import lu.caepatriot.oenobox.winecatalog.entity.Wine;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WineDto {
    private Long id;
    private String name;
    private String wineTypeName;
    private List<String> cepages;
    private String region;
    private Integer year;
    private String producer;
    private String country;
    private String appellation;
    private String description;
    private List<String> aromaNotes;
    private List<String> foodPairings;
    private String imageUrl;
    private String dataSource;
    private String externalId;

    public WineDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWineTypeName() {
        return wineTypeName;
    }

    public void setWineTypeName(String wineTypeName) {
        this.wineTypeName = wineTypeName;
    }

    public List<String> getCepages() {
        return cepages;
    }

    public void setCepages(List<String> cepages) {
        this.cepages = cepages;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAppellation() {
        return appellation;
    }

    public void setAppellation(String appellation) {
        this.appellation = appellation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAromaNotes() {
        return aromaNotes;
    }

    public void setAromaNotes(List<String> aromaNotes) {
        this.aromaNotes = aromaNotes;
    }

    public List<String> getFoodPairings() {
        return foodPairings;
    }

    public void setFoodPairings(List<String> foodPairings) {
        this.foodPairings = foodPairings;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}


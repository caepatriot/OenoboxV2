package lu.caepatriot.oenobox.winecatalog.dto;

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
    private String imageUrl;

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

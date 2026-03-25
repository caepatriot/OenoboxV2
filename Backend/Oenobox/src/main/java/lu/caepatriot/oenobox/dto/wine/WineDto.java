package lu.caepatriot.oenobox.dto.wine;

import lu.caepatriot.oenobox.entity.wine.Wine;

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
}

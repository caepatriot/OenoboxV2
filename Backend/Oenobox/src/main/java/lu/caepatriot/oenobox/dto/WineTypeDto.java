package lu.caepatriot.oenobox.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WineTypeDto {

    private Long id;
    private String name;
    private String colorCode;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CepageDto> cepages;

    public WineTypeDto() {
    }

    public WineTypeDto(Long id, String name, String colorCode, String description,
                      LocalDateTime createdAt, LocalDateTime updatedAt, List<CepageDto> cepages) {
        this.id = id;
        this.name = name;
        this.colorCode = colorCode;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.cepages = cepages;
    }

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

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<CepageDto> getCepages() {
        return cepages;
    }

    public void setCepages(List<CepageDto> cepages) {
        this.cepages = cepages;
    }
}
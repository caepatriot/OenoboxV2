package lu.caepatriot.oenobox.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TastingFieldOptionDto {
    private Long id;
    private TastingFieldDto field;
    private String value;
    private String label;
    private String colorCode;
    private Boolean isNegative;
    private Integer sortOrder;
    private String icon;
    private List<String> wineTypeRestriction;
    private String searchTerm;
    private List<String> filterWineTypes;
    private Boolean filterNegative;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AromaNoteDto> aromaNotes;

    // Constructors
    public TastingFieldOptionDto() {}

    public TastingFieldOptionDto(Long id, TastingFieldDto field, String value, String label, String colorCode,
                                Boolean isNegative, Integer sortOrder, String icon, List<String> wineTypeRestriction,
                                LocalDateTime createdAt, LocalDateTime updatedAt, List<AromaNoteDto> aromaNotes,
                                String searchTerm, List<String> filterWineTypes, Boolean filterNegative) {
        this.id = id;
        this.field = field;
        this.value = value;
        this.label = label;
        this.colorCode = colorCode;
        this.isNegative = isNegative;
        this.sortOrder = sortOrder;
        this.icon = icon;
        this.wineTypeRestriction = wineTypeRestriction;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.aromaNotes = aromaNotes;
        this.searchTerm = searchTerm;
        this.filterWineTypes = filterWineTypes;
        this.filterNegative = filterNegative;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TastingFieldDto getField() { return field; }
    public void setField(TastingFieldDto field) { this.field = field; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getColorCode() { return colorCode; }
    public void setColorCode(String colorCode) { this.colorCode = colorCode; }

    public Boolean getIsNegative() { return isNegative; }
    public void setIsNegative(Boolean isNegative) { this.isNegative = isNegative; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public List<String> getWineTypeRestriction() { return wineTypeRestriction; }
    public void setWineTypeRestriction(List<String> wineTypeRestriction) { this.wineTypeRestriction = wineTypeRestriction; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<AromaNoteDto> getAromaNotes() { return aromaNotes; }
    public void setAromaNotes(List<AromaNoteDto> aromaNotes) { this.aromaNotes = aromaNotes; }

    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }

    public List<String> getFilterWineTypes() { return filterWineTypes; }
    public void setFilterWineTypes(List<String> filterWineTypes) { this.filterWineTypes = filterWineTypes; }

    public Boolean getFilterNegative() { return filterNegative; }
    public void setFilterNegative(Boolean filterNegative) { this.filterNegative = filterNegative; }
}
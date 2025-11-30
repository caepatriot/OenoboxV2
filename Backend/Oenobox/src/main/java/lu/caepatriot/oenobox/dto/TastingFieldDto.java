package lu.caepatriot.oenobox.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TastingFieldDto {
    private Long id;
    private TastingStepDto step;
    private String fieldType;
    private String name;
    private String label;
    private String placeholder;
    private Boolean required;
    private Boolean multiSelect;
    private List<String> wineTypeRestriction;
    private String searchTerm;
    private List<String> filterWineTypes;
    private Boolean filterRequired;
    private Boolean filterMultiSelect;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TastingFieldOptionDto> options;
    private List<TastingFieldGroupDto> groups;

    // Constructors
    public TastingFieldDto() {}

    public TastingFieldDto(Long id, TastingStepDto step, String fieldType, String name, String label,
                          String placeholder, Boolean required, Boolean multiSelect, List<String> wineTypeRestriction,
                          LocalDateTime createdAt, LocalDateTime updatedAt, List<TastingFieldOptionDto> options,
                          String searchTerm, List<String> filterWineTypes, Boolean filterRequired, Boolean filterMultiSelect,
                          List<TastingFieldGroupDto> groups) {
        this.id = id;
        this.step = step;
        this.fieldType = fieldType;
        this.name = name;
        this.label = label;
        this.placeholder = placeholder;
        this.required = required;
        this.multiSelect = multiSelect;
        this.wineTypeRestriction = wineTypeRestriction;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.options = options;
        this.searchTerm = searchTerm;
        this.filterWineTypes = filterWineTypes;
        this.filterRequired = filterRequired;
        this.filterMultiSelect = filterMultiSelect;
        this.groups = groups;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TastingStepDto getStep() { return step; }
    public void setStep(TastingStepDto step) { this.step = step; }

    public String getFieldType() { return fieldType; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getPlaceholder() { return placeholder; }
    public void setPlaceholder(String placeholder) { this.placeholder = placeholder; }

    public Boolean getRequired() { return required; }
    public void setRequired(Boolean required) { this.required = required; }

    public Boolean getMultiSelect() { return multiSelect; }
    public void setMultiSelect(Boolean multiSelect) { this.multiSelect = multiSelect; }

    public List<String> getWineTypeRestriction() { return wineTypeRestriction; }
    public void setWineTypeRestriction(List<String> wineTypeRestriction) { this.wineTypeRestriction = wineTypeRestriction; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<TastingFieldOptionDto> getOptions() { return options; }
    public void setOptions(List<TastingFieldOptionDto> options) { this.options = options; }

    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }

    public List<String> getFilterWineTypes() { return filterWineTypes; }
    public void setFilterWineTypes(List<String> filterWineTypes) { this.filterWineTypes = filterWineTypes; }

    public Boolean getFilterRequired() { return filterRequired; }
    public void setFilterRequired(Boolean filterRequired) { this.filterRequired = filterRequired; }

    public Boolean getFilterMultiSelect() { return filterMultiSelect; }
    public void setFilterMultiSelect(Boolean filterMultiSelect) { this.filterMultiSelect = filterMultiSelect; }

    public List<TastingFieldGroupDto> getGroups() { return groups; }
    public void setGroups(List<TastingFieldGroupDto> groups) { this.groups = groups; }
}
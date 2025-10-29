package lu.caepatriot.oenobox.dto;

import java.util.List;

public class TastingFieldGroupDto {
    private Long id;
    private String type;
    private List<String> wineType;
    private List<TastingFieldOptionDto> groupValues;
    private Boolean multi;
    private Boolean required;

    // Constructors
    public TastingFieldGroupDto() {}

    public TastingFieldGroupDto(Long id, String type, List<String> wineType,
                               List<TastingFieldOptionDto> groupValues, Boolean multi, Boolean required) {
        this.id = id;
        this.type = type;
        this.wineType = wineType;
        this.groupValues = groupValues;
        this.multi = multi;
        this.required = required;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public List<String> getWineType() { return wineType; }
    public void setWineType(List<String> wineType) { this.wineType = wineType; }

    public List<TastingFieldOptionDto> getGroupValues() { return groupValues; }
    public void setGroupValues(List<TastingFieldOptionDto> groupValues) { this.groupValues = groupValues; }

    public Boolean getMulti() { return multi; }
    public void setMulti(Boolean multi) { this.multi = multi; }

    public Boolean getRequired() { return required; }
    public void setRequired(Boolean required) { this.required = required; }
}
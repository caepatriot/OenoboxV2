package lu.caepatriot.oenobox.cellar.dto;

import java.util.List;

public class SpaceBulkUpdateRequestDto {
    private List<Long> spaceIds;
    private String slotLabelPrefix;
    private String preferredWineGroup;
    private String sectionName;
    private Integer capacity;
    private Boolean disabled;
    private String notes;

    public List<Long> getSpaceIds() {
        return spaceIds;
    }

    public void setSpaceIds(List<Long> spaceIds) {
        this.spaceIds = spaceIds;
    }

    public String getSlotLabelPrefix() {
        return slotLabelPrefix;
    }

    public void setSlotLabelPrefix(String slotLabelPrefix) {
        this.slotLabelPrefix = slotLabelPrefix;
    }

    public String getPreferredWineGroup() {
        return preferredWineGroup;
    }

    public void setPreferredWineGroup(String preferredWineGroup) {
        this.preferredWineGroup = preferredWineGroup;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

package lu.caepatriot.oenobox.cellar.dto;

import java.time.LocalDate;
import java.util.List;

public class BulkPlacementRequestDto {
    private Long wineId;
    private List<Long> spaceIds;
    private Integer quantityPerSpace;
    private LocalDate dateAdded;
    private Integer preferredStorageDuration;
    private String notes;

    public Long getWineId() {
        return wineId;
    }

    public void setWineId(Long wineId) {
        this.wineId = wineId;
    }

    public List<Long> getSpaceIds() {
        return spaceIds;
    }

    public void setSpaceIds(List<Long> spaceIds) {
        this.spaceIds = spaceIds;
    }

    public Integer getQuantityPerSpace() {
        return quantityPerSpace;
    }

    public void setQuantityPerSpace(Integer quantityPerSpace) {
        this.quantityPerSpace = quantityPerSpace;
    }

    public LocalDate getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Integer getPreferredStorageDuration() {
        return preferredStorageDuration;
    }

    public void setPreferredStorageDuration(Integer preferredStorageDuration) {
        this.preferredStorageDuration = preferredStorageDuration;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

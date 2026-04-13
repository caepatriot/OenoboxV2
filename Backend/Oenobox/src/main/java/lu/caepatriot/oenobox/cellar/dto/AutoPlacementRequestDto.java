package lu.caepatriot.oenobox.cellar.dto;

import java.time.LocalDate;

public class AutoPlacementRequestDto {
    private Long wineId;
    private Integer quantity;
    private String preferredGroup;
    private LocalDate dateAdded;
    private Integer preferredStorageDuration;
    private String notes;

    public Long getWineId() {
        return wineId;
    }

    public void setWineId(Long wineId) {
        this.wineId = wineId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getPreferredGroup() {
        return preferredGroup;
    }

    public void setPreferredGroup(String preferredGroup) {
        this.preferredGroup = preferredGroup;
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

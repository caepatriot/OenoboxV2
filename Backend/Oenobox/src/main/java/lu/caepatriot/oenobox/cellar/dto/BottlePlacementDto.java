package lu.caepatriot.oenobox.cellar.dto;

import lu.caepatriot.oenobox.cellar.entity.Cave;
import lu.caepatriot.oenobox.winecatalog.dto.WineDto;
import lu.caepatriot.oenobox.winecatalog.entity.Wine;

import java.time.LocalDate;

public class BottlePlacementDto {
    private Long id;
    private Long spaceId;
    private WineDto wine;
    private Integer quantity;
    private LocalDate dateAdded;
    private Integer preferredStorageDuration;
    private String notes;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSpaceId() { return spaceId; }
    public void setSpaceId(Long spaceId) { this.spaceId = spaceId; }
    public WineDto getWine() { return wine; }
    public void setWine(WineDto wine) { this.wine = wine; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public LocalDate getDateAdded() { return dateAdded; }
    public void setDateAdded(LocalDate dateAdded) { this.dateAdded = dateAdded; }
    public Integer getPreferredStorageDuration() { return preferredStorageDuration; }
    public void setPreferredStorageDuration(Integer preferredStorageDuration) { this.preferredStorageDuration = preferredStorageDuration; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}


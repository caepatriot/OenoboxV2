package lu.caepatriot.oenobox.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CreateAcquisitionLotRequest {
    private Long householdId;
    private String source;
    private LocalDate acquiredOn;
    private BigDecimal totalPrice;
    private String currency;
    private String notes;
    private Boolean createStoredBottles;
    private List<CreateAcquisitionLotItemRequest> items;

    public Long getHouseholdId() { return householdId; }
    public void setHouseholdId(Long householdId) { this.householdId = householdId; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public LocalDate getAcquiredOn() { return acquiredOn; }
    public void setAcquiredOn(LocalDate acquiredOn) { this.acquiredOn = acquiredOn; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getCreateStoredBottles() { return createStoredBottles; }
    public void setCreateStoredBottles(Boolean createStoredBottles) { this.createStoredBottles = createStoredBottles; }

    public List<CreateAcquisitionLotItemRequest> getItems() { return items; }
    public void setItems(List<CreateAcquisitionLotItemRequest> items) { this.items = items; }
}

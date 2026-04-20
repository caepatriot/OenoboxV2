package lu.caepatriot.oenobox.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class AcquisitionLotDto {
    private Long id;
    private Long householdId;
    private String source;
    private LocalDate acquiredOn;
    private BigDecimal totalPrice;
    private String currency;
    private String notes;
    private Integer totalReceived;
    private Integer totalAvailable;
    private Integer totalDispatched;
    private List<AcquisitionLotItemDto> items;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Integer getTotalReceived() { return totalReceived; }
    public void setTotalReceived(Integer totalReceived) { this.totalReceived = totalReceived; }

    public Integer getTotalAvailable() { return totalAvailable; }
    public void setTotalAvailable(Integer totalAvailable) { this.totalAvailable = totalAvailable; }

    public Integer getTotalDispatched() { return totalDispatched; }
    public void setTotalDispatched(Integer totalDispatched) { this.totalDispatched = totalDispatched; }

    public List<AcquisitionLotItemDto> getItems() { return items; }
    public void setItems(List<AcquisitionLotItemDto> items) { this.items = items; }
}

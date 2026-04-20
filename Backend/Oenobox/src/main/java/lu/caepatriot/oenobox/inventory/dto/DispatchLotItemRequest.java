package lu.caepatriot.oenobox.inventory.dto;

public class DispatchLotItemRequest {
    private Long lotItemId;
    private Integer quantity;
    private String notes;

    public Long getLotItemId() { return lotItemId; }
    public void setLotItemId(Long lotItemId) { this.lotItemId = lotItemId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

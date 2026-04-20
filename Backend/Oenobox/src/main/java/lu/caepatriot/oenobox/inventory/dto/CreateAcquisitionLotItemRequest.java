package lu.caepatriot.oenobox.inventory.dto;

import java.math.BigDecimal;

public class CreateAcquisitionLotItemRequest {
    private Long wineVintageId;
    private Integer quantityReceived;
    private BigDecimal unitPrice;
    private String currency;
    private String bottleFormat;
    private String notes;

    public Long getWineVintageId() { return wineVintageId; }
    public void setWineVintageId(Long wineVintageId) { this.wineVintageId = wineVintageId; }

    public Integer getQuantityReceived() { return quantityReceived; }
    public void setQuantityReceived(Integer quantityReceived) { this.quantityReceived = quantityReceived; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getBottleFormat() { return bottleFormat; }
    public void setBottleFormat(String bottleFormat) { this.bottleFormat = bottleFormat; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

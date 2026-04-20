package lu.caepatriot.oenobox.inventory.dto;

import java.math.BigDecimal;

public class AcquisitionLotItemDto {
    private Long id;
    private Long wineId;
    private Long wineVintageId;
    private String wineLabel;
    private String producer;
    private Integer vintageYear;
    private String bottleFormat;
    private Integer quantityReceived;
    private Integer quantityAvailable;
    private Integer quantityDispatched;
    private BigDecimal unitPrice;
    private String currency;
    private String notes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getWineId() { return wineId; }
    public void setWineId(Long wineId) { this.wineId = wineId; }

    public Long getWineVintageId() { return wineVintageId; }
    public void setWineVintageId(Long wineVintageId) { this.wineVintageId = wineVintageId; }

    public String getWineLabel() { return wineLabel; }
    public void setWineLabel(String wineLabel) { this.wineLabel = wineLabel; }

    public String getProducer() { return producer; }
    public void setProducer(String producer) { this.producer = producer; }

    public Integer getVintageYear() { return vintageYear; }
    public void setVintageYear(Integer vintageYear) { this.vintageYear = vintageYear; }

    public String getBottleFormat() { return bottleFormat; }
    public void setBottleFormat(String bottleFormat) { this.bottleFormat = bottleFormat; }

    public Integer getQuantityReceived() { return quantityReceived; }
    public void setQuantityReceived(Integer quantityReceived) { this.quantityReceived = quantityReceived; }

    public Integer getQuantityAvailable() { return quantityAvailable; }
    public void setQuantityAvailable(Integer quantityAvailable) { this.quantityAvailable = quantityAvailable; }

    public Integer getQuantityDispatched() { return quantityDispatched; }
    public void setQuantityDispatched(Integer quantityDispatched) { this.quantityDispatched = quantityDispatched; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

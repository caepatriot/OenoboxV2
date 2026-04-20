package lu.caepatriot.oenobox.inventory.entity;

import jakarta.persistence.*;
import lu.caepatriot.oenobox.winecatalog.entity.WineVintage;

import java.math.BigDecimal;

@Entity
@Table(name = "acquisition_lot_items")
public class AcquisitionLotItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "acquisition_lot_id", nullable = false)
    private AcquisitionLot acquisitionLot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wine_vintage_id", nullable = false)
    private WineVintage wineVintage;

    @Column(name = "quantity_received", nullable = false)
    private Integer quantityReceived;

    @Column(name = "quantity_available", nullable = false)
    private Integer quantityAvailable;

    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    private String currency;

    @Column(name = "bottle_format")
    private String bottleFormat;

    private String notes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AcquisitionLot getAcquisitionLot() { return acquisitionLot; }
    public void setAcquisitionLot(AcquisitionLot acquisitionLot) { this.acquisitionLot = acquisitionLot; }

    public WineVintage getWineVintage() { return wineVintage; }
    public void setWineVintage(WineVintage wineVintage) { this.wineVintage = wineVintage; }

    public Integer getQuantityReceived() { return quantityReceived; }
    public void setQuantityReceived(Integer quantityReceived) { this.quantityReceived = quantityReceived; }

    public Integer getQuantityAvailable() { return quantityAvailable; }
    public void setQuantityAvailable(Integer quantityAvailable) { this.quantityAvailable = quantityAvailable; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getBottleFormat() { return bottleFormat; }
    public void setBottleFormat(String bottleFormat) { this.bottleFormat = bottleFormat; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

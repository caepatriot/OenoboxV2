package lu.caepatriot.oenobox.inventory.entity;

import lu.caepatriot.oenobox.winecatalog.entity.WineVintage;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "stored_bottles")
public class StoredBottle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wine_vintage_id", nullable = false)
    private WineVintage wineVintage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acquisition_lot_id")
    private AcquisitionLot acquisitionLot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acquisition_lot_item_id")
    private AcquisitionLotItem acquisitionLotItem;

    private String status;

    @Column(name = "purchase_price", precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "owned_since")
    private LocalDate ownedSince;

    @Column(name = "consumed_on")
    private LocalDate consumedOn;

    private String notes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public WineVintage getWineVintage() { return wineVintage; }
    public void setWineVintage(WineVintage wineVintage) { this.wineVintage = wineVintage; }

    public AcquisitionLot getAcquisitionLot() { return acquisitionLot; }
    public void setAcquisitionLot(AcquisitionLot acquisitionLot) { this.acquisitionLot = acquisitionLot; }

    public AcquisitionLotItem getAcquisitionLotItem() { return acquisitionLotItem; }
    public void setAcquisitionLotItem(AcquisitionLotItem acquisitionLotItem) { this.acquisitionLotItem = acquisitionLotItem; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }

    public LocalDate getOwnedSince() { return ownedSince; }
    public void setOwnedSince(LocalDate ownedSince) { this.ownedSince = ownedSince; }

    public LocalDate getConsumedOn() { return consumedOn; }
    public void setConsumedOn(LocalDate consumedOn) { this.consumedOn = consumedOn; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

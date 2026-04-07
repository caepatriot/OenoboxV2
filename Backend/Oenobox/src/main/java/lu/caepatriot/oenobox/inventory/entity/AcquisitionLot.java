package lu.caepatriot.oenobox.inventory.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "acquisition_lots")
public class AcquisitionLot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "household_id")
    private Long householdId;

    private String source;

    @Column(name = "acquired_on")
    private LocalDate acquiredOn;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    private String currency;

    private String notes;

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
}

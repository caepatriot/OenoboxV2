package lu.caepatriot.oenobox.inventory.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_transactions")
public class StockTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stored_bottle_id", nullable = false)
    private StoredBottle storedBottle;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    private Integer quantity;

    @Column(name = "transaction_at")
    private LocalDateTime transactionAt;

    private String notes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StoredBottle getStoredBottle() { return storedBottle; }
    public void setStoredBottle(StoredBottle storedBottle) { this.storedBottle = storedBottle; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDateTime getTransactionAt() { return transactionAt; }
    public void setTransactionAt(LocalDateTime transactionAt) { this.transactionAt = transactionAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

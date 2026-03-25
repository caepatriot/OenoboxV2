package lu.caepatriot.oenobox.entity.cave;

import lu.caepatriot.oenobox.entity.wine.Wine;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "bottle_placement")
public class BottlePlacement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private Space space;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wine_id", nullable = false)
    private Wine wine;

    private Integer quantity;
    
    @Column(name = "date_added")
    private LocalDate dateAdded;

    @Column(name = "preferred_storage_duration")
    private Integer preferredStorageDuration;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // Constructors
    public BottlePlacement() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Space getSpace() { return space; }
    public void setSpace(Space space) { this.space = space; }

    public Wine getWine() { return wine; }
    public void setWine(Wine wine) { this.wine = wine; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDate getDateAdded() { return dateAdded; }
    public void setDateAdded(LocalDate dateAdded) { this.dateAdded = dateAdded; }

    public Integer getPreferredStorageDuration() { return preferredStorageDuration; }
    public void setPreferredStorageDuration(Integer preferredStorageDuration) { this.preferredStorageDuration = preferredStorageDuration; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

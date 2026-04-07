package lu.caepatriot.oenobox.tasting.entity;

import lu.caepatriot.oenobox.inventory.entity.StoredBottle;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tasting_notes")
public class TastingNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stored_bottle_id")
    private StoredBottle storedBottle;

    private Integer rating;

    @Column(name = "tasted_on")
    private LocalDate tastedOn;

    @Column(columnDefinition = "TEXT")
    private String appearance;

    @Column(columnDefinition = "TEXT")
    private String nose;

    @Column(columnDefinition = "TEXT")
    private String palate;

    @Column(columnDefinition = "TEXT")
    private String finish;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StoredBottle getStoredBottle() { return storedBottle; }
    public void setStoredBottle(StoredBottle storedBottle) { this.storedBottle = storedBottle; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public LocalDate getTastedOn() { return tastedOn; }
    public void setTastedOn(LocalDate tastedOn) { this.tastedOn = tastedOn; }

    public String getAppearance() { return appearance; }
    public void setAppearance(String appearance) { this.appearance = appearance; }

    public String getNose() { return nose; }
    public void setNose(String nose) { this.nose = nose; }

    public String getPalate() { return palate; }
    public void setPalate(String palate) { this.palate = palate; }

    public String getFinish() { return finish; }
    public void setFinish(String finish) { this.finish = finish; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

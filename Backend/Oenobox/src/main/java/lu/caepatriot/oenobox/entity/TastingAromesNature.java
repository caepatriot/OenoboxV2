package lu.caepatriot.oenobox.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tasting_aromes_nature")
public class TastingAromesNature {
    @EmbeddedId
    private TastingAromesNatureId id;

    @ElementCollection
    @CollectionTable(name = "tasting_aromes_nature_notes",
        joinColumns = {
            @JoinColumn(name = "category", referencedColumnName = "category"),
            @JoinColumn(name = "tasting_id", referencedColumnName = "tasting_id")
        })
    @Column(name = "note")
    private List<String> notes;

    // Constructors
    public TastingAromesNature() {}

    public TastingAromesNature(TastingAromesNatureId id, List<String> notes) {
        this.id = id;
        this.notes = notes;
    }

    // Getters and Setters
    public TastingAromesNatureId getId() { return id; }
    public void setId(TastingAromesNatureId id) { this.id = id; }

    public List<String> getNotes() { return notes; }
    public void setNotes(List<String> notes) { this.notes = notes; }
}
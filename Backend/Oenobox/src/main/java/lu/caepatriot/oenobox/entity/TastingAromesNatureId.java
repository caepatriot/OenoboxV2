package lu.caepatriot.oenobox.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TastingAromesNatureId implements Serializable {
    @Column(name = "category")
    private String category;

    @Column(name = "tasting_id")
    private Long tastingId;

    // Constructors
    public TastingAromesNatureId() {}

    public TastingAromesNatureId(String category, Long tastingId) {
        this.category = category;
        this.tastingId = tastingId;
    }

    // Getters and Setters
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Long getTastingId() { return tastingId; }
    public void setTastingId(Long tastingId) { this.tastingId = tastingId; }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TastingAromesNatureId that = (TastingAromesNatureId) o;
        return Objects.equals(category, that.category) && Objects.equals(tastingId, that.tastingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, tastingId);
    }
}
package lu.caepatriot.oenobox.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tasting_cepages")
public class TastingCepages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tasting_id", nullable = false)
    private Long tastingId;

    @Column(name = "cepage")
    private String cepage;

    // Constructors
    public TastingCepages() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTastingId() { return tastingId; }
    public void setTastingId(Long tastingId) { this.tastingId = tastingId; }

    public String getCepage() { return cepage; }
    public void setCepage(String cepage) { this.cepage = cepage; }
}
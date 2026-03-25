package lu.caepatriot.oenobox.entity.wine;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "wine")
public class Wine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wine_type_id")
    private WineType wineType;

    @ManyToMany
    @JoinTable(
        name = "wine_cepages",
        joinColumns = @JoinColumn(name = "wine_id"),
        inverseJoinColumns = @JoinColumn(name = "cepage_id")
    )
    private List<Cepage> cepages;

    private String region;
    
    @Column(name = "wine_year")
    private Integer year;

    // Constructors
    public Wine() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public WineType getWineType() { return wineType; }
    public void setWineType(WineType wineType) { this.wineType = wineType; }

    public List<Cepage> getCepages() { return cepages; }
    public void setCepages(List<Cepage> cepages) { this.cepages = cepages; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
}

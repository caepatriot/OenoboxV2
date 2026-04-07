package lu.caepatriot.oenobox.winecatalog.entity;

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

    private String producer;

    private String country;

    private String appellation;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "aroma_notes", columnDefinition = "jsonb")
    private List<String> aromaNotes;

    @Column(name = "food_pairings", columnDefinition = "jsonb")
    private List<String> foodPairings;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "data_source")
    private String dataSource;

    @Column(name = "external_id")
    private String externalId;

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

    public String getProducer() { return producer; }
    public void setProducer(String producer) { this.producer = producer; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getAppellation() { return appellation; }
    public void setAppellation(String appellation) { this.appellation = appellation; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getAromaNotes() { return aromaNotes; }
    public void setAromaNotes(List<String> aromaNotes) { this.aromaNotes = aromaNotes; }

    public List<String> getFoodPairings() { return foodPairings; }
    public void setFoodPairings(List<String> foodPairings) { this.foodPairings = foodPairings; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
}


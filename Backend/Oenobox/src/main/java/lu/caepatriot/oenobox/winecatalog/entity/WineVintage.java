package lu.caepatriot.oenobox.winecatalog.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "wine_vintages")
public class WineVintage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wine_id", nullable = false)
    private Wine wine;

    @Column(name = "vintage_year")
    private Integer vintageYear;

    @Column(name = "bottle_format")
    private String bottleFormat;

    @Column(name = "alcohol_percent", precision = 4, scale = 2)
    private BigDecimal alcoholPercent;

    @Column(name = "drink_from_year")
    private Integer drinkFromYear;

    @Column(name = "drink_to_year")
    private Integer drinkToYear;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Wine getWine() { return wine; }
    public void setWine(Wine wine) { this.wine = wine; }

    public Integer getVintageYear() { return vintageYear; }
    public void setVintageYear(Integer vintageYear) { this.vintageYear = vintageYear; }

    public String getBottleFormat() { return bottleFormat; }
    public void setBottleFormat(String bottleFormat) { this.bottleFormat = bottleFormat; }

    public BigDecimal getAlcoholPercent() { return alcoholPercent; }
    public void setAlcoholPercent(BigDecimal alcoholPercent) { this.alcoholPercent = alcoholPercent; }

    public Integer getDrinkFromYear() { return drinkFromYear; }
    public void setDrinkFromYear(Integer drinkFromYear) { this.drinkFromYear = drinkFromYear; }

    public Integer getDrinkToYear() { return drinkToYear; }
    public void setDrinkToYear(Integer drinkToYear) { this.drinkToYear = drinkToYear; }
}

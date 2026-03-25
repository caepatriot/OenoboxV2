package lu.caepatriot.oenobox.entity.cave;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "space")
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private StorageUnit unit;

    @Column(name = "pos_row")
    private Integer row;
    
    @Column(name = "pos_column")
    private Integer column;

    private Double coordX;
    private Double coordY;

    private Integer capacity;

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BottlePlacement> placements = new ArrayList<>();

    // Constructors
    public Space() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StorageUnit getUnit() { return unit; }
    public void setUnit(StorageUnit unit) { this.unit = unit; }

    public Integer getRow() { return row; }
    public void setRow(Integer row) { this.row = row; }

    public Integer getColumn() { return column; }
    public void setColumn(Integer column) { this.column = column; }

    public Double getCoordX() { return coordX; }
    public void setCoordX(Double coordX) { this.coordX = coordX; }

    public Double getCoordY() { return coordY; }
    public void setCoordY(Double coordY) { this.coordY = coordY; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public List<BottlePlacement> getPlacements() { return placements; }
    public void setPlacements(List<BottlePlacement> placements) { this.placements = placements; }
}

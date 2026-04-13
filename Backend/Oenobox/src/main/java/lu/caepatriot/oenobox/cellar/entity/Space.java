package lu.caepatriot.oenobox.cellar.entity;

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

    @Column(name = "coordx")
    private Double coordX;

    @Column(name = "coordy")
    private Double coordY;

    private Integer capacity;

    @Column(name = "slot_index")
    private Integer slotIndex;

    @Column(name = "slot_label")
    private String slotLabel;

    @Column(name = "preferred_wine_group")
    private String preferredWineGroup;

    @Column(name = "section_name")
    private String sectionName;

    private Boolean disabled;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BottlePlacement> placements = new ArrayList<>();

    public Space() {}

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

    public Integer getSlotIndex() { return slotIndex; }
    public void setSlotIndex(Integer slotIndex) { this.slotIndex = slotIndex; }

    public String getSlotLabel() { return slotLabel; }
    public void setSlotLabel(String slotLabel) { this.slotLabel = slotLabel; }

    public String getPreferredWineGroup() { return preferredWineGroup; }
    public void setPreferredWineGroup(String preferredWineGroup) { this.preferredWineGroup = preferredWineGroup; }

    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }

    public Boolean getDisabled() { return disabled; }
    public void setDisabled(Boolean disabled) { this.disabled = disabled; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<BottlePlacement> getPlacements() { return placements; }
    public void setPlacements(List<BottlePlacement> placements) { this.placements = placements; }
}

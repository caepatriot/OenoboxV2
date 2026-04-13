package lu.caepatriot.oenobox.cellar.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "storage_unit")
public class StorageUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(name = "template_key")
    private String templateKey;

    @Column(name = "layout_mode")
    private String layoutMode;

    @Column(name = "posx")
    private Double posX;

    @Column(name = "posy")
    private Double posY;

    private Double width;
    private Double height;
    private Double depth;

    private String orientation;
    private String wall;
    private Double elevation;
    private Integer capacity;
    private Double rotation;

    @Column(name = "row_count")
    private Integer rowCount;

    @Column(name = "column_count")
    private Integer columnCount;

    @Column(name = "default_space_capacity")
    private Integer defaultSpaceCapacity;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "preferred_wine_group")
    private String preferredWineGroup;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cave_id", nullable = false)
    private Cave cave;

    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("row ASC, column ASC")
    private List<Space> spaces = new ArrayList<>();

    public StorageUnit() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTemplateKey() { return templateKey; }
    public void setTemplateKey(String templateKey) { this.templateKey = templateKey; }

    public String getLayoutMode() { return layoutMode; }
    public void setLayoutMode(String layoutMode) { this.layoutMode = layoutMode; }

    public Double getPosX() { return posX; }
    public void setPosX(Double posX) { this.posX = posX; }

    public Double getPosY() { return posY; }
    public void setPosY(Double posY) { this.posY = posY; }

    public Double getWidth() { return width; }
    public void setWidth(Double width) { this.width = width; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public Double getDepth() { return depth; }
    public void setDepth(Double depth) { this.depth = depth; }

    public String getOrientation() { return orientation; }
    public void setOrientation(String orientation) { this.orientation = orientation; }

    public String getWall() { return wall; }
    public void setWall(String wall) { this.wall = wall; }

    public Double getElevation() { return elevation; }
    public void setElevation(Double elevation) { this.elevation = elevation; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Double getRotation() { return rotation; }
    public void setRotation(Double rotation) { this.rotation = rotation; }

    public Integer getRowCount() { return rowCount; }
    public void setRowCount(Integer rowCount) { this.rowCount = rowCount; }

    public Integer getColumnCount() { return columnCount; }
    public void setColumnCount(Integer columnCount) { this.columnCount = columnCount; }

    public Integer getDefaultSpaceCapacity() { return defaultSpaceCapacity; }
    public void setDefaultSpaceCapacity(Integer defaultSpaceCapacity) { this.defaultSpaceCapacity = defaultSpaceCapacity; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public String getPreferredWineGroup() { return preferredWineGroup; }
    public void setPreferredWineGroup(String preferredWineGroup) { this.preferredWineGroup = preferredWineGroup; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Cave getCave() { return cave; }
    public void setCave(Cave cave) { this.cave = cave; }

    public List<Space> getSpaces() { return spaces; }
    public void setSpaces(List<Space> spaces) { this.spaces = spaces; }
}

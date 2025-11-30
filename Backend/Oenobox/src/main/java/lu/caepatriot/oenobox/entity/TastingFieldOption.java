package lu.caepatriot.oenobox.entity;

import jakarta.persistence.*;
import lu.caepatriot.oenobox.util.StringListJsonbConverter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tasting_field_option")
public class TastingFieldOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false)
    private TastingField field;

    @Column(name = "value", nullable = false, length = 100)
    private String value;

    @Column(name = "label", nullable = false, length = 100)
    private String label;

    @Column(name = "color_code", length = 7)
    private String colorCode;

    @Column(name = "is_negative")
    private Boolean isNegative = false;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "icon", length = 50)
    private String icon;

    @Column(name = "wine_type_restriction", columnDefinition = "JSONB")
    @Convert(converter = StringListJsonbConverter.class)
    private List<String> wineTypeRestriction;

    @Column(name = "group_index")
    private Short groupIndex;

    @Column(name = "group_required")
    private Boolean groupRequired = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "categoryOption", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AromaNote> aromaNotes;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public TastingFieldOption() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TastingField getField() {
        return field;
    }

    public void setField(TastingField field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public Boolean getIsNegative() {
        return isNegative;
    }

    public void setIsNegative(Boolean isNegative) {
        this.isNegative = isNegative;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<String> getWineTypeRestriction() {
        return wineTypeRestriction;
    }

    public void setWineTypeRestriction(List<String> wineTypeRestriction) {
        this.wineTypeRestriction = wineTypeRestriction;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<AromaNote> getAromaNotes() {
        return aromaNotes;
    }

    public void setAromaNotes(List<AromaNote> aromaNotes) {
        this.aromaNotes = aromaNotes;
    }
}
package lu.caepatriot.oenobox.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tasting_field")
public class TastingField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    private TastingStep step;

    @Column(name = "field_type", nullable = false, length = 20)
    private String fieldType;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "label", nullable = false, length = 100)
    private String label;

    @Column(name = "placeholder", columnDefinition = "TEXT")
    private String placeholder;

    @Column(name = "required")
    private Boolean required = false;

    @Column(name = "multi_select")
    private Boolean multiSelect = false;

    @Column(name = "wine_type_restriction", columnDefinition = "JSONB")
    private List<String> wineTypeRestriction;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TastingFieldOption> options;

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
    public TastingField() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TastingStep getStep() { return step; }
    public void setStep(TastingStep step) { this.step = step; }

    public String getFieldType() { return fieldType; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getPlaceholder() { return placeholder; }
    public void setPlaceholder(String placeholder) { this.placeholder = placeholder; }

    public Boolean getRequired() { return required; }
    public void setRequired(Boolean required) { this.required = required; }

    public Boolean getMultiSelect() { return multiSelect; }
    public void setMultiSelect(Boolean multiSelect) { this.multiSelect = multiSelect; }

    public List<String> getWineTypeRestriction() { return wineTypeRestriction; }
    public void setWineTypeRestriction(List<String> wineTypeRestriction) { this.wineTypeRestriction = wineTypeRestriction; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<TastingFieldOption> getOptions() { return options; }
    public void setOptions(List<TastingFieldOption> options) { this.options = options; }
}
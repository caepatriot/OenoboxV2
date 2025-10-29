package lu.caepatriot.oenobox.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TastingStepDto {
    private Long id;
    private Integer stepNumber;
    private String name;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TastingFieldDto> fields;

    // Constructors
    public TastingStepDto() {}

    public TastingStepDto(Long id, Integer stepNumber, String name, String title, String description,
                         LocalDateTime createdAt, LocalDateTime updatedAt, List<TastingFieldDto> fields) {
        this.id = id;
        this.stepNumber = stepNumber;
        this.name = name;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.fields = fields;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getStepNumber() { return stepNumber; }
    public void setStepNumber(Integer stepNumber) { this.stepNumber = stepNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<TastingFieldDto> getFields() { return fields; }
    public void setFields(List<TastingFieldDto> fields) { this.fields = fields; }
}
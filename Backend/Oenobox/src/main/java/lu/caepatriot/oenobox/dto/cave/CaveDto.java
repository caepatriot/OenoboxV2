package lu.caepatriot.oenobox.dto.cave;

import lu.caepatriot.oenobox.entity.cave.Cave;

import java.util.List;

public class CaveDto {
    private Long id;
    private String name;
    private String description;
    private DimensionsDto dimensions;
    private Double temperature;
    private Double humidity;
    private List<StorageUnitDto> units;

    public static class DimensionsDto {
        private Double width;
        private Double height;
        private Double depth;

        public DimensionsDto() {}
        public DimensionsDto(Double width, Double height, Double depth) {
            this.width = width;
            this.height = height;
            this.depth = depth;
        }

        public Double getWidth() { return width; }
        public void setWidth(Double width) { this.width = width; }
        public Double getHeight() { return height; }
        public void setHeight(Double height) { this.height = height; }
        public Double getDepth() { return depth; }
        public void setDepth(Double depth) { this.depth = depth; }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public DimensionsDto getDimensions() { return dimensions; }
    public void setDimensions(DimensionsDto dimensions) { this.dimensions = dimensions; }
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public Double getHumidity() { return humidity; }
    public void setHumidity(Double humidity) { this.humidity = humidity; }
    public List<StorageUnitDto> getUnits() { return units; }
    public void setUnits(List<StorageUnitDto> units) { this.units = units; }
}

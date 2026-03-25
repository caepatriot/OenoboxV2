package lu.caepatriot.oenobox.dto.cave;

import lu.caepatriot.oenobox.entity.cave.Cave;

import java.util.List;

public class StorageUnitDto {
    private Long id;
    private String name;
    private String type;
    private PositionDto position;
    private DimensionsDto dimensions;
    private String orientation;
    private String wall;
    private Double elevation;
    private Integer capacity;
    private Double rotation;
    private List<SpaceDto> spaces;

    public static class PositionDto {
        private Double x;
        private Double y;

        public PositionDto() {}
        public PositionDto(Double x, Double y) {
            this.x = x;
            this.y = y;
        }

        public Double getX() { return x; }
        public void setX(Double x) { this.x = x; }
        public Double getY() { return y; }
        public void setY(Double y) { this.y = y; }
    }

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
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public PositionDto getPosition() { return position; }
    public void setPosition(PositionDto position) { this.position = position; }
    public DimensionsDto getDimensions() { return dimensions; }
    public void setDimensions(DimensionsDto dimensions) { this.dimensions = dimensions; }
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
    public List<SpaceDto> getSpaces() { return spaces; }
    public void setSpaces(List<SpaceDto> spaces) { this.spaces = spaces; }
}

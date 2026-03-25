package lu.caepatriot.oenobox.dto.cave;

import lu.caepatriot.oenobox.entity.cave.Cave;

import java.util.List;

public class SpaceDto {
    private Long id;
    private Long unitId;
    private PositionDto position;
    private CoordinatesDto coordinates;
    private Integer capacity;
    private List<BottlePlacementDto> currentBottles;

    public static class PositionDto {
        private Integer row;
        private Integer column;

        public PositionDto() {}
        public PositionDto(Integer row, Integer column) {
            this.row = row;
            this.column = column;
        }

        public Integer getRow() { return row; }
        public void setRow(Integer row) { this.row = row; }
        public Integer getColumn() { return column; }
        public void setColumn(Integer column) { this.column = column; }
    }

    public static class CoordinatesDto {
        private Double x;
        private Double y;

        public CoordinatesDto() {}
        public CoordinatesDto(Double x, Double y) {
            this.x = x;
            this.y = y;
        }

        public Double getX() { return x; }
        public void setX(Double x) { this.x = x; }
        public Double getY() { return y; }
        public void setY(Double y) { this.y = y; }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUnitId() { return unitId; }
    public void setUnitId(Long unitId) { this.unitId = unitId; }
    public PositionDto getPosition() { return position; }
    public void setPosition(PositionDto position) { this.position = position; }
    public CoordinatesDto getCoordinates() { return coordinates; }
    public void setCoordinates(CoordinatesDto coordinates) { this.coordinates = coordinates; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public List<BottlePlacementDto> getCurrentBottles() { return currentBottles; }
    public void setCurrentBottles(List<BottlePlacementDto> currentBottles) { this.currentBottles = currentBottles; }
}

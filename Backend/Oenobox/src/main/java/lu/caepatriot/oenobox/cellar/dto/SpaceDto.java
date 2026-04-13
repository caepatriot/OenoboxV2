package lu.caepatriot.oenobox.cellar.dto;

import java.util.List;

public class SpaceDto {
    private Long id;
    private Long unitId;
    private PositionDto position;
    private CoordinatesDto coordinates;
    private Integer capacity;
    private Integer slotIndex;
    private String slotLabel;
    private String preferredWineGroup;
    private String sectionName;
    private Boolean disabled;
    private String notes;
    private Integer occupiedQuantity;
    private Integer freeCapacity;
    private List<BottlePlacementDto> currentBottles;

    public static class PositionDto {
        private Integer row;
        private Integer column;

        public PositionDto() {}

        public PositionDto(Integer row, Integer column) {
            this.row = row;
            this.column = column;
        }

        public Integer getRow() {
            return row;
        }

        public void setRow(Integer row) {
            this.row = row;
        }

        public Integer getColumn() {
            return column;
        }

        public void setColumn(Integer column) {
            this.column = column;
        }
    }

    public static class CoordinatesDto {
        private Double x;
        private Double y;

        public CoordinatesDto() {}

        public CoordinatesDto(Double x, Double y) {
            this.x = x;
            this.y = y;
        }

        public Double getX() {
            return x;
        }

        public void setX(Double x) {
            this.x = x;
        }

        public Double getY() {
            return y;
        }

        public void setY(Double y) {
            this.y = y;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public PositionDto getPosition() {
        return position;
    }

    public void setPosition(PositionDto position) {
        this.position = position;
    }

    public CoordinatesDto getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(CoordinatesDto coordinates) {
        this.coordinates = coordinates;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getSlotIndex() {
        return slotIndex;
    }

    public void setSlotIndex(Integer slotIndex) {
        this.slotIndex = slotIndex;
    }

    public String getSlotLabel() {
        return slotLabel;
    }

    public void setSlotLabel(String slotLabel) {
        this.slotLabel = slotLabel;
    }

    public String getPreferredWineGroup() {
        return preferredWineGroup;
    }

    public void setPreferredWineGroup(String preferredWineGroup) {
        this.preferredWineGroup = preferredWineGroup;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getOccupiedQuantity() {
        return occupiedQuantity;
    }

    public void setOccupiedQuantity(Integer occupiedQuantity) {
        this.occupiedQuantity = occupiedQuantity;
    }

    public Integer getFreeCapacity() {
        return freeCapacity;
    }

    public void setFreeCapacity(Integer freeCapacity) {
        this.freeCapacity = freeCapacity;
    }

    public List<BottlePlacementDto> getCurrentBottles() {
        return currentBottles;
    }

    public void setCurrentBottles(List<BottlePlacementDto> currentBottles) {
        this.currentBottles = currentBottles;
    }
}

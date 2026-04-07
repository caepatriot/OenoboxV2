package lu.caepatriot.oenobox.cellar.service;

import lu.caepatriot.oenobox.cellar.dto.SpaceDto;
import lu.caepatriot.oenobox.cellar.dto.BottlePlacementDto;
import lu.caepatriot.oenobox.cellar.entity.BottlePlacement;
import lu.caepatriot.oenobox.cellar.dto.CaveDto;
import lu.caepatriot.oenobox.cellar.entity.Cave;
import lu.caepatriot.oenobox.cellar.entity.Space;
import lu.caepatriot.oenobox.cellar.repository.SpaceRepository;
import lu.caepatriot.oenobox.cellar.dto.StorageUnitDto;
import lu.caepatriot.oenobox.cellar.entity.StorageUnit;
import lu.caepatriot.oenobox.winecatalog.dto.WineDto;
import lu.caepatriot.oenobox.winecatalog.entity.Wine;
import lu.caepatriot.oenobox.cellar.repository.StorageUnitRepository;
import lu.caepatriot.oenobox.cellar.repository.BottlePlacementRepository;
import lu.caepatriot.oenobox.winecatalog.repository.WineRepository;
import lu.caepatriot.oenobox.cellar.repository.CaveRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CaveService {
    private final CaveRepository caveRepository;
    private final StorageUnitRepository storageUnitRepository;
    private final SpaceRepository spaceRepository;
    private final BottlePlacementRepository bottlePlacementRepository;
    private final WineRepository wineRepository;

    public CaveService(CaveRepository caveRepository,
                       StorageUnitRepository storageUnitRepository,
                       SpaceRepository spaceRepository,
                       BottlePlacementRepository bottlePlacementRepository,
                       WineRepository wineRepository) {
        this.caveRepository = caveRepository;
        this.storageUnitRepository = storageUnitRepository;
        this.spaceRepository = spaceRepository;
        this.bottlePlacementRepository = bottlePlacementRepository;
        this.wineRepository = wineRepository;
    }

    @Transactional(readOnly = true)
    public List<CaveDto> getAllCaves() {
        return caveRepository.findAll().stream()
                .map(this::mapToCaveDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CaveDto getCaveById(Long id) {
        return caveRepository.findById(id)
                .map(this::mapToCaveDto)
                .orElseThrow(() -> new RuntimeException("Cave not found"));
    }

    @Transactional
    public CaveDto createCave(CaveDto caveDto) {
        Cave cave = new Cave();
        updateCaveFromDto(cave, caveDto);
        return mapToCaveDto(caveRepository.save(cave));
    }

    @Transactional
    public CaveDto updateCave(Long id, CaveDto caveDto) {
        Cave cave = caveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cave not found"));
        updateCaveFromDto(cave, caveDto);
        return mapToCaveDto(caveRepository.save(cave));
    }

    @Transactional
    public void deleteCave(Long id) {
        caveRepository.deleteById(id);
    }

    @Transactional
    public StorageUnitDto createUnit(Long caveId, StorageUnitDto unitDto) {
        Cave cave = caveRepository.findById(caveId)
                .orElseThrow(() -> new RuntimeException("Cave not found"));
        
        StorageUnit unit = new StorageUnit();
        unit.setCave(cave);
        updateUnitFromDto(unit, unitDto);
        
        generateSpacesForUnit(unit);
        
        return mapToUnitDto(storageUnitRepository.save(unit));
    }

    @Transactional
    public StorageUnitDto updateUnit(Long unitId, StorageUnitDto unitDto) {
        StorageUnit unit = storageUnitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unit not found"));
        updateUnitFromDto(unit, unitDto);
        return mapToUnitDto(storageUnitRepository.save(unit));
    }

    @Transactional
    public void deleteUnit(Long unitId) {
        storageUnitRepository.deleteById(unitId);
    }

    @Transactional
    public BottlePlacementDto addPlacement(BottlePlacementDto placementDto) {
        Space space = spaceRepository.findById(placementDto.getSpaceId())
                .orElseThrow(() -> new RuntimeException("Space not found"));
        
        Wine wine = wineRepository.findById(placementDto.getWine().getId())
                .orElse(null);
        
        if (wine == null && placementDto.getWine() != null) {
            wine = new Wine();
            wine.setName(placementDto.getWine().getName());
            wine.setRegion(placementDto.getWine().getRegion());
            wine.setYear(placementDto.getWine().getYear());
            wine.setProducer(placementDto.getWine().getProducer());
            wine.setCountry(placementDto.getWine().getCountry());
            wine.setAppellation(placementDto.getWine().getAppellation());
            wine.setDescription(placementDto.getWine().getDescription());
            wine.setAromaNotes(placementDto.getWine().getAromaNotes());
            wine.setFoodPairings(placementDto.getWine().getFoodPairings());
            wine.setImageUrl(placementDto.getWine().getImageUrl());
            wine.setDataSource(placementDto.getWine().getDataSource());
            wine.setExternalId(placementDto.getWine().getExternalId());
            wine = wineRepository.save(wine);
        }

        BottlePlacement placement = new BottlePlacement();
        placement.setSpace(space);
        placement.setWine(wine);
        placement.setQuantity(placementDto.getQuantity());
        placement.setDateAdded(placementDto.getDateAdded());
        placement.setPreferredStorageDuration(placementDto.getPreferredStorageDuration());
        placement.setNotes(placementDto.getNotes());
        
        return mapToBottlePlacementDto(bottlePlacementRepository.save(placement));
    }

    @Transactional
    public BottlePlacementDto updatePlacement(Long id, BottlePlacementDto placementDto) {
        BottlePlacement placement = bottlePlacementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Placement not found"));
        
        placement.setQuantity(placementDto.getQuantity());
        placement.setDateAdded(placementDto.getDateAdded());
        placement.setPreferredStorageDuration(placementDto.getPreferredStorageDuration());
        placement.setNotes(placementDto.getNotes());
        
        return mapToBottlePlacementDto(bottlePlacementRepository.save(placement));
    }

    @Transactional
    public void deletePlacement(Long id) {
        bottlePlacementRepository.deleteById(id);
    }

    private void generateSpacesForUnit(StorageUnit unit) {
        int rows = "shelf".equalsIgnoreCase(unit.getType()) ? 2 : 6;
        int cols = "shelf".equalsIgnoreCase(unit.getType()) ? 6 : 4;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Space space = new Space();
                space.setUnit(unit);
                space.setRow(r);
                space.setColumn(c);
                space.setCoordX(c * (unit.getWidth() / cols));
                space.setCoordY(r * (unit.getHeight() / rows));
                space.setCapacity(1);
                unit.getSpaces().add(space);
            }
        }
    }

    private CaveDto mapToCaveDto(Cave cave) {
        CaveDto dto = new CaveDto();
        dto.setId(cave.getId());
        dto.setName(cave.getName());
        dto.setDescription(cave.getDescription());
        dto.setDimensions(new CaveDto.DimensionsDto(cave.getWidth(), cave.getHeight(), cave.getDepth()));
        dto.setTemperature(cave.getTemperature());
        dto.setHumidity(cave.getHumidity());
        dto.setUnits(cave.getUnits().stream().map(this::mapToUnitDto).collect(Collectors.toList()));
        return dto;
    }

    private void updateCaveFromDto(Cave cave, CaveDto dto) {
        cave.setName(dto.getName());
        cave.setDescription(dto.getDescription());
        if (dto.getDimensions() != null) {
            cave.setWidth(dto.getDimensions().getWidth());
            cave.setHeight(dto.getDimensions().getHeight());
            cave.setDepth(dto.getDimensions().getDepth());
        }
        cave.setTemperature(dto.getTemperature());
        cave.setHumidity(dto.getHumidity());
    }

    private StorageUnitDto mapToUnitDto(StorageUnit unit) {
        StorageUnitDto dto = new StorageUnitDto();
        dto.setId(unit.getId());
        dto.setName(unit.getName());
        dto.setType(unit.getType());
        dto.setPosition(new StorageUnitDto.PositionDto(unit.getPosX(), unit.getPosY()));
        dto.setDimensions(new StorageUnitDto.DimensionsDto(unit.getWidth(), unit.getHeight(), unit.getDepth()));
        dto.setOrientation(unit.getOrientation());
        dto.setWall(unit.getWall());
        dto.setElevation(unit.getElevation());
        dto.setCapacity(unit.getCapacity());
        dto.setRotation(unit.getRotation());
        dto.setSpaces(unit.getSpaces().stream().map(this::mapToSpaceDto).collect(Collectors.toList()));
        return dto;
    }

    private void updateUnitFromDto(StorageUnit unit, StorageUnitDto dto) {
        unit.setName(dto.getName());
        unit.setType(dto.getType());
        if (dto.getPosition() != null) {
            unit.setPosX(dto.getPosition().getX());
            unit.setPosY(dto.getPosition().getY());
        }
        if (dto.getDimensions() != null) {
            unit.setWidth(dto.getDimensions().getWidth());
            unit.setHeight(dto.getDimensions().getHeight());
            unit.setDepth(dto.getDimensions().getDepth());
        }
        unit.setOrientation(dto.getOrientation());
        unit.setWall(dto.getWall());
        unit.setElevation(dto.getElevation());
        unit.setCapacity(dto.getCapacity());
        unit.setRotation(dto.getRotation());
    }

    private SpaceDto mapToSpaceDto(Space space) {
        SpaceDto dto = new SpaceDto();
        dto.setId(space.getId());
        dto.setUnitId(space.getUnit().getId());
        dto.setPosition(new SpaceDto.PositionDto(space.getRow(), space.getColumn()));
        dto.setCoordinates(new SpaceDto.CoordinatesDto(space.getCoordX(), space.getCoordY()));
        dto.setCapacity(space.getCapacity());
        dto.setCurrentBottles(space.getPlacements().stream().map(this::mapToBottlePlacementDto).collect(Collectors.toList()));
        return dto;
    }

    private BottlePlacementDto mapToBottlePlacementDto(BottlePlacement p) {
        BottlePlacementDto dto = new BottlePlacementDto();
        dto.setId(p.getId());
        dto.setSpaceId(p.getSpace().getId());
        dto.setQuantity(p.getQuantity());
        dto.setDateAdded(p.getDateAdded());
        dto.setPreferredStorageDuration(p.getPreferredStorageDuration());
        dto.setNotes(p.getNotes());
        
        if (p.getWine() != null) {
            WineDto wineDto = new WineDto();
            wineDto.setId(p.getWine().getId());
            wineDto.setName(p.getWine().getName());
            if (p.getWine().getWineType() != null) {
                wineDto.setWineTypeName(p.getWine().getWineType().getName());
            }
            wineDto.setRegion(p.getWine().getRegion());
            wineDto.setYear(p.getWine().getYear());
            wineDto.setProducer(p.getWine().getProducer());
            wineDto.setCountry(p.getWine().getCountry());
            wineDto.setAppellation(p.getWine().getAppellation());
            wineDto.setDescription(p.getWine().getDescription());
            wineDto.setAromaNotes(p.getWine().getAromaNotes());
            wineDto.setFoodPairings(p.getWine().getFoodPairings());
            wineDto.setImageUrl(p.getWine().getImageUrl());
            wineDto.setDataSource(p.getWine().getDataSource());
            wineDto.setExternalId(p.getWine().getExternalId());
            dto.setWine(wineDto);
        }
        
        return dto;
    }
}


package lu.caepatriot.oenobox.cellar.service;

import lu.caepatriot.oenobox.cellar.dto.AutoPlacementRequestDto;
import lu.caepatriot.oenobox.cellar.dto.BottlePlacementDto;
import lu.caepatriot.oenobox.cellar.dto.BulkPlacementRequestDto;
import lu.caepatriot.oenobox.cellar.dto.CaveDto;
import lu.caepatriot.oenobox.cellar.dto.SpaceBulkUpdateRequestDto;
import lu.caepatriot.oenobox.cellar.dto.SpaceDto;
import lu.caepatriot.oenobox.cellar.dto.StorageUnitDto;
import lu.caepatriot.oenobox.cellar.entity.BottlePlacement;
import lu.caepatriot.oenobox.cellar.entity.Cave;
import lu.caepatriot.oenobox.cellar.entity.Space;
import lu.caepatriot.oenobox.cellar.entity.StorageUnit;
import lu.caepatriot.oenobox.cellar.repository.BottlePlacementRepository;
import lu.caepatriot.oenobox.cellar.repository.CaveRepository;
import lu.caepatriot.oenobox.cellar.repository.SpaceRepository;
import lu.caepatriot.oenobox.cellar.repository.StorageUnitRepository;
import lu.caepatriot.oenobox.common.exception.ResourceNotFoundException;
import lu.caepatriot.oenobox.winecatalog.dto.WineDto;
import lu.caepatriot.oenobox.winecatalog.entity.Wine;
import lu.caepatriot.oenobox.winecatalog.entity.WineType;
import lu.caepatriot.oenobox.winecatalog.repository.WineRepository;
import lu.caepatriot.oenobox.winecatalog.repository.WineTypeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class CaveService {
    private final CaveRepository caveRepository;
    private final StorageUnitRepository storageUnitRepository;
    private final SpaceRepository spaceRepository;
    private final BottlePlacementRepository bottlePlacementRepository;
    private final WineRepository wineRepository;
    private final WineTypeRepository wineTypeRepository;

    public CaveService(
            CaveRepository caveRepository,
            StorageUnitRepository storageUnitRepository,
            SpaceRepository spaceRepository,
            BottlePlacementRepository bottlePlacementRepository,
            WineRepository wineRepository,
            WineTypeRepository wineTypeRepository
    ) {
        this.caveRepository = caveRepository;
        this.storageUnitRepository = storageUnitRepository;
        this.spaceRepository = spaceRepository;
        this.bottlePlacementRepository = bottlePlacementRepository;
        this.wineRepository = wineRepository;
        this.wineTypeRepository = wineTypeRepository;
    }

    @Transactional(readOnly = true)
    public List<CaveDto> getAllCaves() {
        return caveRepository.findAll().stream()
                .sorted(Comparator.comparing(Cave::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(this::mapToCaveDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CaveDto getCaveById(Long id) {
        return mapToCaveDto(findCave(id));
    }

    @Transactional
    public CaveDto createCave(CaveDto caveDto) {
        Cave cave = new Cave();
        updateCaveFromDto(cave, caveDto);
        if (cave.getUnits() == null) {
            cave.setUnits(new ArrayList<>());
        }
        if (caveDto.getUnits() != null && !caveDto.getUnits().isEmpty()) {
            replaceUnits(cave, caveDto.getUnits());
        }
        return mapToCaveDto(caveRepository.save(cave));
    }

    @Transactional
    public CaveDto importCave(CaveDto caveDto) {
        return createCave(caveDto);
    }

    @Transactional
    public CaveDto restoreCave(Long id, CaveDto caveDto) {
        Cave cave = findCave(id);
        updateCaveFromDto(cave, caveDto);
        replaceUnits(cave, caveDto.getUnits());
        return mapToCaveDto(caveRepository.save(cave));
    }

    @Transactional
    public CaveDto updateCave(Long id, CaveDto caveDto) {
        Cave cave = findCave(id);
        updateCaveFromDto(cave, caveDto);
        return mapToCaveDto(caveRepository.save(cave));
    }

    @Transactional
    public void deleteCave(Long id) {
        if (!caveRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cave not found");
        }
        caveRepository.deleteById(id);
    }

    @Transactional
    public StorageUnitDto createUnit(Long caveId, StorageUnitDto unitDto) {
        Cave cave = findCave(caveId);
        StorageUnit unit = new StorageUnit();
        unit.setCave(cave);
        updateUnitFromDto(unit, unitDto);
        rebuildSpaces(unit, unitDto);
        StorageUnit saved = storageUnitRepository.save(unit);
        cave.getUnits().add(saved);
        return mapToUnitDto(saved);
    }

    @Transactional
    public StorageUnitDto updateUnit(Long unitId, StorageUnitDto unitDto) {
        StorageUnit unit = findUnit(unitId);
        boolean layoutChanged = isLayoutDefinitionProvided(unitDto);
        boolean hasPlacements = unit.getSpaces().stream().flatMap(space -> space.getPlacements().stream()).findAny().isPresent();
        if (layoutChanged && hasPlacements) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change rack formula while bottles are assigned. Empty the rack first.");
        }
        updateUnitFromDto(unit, unitDto);
        if (layoutChanged || unit.getSpaces().isEmpty()) {
            rebuildSpaces(unit, unitDto);
        }
        return mapToUnitDto(storageUnitRepository.save(unit));
    }

    @Transactional
    public void deleteUnit(Long unitId) {
        if (!storageUnitRepository.existsById(unitId)) {
            throw new ResourceNotFoundException("Storage unit not found");
        }
        storageUnitRepository.deleteById(unitId);
    }

    @Transactional
    public SpaceDto updateSpace(Long spaceId, SpaceDto spaceDto) {
        Space space = findSpace(spaceId);
        applySpaceMetadata(space, spaceDto, null);
        return mapToSpaceDto(spaceRepository.save(space));
    }

    @Transactional
    public List<SpaceDto> bulkUpdateSpaces(SpaceBulkUpdateRequestDto requestDto) {
        if (requestDto.getSpaceIds() == null || requestDto.getSpaceIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one slot must be selected.");
        }

        List<Space> spaces = spaceRepository.findByIdIn(requestDto.getSpaceIds()).stream()
                .sorted(Comparator.comparing(Space::getSlotIndex, Comparator.nullsLast(Integer::compareTo)))
                .collect(Collectors.toList());

        AtomicInteger suffix = new AtomicInteger(1);
        for (Space space : spaces) {
            if (requestDto.getSlotLabelPrefix() != null && !requestDto.getSlotLabelPrefix().isBlank()) {
                space.setSlotLabel(requestDto.getSlotLabelPrefix().trim() + "-" + suffix.getAndIncrement());
            }
            if (requestDto.getPreferredWineGroup() != null) {
                space.setPreferredWineGroup(blankToNull(requestDto.getPreferredWineGroup()));
            }
            if (requestDto.getSectionName() != null) {
                space.setSectionName(blankToNull(requestDto.getSectionName()));
            }
            if (requestDto.getCapacity() != null) {
                int occupied = occupiedQuantity(space);
                if (requestDto.getCapacity() < occupied) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot capacity cannot be lower than current occupancy.");
                }
                space.setCapacity(requestDto.getCapacity());
            }
            if (requestDto.getDisabled() != null) {
                space.setDisabled(requestDto.getDisabled());
            }
            if (requestDto.getNotes() != null) {
                space.setNotes(blankToNull(requestDto.getNotes()));
            }
        }

        return spaceRepository.saveAll(spaces).stream().map(this::mapToSpaceDto).collect(Collectors.toList());
    }

    @Transactional
    public BottlePlacementDto addPlacement(BottlePlacementDto placementDto) {
        Space space = findSpace(placementDto.getSpaceId());
        Wine wine = resolveWine(placementDto.getWine());
        validatePlacement(space, defaultQuantity(placementDto.getQuantity()));

        BottlePlacement placement = new BottlePlacement();
        placement.setSpace(space);
        placement.setWine(wine);
        placement.setQuantity(defaultQuantity(placementDto.getQuantity()));
        placement.setDateAdded(Optional.ofNullable(placementDto.getDateAdded()).orElse(LocalDate.now()));
        placement.setPreferredStorageDuration(placementDto.getPreferredStorageDuration());
        placement.setNotes(blankToNull(placementDto.getNotes()));
        return mapToBottlePlacementDto(bottlePlacementRepository.save(placement));
    }

    @Transactional
    public List<BottlePlacementDto> bulkAddPlacements(BulkPlacementRequestDto requestDto) {
        if (requestDto.getSpaceIds() == null || requestDto.getSpaceIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Select at least one free slot.");
        }
        Wine wine = findWine(requestDto.getWineId());
        int quantityPerSpace = defaultQuantity(requestDto.getQuantityPerSpace());
        List<Space> spaces = spaceRepository.findByIdIn(requestDto.getSpaceIds()).stream()
                .sorted(Comparator.comparing(Space::getSlotIndex, Comparator.nullsLast(Integer::compareTo)))
                .collect(Collectors.toList());

        List<BottlePlacement> placements = new ArrayList<>();
        for (Space space : spaces) {
            validatePlacement(space, quantityPerSpace);
            BottlePlacement placement = new BottlePlacement();
            placement.setSpace(space);
            placement.setWine(wine);
            placement.setQuantity(quantityPerSpace);
            placement.setDateAdded(Optional.ofNullable(requestDto.getDateAdded()).orElse(LocalDate.now()));
            placement.setPreferredStorageDuration(requestDto.getPreferredStorageDuration());
            placement.setNotes(blankToNull(requestDto.getNotes()));
            placements.add(placement);
        }

        return bottlePlacementRepository.saveAll(placements).stream()
                .map(this::mapToBottlePlacementDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BottlePlacementDto> autoPlace(Long caveId, AutoPlacementRequestDto requestDto) {
        Cave cave = findCave(caveId);
        Wine wine = findWine(requestDto.getWineId());
        int requestedQuantity = Optional.ofNullable(requestDto.getQuantity()).orElse(1);
        String desiredGroup = firstNonBlank(requestDto.getPreferredGroup(), wineGroupCandidate(wine));

        List<Space> allSpaces = cave.getUnits().stream()
                .sorted(Comparator.comparing(StorageUnit::getDisplayOrder, Comparator.nullsLast(Integer::compareTo)).thenComparing(StorageUnit::getId))
                .flatMap(unit -> unit.getSpaces().stream())
                .sorted(Comparator.comparing(Space::getSlotIndex, Comparator.nullsLast(Integer::compareTo)).thenComparing(Space::getId))
                .collect(Collectors.toList());

        List<Space> preferredSpaces = allSpaces.stream()
                .filter(space -> isSlotAvailable(space, 1))
                .filter(space -> matchesPreferredGroup(space, desiredGroup))
                .collect(Collectors.toList());

        List<Space> fallbackSpaces = allSpaces.stream()
                .filter(space -> isSlotAvailable(space, 1))
                .filter(space -> !preferredSpaces.contains(space))
                .collect(Collectors.toList());

        List<Space> selectedSpaces = new ArrayList<>();
        selectedSpaces.addAll(preferredSpaces);
        selectedSpaces.addAll(fallbackSpaces);
        if (selectedSpaces.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No free slot is available in this cellar.");
        }

        selectedSpaces = selectedSpaces.stream().limit(requestedQuantity).collect(Collectors.toList());
        if (selectedSpaces.size() < requestedQuantity) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough free slots are available for this auto-placement.");
        }

        List<BottlePlacement> created = new ArrayList<>();
        for (Space space : selectedSpaces) {
            BottlePlacement placement = new BottlePlacement();
            placement.setSpace(space);
            placement.setWine(wine);
            placement.setQuantity(1);
            placement.setDateAdded(Optional.ofNullable(requestDto.getDateAdded()).orElse(LocalDate.now()));
            placement.setPreferredStorageDuration(requestDto.getPreferredStorageDuration());
            placement.setNotes(blankToNull(requestDto.getNotes()));
            created.add(placement);
        }

        return bottlePlacementRepository.saveAll(created).stream()
                .map(this::mapToBottlePlacementDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BottlePlacementDto updatePlacement(Long id, BottlePlacementDto placementDto) {
        BottlePlacement placement = bottlePlacementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bottle placement not found"));

        int newQuantity = defaultQuantity(placementDto.getQuantity());
        int occupiedByOthers = occupiedQuantity(placement.getSpace()) - Optional.ofNullable(placement.getQuantity()).orElse(0);
        if (occupiedByOthers + newQuantity > Optional.ofNullable(placement.getSpace().getCapacity()).orElse(1)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This slot does not have enough remaining capacity.");
        }

        if (placementDto.getWine() != null) {
            placement.setWine(resolveWine(placementDto.getWine()));
        }
        placement.setQuantity(newQuantity);
        placement.setDateAdded(Optional.ofNullable(placementDto.getDateAdded()).orElse(placement.getDateAdded()));
        placement.setPreferredStorageDuration(placementDto.getPreferredStorageDuration());
        placement.setNotes(blankToNull(placementDto.getNotes()));
        return mapToBottlePlacementDto(bottlePlacementRepository.save(placement));
    }

    @Transactional
    public void deletePlacement(Long id) {
        if (!bottlePlacementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bottle placement not found");
        }
        bottlePlacementRepository.deleteById(id);
    }

    private Cave findCave(Long caveId) {
        return caveRepository.findById(caveId).orElseThrow(() -> new ResourceNotFoundException("Cave not found"));
    }

    private StorageUnit findUnit(Long unitId) {
        return storageUnitRepository.findById(unitId).orElseThrow(() -> new ResourceNotFoundException("Storage unit not found"));
    }

    private Space findSpace(Long spaceId) {
        return spaceRepository.findById(spaceId).orElseThrow(() -> new ResourceNotFoundException("Slot not found"));
    }

    private Wine findWine(Long wineId) {
        return wineRepository.findById(wineId).orElseThrow(() -> new ResourceNotFoundException("Wine not found"));
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

    private void updateUnitFromDto(StorageUnit unit, StorageUnitDto dto) {
        unit.setName(dto.getName());
        unit.setType(firstNonBlank(dto.getType(), "rack"));
        unit.setTemplateKey(blankToNull(dto.getTemplateKey()));
        unit.setLayoutMode(firstNonBlank(dto.getLayoutMode(), "formula"));
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
        unit.setRowCount(dto.getRowCount());
        unit.setColumnCount(dto.getColumnCount());
        unit.setDefaultSpaceCapacity(Optional.ofNullable(dto.getDefaultSpaceCapacity()).orElse(1));
        unit.setDisplayOrder(Optional.ofNullable(dto.getDisplayOrder()).orElse(0));
        unit.setPreferredWineGroup(blankToNull(dto.getPreferredWineGroup()));
        unit.setNotes(blankToNull(dto.getNotes()));
    }

    private void replaceUnits(Cave cave, List<StorageUnitDto> unitDtos) {
        cave.getUnits().clear();
        if (unitDtos == null) {
            return;
        }
        List<StorageUnitDto> ordered = unitDtos.stream()
                .sorted(Comparator.comparing(StorageUnitDto::getDisplayOrder, Comparator.nullsLast(Integer::compareTo)))
                .collect(Collectors.toList());
        for (StorageUnitDto unitDto : ordered) {
            StorageUnit unit = new StorageUnit();
            unit.setCave(cave);
            updateUnitFromDto(unit, unitDto);
            rebuildSpaces(unit, unitDto);
            cave.getUnits().add(unit);
        }
    }

    private void rebuildSpaces(StorageUnit unit, StorageUnitDto unitDto) {
        unit.getSpaces().clear();
        if (unitDto.getSpaces() != null && !unitDto.getSpaces().isEmpty()) {
            AtomicInteger slotIndex = new AtomicInteger(1);
            List<SpaceDto> orderedSpaces = unitDto.getSpaces().stream()
                    .sorted(Comparator
                            .comparing((SpaceDto dto) -> dto.getPosition() != null ? dto.getPosition().getRow() : 0, Comparator.nullsLast(Integer::compareTo))
                            .thenComparing(dto -> dto.getPosition() != null ? dto.getPosition().getColumn() : 0, Comparator.nullsLast(Integer::compareTo)))
                    .collect(Collectors.toList());
            for (SpaceDto spaceDto : orderedSpaces) {
                Space space = new Space();
                space.setUnit(unit);
                applySpaceMetadata(space, spaceDto, slotIndex.getAndIncrement());
                addPlacementsFromDto(space, spaceDto.getCurrentBottles());
                unit.getSpaces().add(space);
            }
            refreshUnitFormulaFromSpaces(unit);
            unit.setCapacity(unit.getSpaces().stream().map(Space::getCapacity).filter(Objects::nonNull).reduce(0, Integer::sum));
            return;
        }

        List<Integer> rowCapacities = normalizeRowCapacities(unitDto);
        AtomicInteger slotIndex = new AtomicInteger(1);
        for (int row = 0; row < rowCapacities.size(); row++) {
            int columns = rowCapacities.get(row);
            for (int column = 0; column < columns; column++) {
                Space space = new Space();
                space.setUnit(unit);
                space.setRow(row + 1);
                space.setColumn(column + 1);
                space.setCoordX((double) column);
                space.setCoordY((double) row);
                space.setCapacity(Optional.ofNullable(unit.getDefaultSpaceCapacity()).orElse(1));
                space.setSlotIndex(slotIndex.getAndIncrement());
                space.setSlotLabel(buildSlotLabel(row + 1, column + 1));
                space.setSectionName(unit.getName());
                space.setPreferredWineGroup(unit.getPreferredWineGroup());
                space.setDisabled(false);
                unit.getSpaces().add(space);
            }
        }
        unit.setRowCount(rowCapacities.size());
        unit.setColumnCount(rowCapacities.stream().max(Integer::compareTo).orElse(0));
        unit.setCapacity(unit.getSpaces().stream().map(Space::getCapacity).filter(Objects::nonNull).reduce(0, Integer::sum));
    }

    private void addPlacementsFromDto(Space space, List<BottlePlacementDto> placementDtos) {
        if (placementDtos == null) {
            return;
        }
        for (BottlePlacementDto placementDto : placementDtos) {
            if (placementDto.getWine() == null) {
                continue;
            }
            BottlePlacement placement = new BottlePlacement();
            placement.setSpace(space);
            placement.setWine(resolveWine(placementDto.getWine()));
            placement.setQuantity(defaultQuantity(placementDto.getQuantity()));
            placement.setDateAdded(Optional.ofNullable(placementDto.getDateAdded()).orElse(LocalDate.now()));
            placement.setPreferredStorageDuration(placementDto.getPreferredStorageDuration());
            placement.setNotes(blankToNull(placementDto.getNotes()));
            space.getPlacements().add(placement);
        }
    }

    private void refreshUnitFormulaFromSpaces(StorageUnit unit) {
        Map<Integer, Long> counts = unit.getSpaces().stream()
                .filter(space -> space.getRow() != null)
                .collect(Collectors.groupingBy(Space::getRow, LinkedHashMap::new, Collectors.counting()));
        unit.setRowCount(counts.size());
        unit.setColumnCount(counts.values().stream().map(Long::intValue).max(Integer::compareTo).orElse(0));
    }

    private void applySpaceMetadata(Space space, SpaceDto dto, Integer fallbackSlotIndex) {
        if (dto.getPosition() != null) {
            space.setRow(dto.getPosition().getRow());
            space.setColumn(dto.getPosition().getColumn());
        }
        if (dto.getCoordinates() != null) {
            space.setCoordX(dto.getCoordinates().getX());
            space.setCoordY(dto.getCoordinates().getY());
        } else {
            space.setCoordX(space.getColumn() != null ? space.getColumn().doubleValue() - 1 : 0d);
            space.setCoordY(space.getRow() != null ? space.getRow().doubleValue() - 1 : 0d);
        }
        space.setCapacity(Optional.ofNullable(dto.getCapacity()).orElse(Optional.ofNullable(space.getCapacity()).orElse(1)));
        space.setSlotIndex(Optional.ofNullable(dto.getSlotIndex()).orElseGet(() -> Optional.ofNullable(fallbackSlotIndex).orElse(1)));
        space.setSlotLabel(firstNonBlank(dto.getSlotLabel(), buildSlotLabel(Optional.ofNullable(space.getRow()).orElse(1), Optional.ofNullable(space.getColumn()).orElse(1))));
        if (dto.getPreferredWineGroup() != null || fallbackSlotIndex != null) {
            space.setPreferredWineGroup(blankToNull(dto.getPreferredWineGroup()));
        }
        if (dto.getSectionName() != null || fallbackSlotIndex != null) {
            space.setSectionName(blankToNull(dto.getSectionName()));
        }
        if (dto.getDisabled() != null || fallbackSlotIndex != null) {
            space.setDisabled(Optional.ofNullable(dto.getDisabled()).orElse(false));
        }
        if (dto.getNotes() != null || fallbackSlotIndex != null) {
            space.setNotes(blankToNull(dto.getNotes()));
        }
    }

    private List<Integer> normalizeRowCapacities(StorageUnitDto unitDto) {
        if (unitDto.getRowCapacities() != null && !unitDto.getRowCapacities().isEmpty()) {
            return unitDto.getRowCapacities().stream()
                    .map(capacity -> Optional.ofNullable(capacity).orElse(0))
                    .filter(capacity -> capacity > 0)
                    .collect(Collectors.toList());
        }

        Integer rows = Optional.ofNullable(unitDto.getRowCount()).filter(value -> value > 0).orElse(null);
        Integer columns = Optional.ofNullable(unitDto.getColumnCount()).filter(value -> value > 0).orElse(null);
        Integer totalSpaces = Optional.ofNullable(unitDto.getTotalSpaces()).filter(value -> value > 0).orElse(null);

        if (rows != null && columns != null) {
            return java.util.stream.IntStream.range(0, rows).mapToObj(index -> columns).collect(Collectors.toList());
        }

        if (rows != null && totalSpaces != null) {
            List<Integer> rowCapacities = new ArrayList<>();
            int base = totalSpaces / rows;
            int remainder = totalSpaces % rows;
            for (int row = 0; row < rows; row++) {
                rowCapacities.add(base + (row < remainder ? 1 : 0));
            }
            return rowCapacities;
        }

        if (totalSpaces != null) {
            return List.of(totalSpaces);
        }

        return List.of(12);
    }

    private boolean isLayoutDefinitionProvided(StorageUnitDto dto) {
        return (dto.getRowCapacities() != null && !dto.getRowCapacities().isEmpty())
                || dto.getRowCount() != null
                || dto.getColumnCount() != null
                || dto.getTotalSpaces() != null
                || (dto.getSpaces() != null && !dto.getSpaces().isEmpty());
    }

    private Wine resolveWine(WineDto wineDto) {
        if (wineDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wine data is required.");
        }
        if (wineDto.getId() != null) {
            return findWine(wineDto.getId());
        }
        if (wineDto.getName() == null || wineDto.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wine name is required.");
        }
        Wine wine = new Wine();
        wine.setName(wineDto.getName());
        wine.setRegion(wineDto.getRegion());
        wine.setYear(wineDto.getYear());
        wine.setProducer(wineDto.getProducer());
        wine.setCountry(wineDto.getCountry());
        wine.setAppellation(wineDto.getAppellation());
        wine.setDescription(wineDto.getDescription());
        wine.setImageUrl(wineDto.getImageUrl());
        if (wineDto.getWineTypeName() != null && !wineDto.getWineTypeName().isBlank()) {
            WineType wineType = wineTypeRepository.findByNameIgnoreCase(wineDto.getWineTypeName().trim())
                    .orElseGet(() -> {
                        WineType created = new WineType();
                        created.setName(wineDto.getWineTypeName().trim());
                        return wineTypeRepository.save(created);
                    });
            wine.setWineType(wineType);
        }
        return wineRepository.save(wine);
    }

    private void validatePlacement(Space space, int requestedQuantity) {
        if (Boolean.TRUE.equals(space.getDisabled())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This slot is disabled.");
        }
        int occupied = occupiedQuantity(space);
        int capacity = Optional.ofNullable(space.getCapacity()).orElse(1);
        if (occupied + requestedQuantity > capacity) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This slot does not have enough remaining capacity.");
        }
    }

    private int occupiedQuantity(Space space) {
        return space.getPlacements().stream().map(BottlePlacement::getQuantity).filter(Objects::nonNull).reduce(0, Integer::sum);
    }

    private boolean isSlotAvailable(Space space, int requestedQuantity) {
        return !Boolean.TRUE.equals(space.getDisabled())
                && occupiedQuantity(space) + requestedQuantity <= Optional.ofNullable(space.getCapacity()).orElse(1);
    }

    private boolean matchesPreferredGroup(Space space, String preferredGroup) {
        if (preferredGroup == null || preferredGroup.isBlank()) {
            return true;
        }
        String normalizedPreferredGroup = preferredGroup.trim().toLowerCase(Locale.ROOT);
        return normalizeGroup(space.getPreferredWineGroup()).equals(normalizedPreferredGroup)
                || normalizeGroup(space.getSectionName()).equals(normalizedPreferredGroup)
                || normalizeGroup(space.getUnit().getPreferredWineGroup()).equals(normalizedPreferredGroup)
                || normalizeGroup(space.getUnit().getName()).equals(normalizedPreferredGroup);
    }

    private String normalizeGroup(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private String wineGroupCandidate(Wine wine) {
        return firstNonBlank(
                wine.getWineType() != null ? wine.getWineType().getName() : null,
                wine.getRegion(),
                wine.getAppellation()
        );
    }

    private String buildSlotLabel(int row, int column) {
        return "R" + row + "-S" + column;
    }

    private int defaultQuantity(Integer quantity) {
        return quantity == null || quantity < 1 ? 1 : quantity;
    }

    private String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private CaveDto mapToCaveDto(Cave cave) {
        CaveDto dto = new CaveDto();
        dto.setId(cave.getId());
        dto.setName(cave.getName());
        dto.setDescription(cave.getDescription());
        dto.setDimensions(new CaveDto.DimensionsDto(cave.getWidth(), cave.getHeight(), cave.getDepth()));
        dto.setTemperature(cave.getTemperature());
        dto.setHumidity(cave.getHumidity());
        List<StorageUnitDto> units = cave.getUnits().stream()
                .sorted(Comparator.comparing(StorageUnit::getDisplayOrder, Comparator.nullsLast(Integer::compareTo)).thenComparing(StorageUnit::getId))
                .map(this::mapToUnitDto)
                .collect(Collectors.toList());
        dto.setUnits(units);
        int totalSpaces = units.stream().map(StorageUnitDto::getSpaces).filter(Objects::nonNull).mapToInt(List::size).sum();
        int occupiedSpaces = units.stream().flatMap(unit -> unit.getSpaces().stream()).mapToInt(space -> Optional.ofNullable(space.getOccupiedQuantity()).orElse(0) > 0 ? 1 : 0).sum();
        dto.setTotalSpaces(totalSpaces);
        dto.setOccupiedSpaces(occupiedSpaces);
        dto.setFreeSpaces(Math.max(totalSpaces - occupiedSpaces, 0));
        return dto;
    }

    private StorageUnitDto mapToUnitDto(StorageUnit unit) {
        StorageUnitDto dto = new StorageUnitDto();
        dto.setId(unit.getId());
        dto.setName(unit.getName());
        dto.setType(unit.getType());
        dto.setTemplateKey(unit.getTemplateKey());
        dto.setLayoutMode(unit.getLayoutMode());
        dto.setPosition(new StorageUnitDto.PositionDto(unit.getPosX(), unit.getPosY()));
        dto.setDimensions(new StorageUnitDto.DimensionsDto(unit.getWidth(), unit.getHeight(), unit.getDepth()));
        dto.setOrientation(unit.getOrientation());
        dto.setWall(unit.getWall());
        dto.setElevation(unit.getElevation());
        dto.setCapacity(unit.getCapacity());
        dto.setRotation(unit.getRotation());
        dto.setRowCount(unit.getRowCount());
        dto.setColumnCount(unit.getColumnCount());
        dto.setDefaultSpaceCapacity(unit.getDefaultSpaceCapacity());
        dto.setDisplayOrder(unit.getDisplayOrder());
        dto.setPreferredWineGroup(unit.getPreferredWineGroup());
        dto.setNotes(unit.getNotes());
        dto.setSpaces(unit.getSpaces().stream().sorted(Comparator.comparing(Space::getSlotIndex, Comparator.nullsLast(Integer::compareTo))).map(this::mapToSpaceDto).collect(Collectors.toList()));
        dto.setTotalSpaces(dto.getSpaces().size());

        Map<Integer, Long> counts = unit.getSpaces().stream()
                .filter(space -> space.getRow() != null)
                .collect(Collectors.groupingBy(Space::getRow, LinkedHashMap::new, Collectors.counting()));
        dto.setRowCapacities(counts.values().stream().map(Long::intValue).collect(Collectors.toList()));
        if ((dto.getRowCount() == null || dto.getRowCount() == 0) && !counts.isEmpty()) {
            dto.setRowCount(counts.size());
        }
        if ((dto.getColumnCount() == null || dto.getColumnCount() == 0) && !counts.isEmpty()) {
            dto.setColumnCount(counts.values().stream().map(Long::intValue).max(Integer::compareTo).orElse(0));
        }
        return dto;
    }

    private SpaceDto mapToSpaceDto(Space space) {
        SpaceDto dto = new SpaceDto();
        dto.setId(space.getId());
        dto.setUnitId(space.getUnit().getId());
        dto.setPosition(new SpaceDto.PositionDto(space.getRow(), space.getColumn()));
        dto.setCoordinates(new SpaceDto.CoordinatesDto(space.getCoordX(), space.getCoordY()));
        dto.setCapacity(space.getCapacity());
        dto.setSlotIndex(space.getSlotIndex());
        dto.setSlotLabel(space.getSlotLabel());
        dto.setPreferredWineGroup(space.getPreferredWineGroup());
        dto.setSectionName(space.getSectionName());
        dto.setDisabled(space.getDisabled());
        dto.setNotes(space.getNotes());
        dto.setCurrentBottles(space.getPlacements().stream().map(this::mapToBottlePlacementDto).collect(Collectors.toList()));
        int occupied = occupiedQuantity(space);
        dto.setOccupiedQuantity(occupied);
        dto.setFreeCapacity(Math.max(Optional.ofNullable(space.getCapacity()).orElse(1) - occupied, 0));
        return dto;
    }

    private BottlePlacementDto mapToBottlePlacementDto(BottlePlacement placement) {
        BottlePlacementDto dto = new BottlePlacementDto();
        dto.setId(placement.getId());
        dto.setSpaceId(placement.getSpace().getId());
        dto.setQuantity(placement.getQuantity());
        dto.setDateAdded(placement.getDateAdded());
        dto.setPreferredStorageDuration(placement.getPreferredStorageDuration());
        dto.setNotes(placement.getNotes());
        if (placement.getWine() != null) {
            Wine wine = placement.getWine();
            WineDto wineDto = new WineDto();
            wineDto.setId(wine.getId());
            wineDto.setName(wine.getName());
            wineDto.setRegion(wine.getRegion());
            wineDto.setYear(wine.getYear());
            wineDto.setProducer(wine.getProducer());
            wineDto.setCountry(wine.getCountry());
            wineDto.setAppellation(wine.getAppellation());
            wineDto.setDescription(wine.getDescription());
            wineDto.setImageUrl(wine.getImageUrl());
            if (wine.getWineType() != null) {
                wineDto.setWineTypeName(wine.getWineType().getName());
            }
            dto.setWine(wineDto);
        }
        return dto;
    }
}

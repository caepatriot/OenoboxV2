package lu.caepatriot.oenobox.cellar.controller;

import lu.caepatriot.oenobox.cellar.dto.AutoPlacementRequestDto;
import lu.caepatriot.oenobox.cellar.dto.BottlePlacementDto;
import lu.caepatriot.oenobox.cellar.dto.BulkPlacementRequestDto;
import lu.caepatriot.oenobox.cellar.dto.CaveDto;
import lu.caepatriot.oenobox.cellar.dto.SpaceBulkUpdateRequestDto;
import lu.caepatriot.oenobox.cellar.dto.SpaceDto;
import lu.caepatriot.oenobox.cellar.dto.StorageUnitDto;
import lu.caepatriot.oenobox.cellar.service.CaveService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/caves")
@CrossOrigin(origins = "*")
public class CaveController {
    private final CaveService caveService;

    public CaveController(CaveService caveService) {
        this.caveService = caveService;
    }

    @GetMapping
    public List<CaveDto> getAllCaves() {
        return caveService.getAllCaves();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaveDto> getCaveById(@PathVariable Long id) {
        return ResponseEntity.ok(caveService.getCaveById(id));
    }

    @GetMapping("/{id}/workspace")
    public ResponseEntity<CaveDto> getWorkspace(@PathVariable Long id) {
        return ResponseEntity.ok(caveService.getCaveById(id));
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<CaveDto> exportCave(@PathVariable Long id) {
        return ResponseEntity.ok(caveService.getCaveById(id));
    }

    @PostMapping
    public CaveDto createCave(@RequestBody CaveDto caveDto) {
        return caveService.createCave(caveDto);
    }

    @PostMapping("/import")
    public CaveDto importCave(@RequestBody CaveDto caveDto) {
        return caveService.importCave(caveDto);
    }

    @PutMapping("/{id}")
    public CaveDto updateCave(@PathVariable Long id, @RequestBody CaveDto caveDto) {
        return caveService.updateCave(id, caveDto);
    }

    @PutMapping("/{id}/restore")
    public CaveDto restoreCave(@PathVariable Long id, @RequestBody CaveDto caveDto) {
        return caveService.restoreCave(id, caveDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCave(@PathVariable Long id) {
        caveService.deleteCave(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{caveId}/units")
    public StorageUnitDto createUnit(@PathVariable Long caveId, @RequestBody StorageUnitDto unitDto) {
        return caveService.createUnit(caveId, unitDto);
    }

    @PutMapping("/units/{unitId}")
    public StorageUnitDto updateUnit(@PathVariable Long unitId, @RequestBody StorageUnitDto unitDto) {
        return caveService.updateUnit(unitId, unitDto);
    }

    @DeleteMapping("/units/{unitId}")
    public ResponseEntity<Void> deleteUnit(@PathVariable Long unitId) {
        caveService.deleteUnit(unitId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/spaces/{spaceId}")
    public SpaceDto updateSpace(@PathVariable Long spaceId, @RequestBody SpaceDto spaceDto) {
        return caveService.updateSpace(spaceId, spaceDto);
    }

    @PostMapping("/spaces/bulk")
    public List<SpaceDto> bulkUpdateSpaces(@RequestBody SpaceBulkUpdateRequestDto requestDto) {
        return caveService.bulkUpdateSpaces(requestDto);
    }

    @PostMapping("/placements")
    public BottlePlacementDto addPlacement(@RequestBody BottlePlacementDto placementDto) {
        return caveService.addPlacement(placementDto);
    }

    @PostMapping("/placements/bulk")
    public List<BottlePlacementDto> bulkAddPlacements(@RequestBody BulkPlacementRequestDto requestDto) {
        return caveService.bulkAddPlacements(requestDto);
    }

    @PostMapping("/{caveId}/auto-place")
    public List<BottlePlacementDto> autoPlace(@PathVariable Long caveId, @RequestBody AutoPlacementRequestDto requestDto) {
        return caveService.autoPlace(caveId, requestDto);
    }

    @PutMapping("/placements/{id}")
    public BottlePlacementDto updatePlacement(@PathVariable Long id, @RequestBody BottlePlacementDto placementDto) {
        return caveService.updatePlacement(id, placementDto);
    }

    @DeleteMapping("/placements/{id}")
    public ResponseEntity<Void> deletePlacement(@PathVariable Long id) {
        caveService.deletePlacement(id);
        return ResponseEntity.noContent().build();
    }
}

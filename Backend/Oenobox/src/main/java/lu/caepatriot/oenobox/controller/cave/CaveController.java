package lu.caepatriot.oenobox.controller.cave;

import lu.caepatriot.oenobox.dto.cave.BottlePlacementDto;
import lu.caepatriot.oenobox.dto.cave.CaveDto;
import lu.caepatriot.oenobox.entity.cave.Cave;
import lu.caepatriot.oenobox.service.cave.CaveService;
import lu.caepatriot.oenobox.dto.cave.StorageUnitDto;

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

    @PostMapping
    public CaveDto createCave(@RequestBody CaveDto caveDto) {
        return caveService.createCave(caveDto);
    }

    @PutMapping("/{id}")
    public CaveDto updateCave(@PathVariable Long id, @RequestBody CaveDto caveDto) {
        return caveService.updateCave(id, caveDto);
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

    @PostMapping("/placements")
    public BottlePlacementDto addPlacement(@RequestBody BottlePlacementDto placementDto) {
        return caveService.addPlacement(placementDto);
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

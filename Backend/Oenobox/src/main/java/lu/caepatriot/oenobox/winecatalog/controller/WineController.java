package lu.caepatriot.oenobox.winecatalog.controller;

import lu.caepatriot.oenobox.winecatalog.service.WineService;
import lu.caepatriot.oenobox.winecatalog.dto.WineDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wines")
@CrossOrigin(origins = "*")
@Tag(name = "Wines", description = "API for managing wines")
public class WineController {
    private final WineService wineService;

    public WineController(WineService wineService) {
        this.wineService = wineService;
    }

    @GetMapping
    @Operation(summary = "List wines", description = "Returns all wines or filtered wines when a query is provided.")
    public List<WineDto> getAllWines(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String producer,
            @RequestParam(required = false, name = "wineType") String wineType
    ) {
        return wineService.getWines(query, region, country, producer, wineType);
    }

    @PostMapping
    @Operation(summary = "Create wine", description = "Creates a wine in the shared catalog. If a duplicate exists, the existing wine is returned.")
    public WineDto createWine(@RequestBody WineDto wineDto) {
        return wineService.createWine(wineDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get wine by ID", description = "Retrieves one wine with full details.")
    public WineDto getWineById(@PathVariable Long id) {
        return wineService.getWineById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update wine", description = "Updates an existing shared wine reference.")
    public WineDto updateWine(@PathVariable Long id, @RequestBody WineDto wineDto) {
        return wineService.updateWine(id, wineDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete wine", description = "Deletes a wine from catalog.")
    public ResponseEntity<Void> deleteWine(@PathVariable Long id) {
        wineService.deleteWine(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/suggestions")
    @Operation(summary = "Get wine suggestions", description = "Returns local and public-source suggestions to pre-fill add forms.")
    public List<WineDto> getSuggestions(@RequestParam String query) {
        return wineService.getWineSuggestions(query);
    }
}


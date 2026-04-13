package lu.caepatriot.oenobox.winecatalog.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lu.caepatriot.oenobox.winecatalog.dto.WineDto;
import lu.caepatriot.oenobox.winecatalog.service.WineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wines")
@Tag(name = "Wines", description = "API for managing wines")
public class WineController {
    private final WineService wineService;

    public WineController(WineService wineService) {
        this.wineService = wineService;
    }

    @GetMapping
    public List<WineDto> getAllWines(@RequestParam(required = false) String search) {
        return wineService.search(search);
    }

    @GetMapping("/{id}")
    public WineDto getWineById(@PathVariable Long id) {
        return wineService.getWineById(id);
    }

    @PostMapping
    public WineDto createWine(@RequestBody WineDto wineDto) {
        return wineService.createWine(wineDto);
    }

    @PutMapping("/{id}")
    public WineDto updateWine(@PathVariable Long id, @RequestBody WineDto wineDto) {
        return wineService.updateWine(id, wineDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWine(@PathVariable Long id) {
        wineService.deleteWine(id);
        return ResponseEntity.noContent().build();
    }
}

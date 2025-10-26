package lu.caepatriot.oenobox.controller;

import lu.caepatriot.oenobox.dto.TastingDto;
import lu.caepatriot.oenobox.service.TastingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tastings")
@CrossOrigin(origins = "*")
public class TastingController {

    @Autowired
    private TastingService tastingService;

    // GET /api/tastings - Get all tastings
    @GetMapping
    public ResponseEntity<List<TastingDto>> getAllTastings() {
        List<TastingDto> tastings = tastingService.findAll();
        return ResponseEntity.ok(tastings);
    }

    // GET /api/tastings/{id} - Get tasting by ID
    @GetMapping("/{id}")
    public ResponseEntity<TastingDto> getTastingById(@PathVariable Long id) {
        Optional<TastingDto> tasting = tastingService.findById(id);
        return tasting.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/tastings - Create new tasting
    @PostMapping
    public ResponseEntity<TastingDto> createTasting(@RequestBody TastingDto tastingDto) {
        try {
            System.out.println("Received tasting data: " + tastingDto);
            System.out.println("robeRouge value: " + tastingDto.getRobeRouge());
            System.out.println("robeRouge type: " + (tastingDto.getRobeRouge() != null ? tastingDto.getRobeRouge().getClass() : "null"));
            TastingDto savedTasting = tastingService.save(tastingDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTasting);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // PUT /api/tastings/{id} - Update tasting
    @PutMapping("/{id}")
    public ResponseEntity<TastingDto> updateTasting(@PathVariable Long id, @RequestBody TastingDto tastingDto) {
        try {
            TastingDto updatedTasting = tastingService.update(id, tastingDto);
            return ResponseEntity.ok(updatedTasting);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // DELETE /api/tastings/{id} - Delete tasting
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTasting(@PathVariable Long id) {
        try {
            tastingService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/tastings/wine-type/{wineType} - Get tastings by wine type
    @GetMapping("/wine-type/{wineType}")
    public ResponseEntity<List<TastingDto>> getTastingsByWineType(@PathVariable String wineType) {
        List<TastingDto> tastings = tastingService.findByWineType(wineType);
        return ResponseEntity.ok(tastings);
    }

    // GET /api/tastings/region/{region} - Get tastings by region
    @GetMapping("/region/{region}")
    public ResponseEntity<List<TastingDto>> getTastingsByRegion(@PathVariable String region) {
        List<TastingDto> tastings = tastingService.findByRegion(region);
        return ResponseEntity.ok(tastings);
    }

    // GET /api/tastings/cepage/{cepage} - Get tastings by cepage
    @GetMapping("/cepage/{cepage}")
    public ResponseEntity<List<TastingDto>> getTastingsByCepage(@PathVariable String cepage) {
        List<TastingDto> tastings = tastingService.findByCepage(cepage);
        return ResponseEntity.ok(tastings);
    }

    // GET /api/tastings/recent - Get recent tastings
    @GetMapping("/recent")
    public ResponseEntity<List<TastingDto>> getRecentTastings() {
        List<TastingDto> tastings = tastingService.findRecentTastings();
        return ResponseEntity.ok(tastings);
    }

    // GET /api/tastings/count/wine-type/{wineType} - Count tastings by wine type
    @GetMapping("/count/wine-type/{wineType}")
    public ResponseEntity<Long> countTastingsByWineType(@PathVariable String wineType) {
        Long count = tastingService.countByWineType(wineType);
        return ResponseEntity.ok(count);
    }

    // GET /api/tastings/search - Search tastings with parameters
    @GetMapping("/search")
    public ResponseEntity<List<TastingDto>> searchTastings(
            @RequestParam(required = false) String wineType,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String cepage,
            @RequestParam(required = false) String noteFinale) {

        List<TastingDto> results;

        if (wineType != null) {
            results = tastingService.findByWineType(wineType);
        } else if (region != null) {
            results = tastingService.findByRegion(region);
        } else if (cepage != null) {
            results = tastingService.findByCepage(cepage);
        } else {
            results = tastingService.findAll();
        }

        return ResponseEntity.ok(results);
    }

    // GET /api/tastings/steps - Get tasting steps configuration
    @GetMapping("/steps")
    public ResponseEntity<List<Object>> getTastingSteps() {
        // For now, return a simple list of steps. In a real application,
        // this could be loaded from a database or configuration file
        List<Object> steps = List.of(
            Map.of("step", 1, "name", "informations", "title", "Informations sur le Vin"),
            Map.of("step", 2, "name", "visuel", "title", "Aspect Visuel"),
            Map.of("step", 3, "name", "nez", "title", "Nez"),
            Map.of("step", 4, "name", "bouche", "title", "Bouche"),
            Map.of("step", 5, "name", "longueur", "title", "Longueur en bouche"),
            Map.of("step", 6, "name", "conclusion", "title", "Conclusion")
        );
        return ResponseEntity.ok(steps);
    }
}
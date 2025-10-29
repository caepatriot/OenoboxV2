package lu.caepatriot.oenobox.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lu.caepatriot.oenobox.dto.TastingStepDto;
import lu.caepatriot.oenobox.service.TastingStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasting-steps")
@CrossOrigin(origins = "*")
@Tag(name = "Tasting Steps", description = "API for managing tasting steps")
public class TastingStepController {

    @Autowired
    private TastingStepService tastingStepService;

    @GetMapping
    @Operation(summary = "Get all tasting steps", description = "Retrieves a list of all tasting steps")
    public ResponseEntity<List<TastingStepDto>> getAllTastingSteps() {
        List<TastingStepDto> steps = tastingStepService.getAllTastingSteps();
        return ResponseEntity.ok(steps);
    }

    @GetMapping("/by-wine-type/{wineType}")
    @Operation(summary = "Get tasting steps by wine type", description = "Retrieves tasting steps filtered by wine type for dynamic form construction")
    public ResponseEntity<List<TastingStepDto>> getTastingStepsByWineType(@PathVariable String wineType) {
        List<TastingStepDto> steps = tastingStepService.getTastingStepsByWineType(wineType);
        return ResponseEntity.ok(steps);
    }
}
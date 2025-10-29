package lu.caepatriot.oenobox.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lu.caepatriot.oenobox.dto.TastingFieldDto;
import lu.caepatriot.oenobox.service.TastingFieldService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasting-fields")
@Tag(name = "Tasting Fields", description = "API for managing tasting form fields")
public class TastingFieldController {
    private final TastingFieldService tastingFieldService;

    public TastingFieldController(TastingFieldService tastingFieldService) {
        this.tastingFieldService = tastingFieldService;
    }

    @GetMapping("/step/{stepId}")
    @Operation(summary = "Get tasting fields by step", description = "Retrieves all tasting fields for a specific tasting step")
    public List<TastingFieldDto> getFieldsByStep(
            @Parameter(description = "ID of the tasting step") @PathVariable Long stepId) {
        return tastingFieldService.getFieldsByStep(stepId);
    }

    @GetMapping("/step/{stepId}/wine-type/{wineType}")
    @Operation(summary = "Get tasting fields by step and wine type", description = "Retrieves tasting fields for a specific step, filtered by wine type restrictions")
    public List<TastingFieldDto> getFieldsByStepAndWineType(
            @Parameter(description = "ID of the tasting step") @PathVariable Long stepId,
            @Parameter(description = "Wine type to filter by") @PathVariable String wineType) {
        return tastingFieldService.getFieldsByStepAndWineType(stepId, wineType);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tasting field by ID", description = "Retrieves a specific tasting field by its ID")
    public TastingFieldDto getFieldById(
            @Parameter(description = "ID of the tasting field") @PathVariable Long id) {
        return tastingFieldService.getFieldById(id);
    }
}
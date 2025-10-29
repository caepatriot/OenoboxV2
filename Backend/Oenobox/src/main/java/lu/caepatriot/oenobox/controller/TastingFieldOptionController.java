package lu.caepatriot.oenobox.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lu.caepatriot.oenobox.dto.TastingFieldOptionDto;
import lu.caepatriot.oenobox.service.TastingFieldOptionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasting-field-options")
@Tag(name = "Tasting Field Options", description = "API for managing tasting field options")
public class TastingFieldOptionController {
    private final TastingFieldOptionService tastingFieldOptionService;

    public TastingFieldOptionController(TastingFieldOptionService tastingFieldOptionService) {
        this.tastingFieldOptionService = tastingFieldOptionService;
    }

    @GetMapping("/field/{fieldId}")
    @Operation(summary = "Get tasting field options by field ID", description = "Retrieves all options for a specific tasting field")
    public List<TastingFieldOptionDto> getOptionsByFieldId(
            @Parameter(description = "ID of the tasting field") @PathVariable Long fieldId) {
        return tastingFieldOptionService.getOptionsByFieldId(fieldId);
    }

    @GetMapping("/field/{fieldId}/wine-type/{wineType}")
    @Operation(summary = "Get tasting field options by field ID and wine type", description = "Retrieves options for a specific field, filtered by wine type restrictions")
    public List<TastingFieldOptionDto> getOptionsByFieldIdAndWineType(
            @Parameter(description = "ID of the tasting field") @PathVariable Long fieldId,
            @Parameter(description = "Wine type to filter by") @PathVariable String wineType) {
        return tastingFieldOptionService.getOptionsByFieldIdAndWineType(fieldId, wineType);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tasting field option by ID", description = "Retrieves a specific tasting field option by its ID")
    public TastingFieldOptionDto getOptionById(
            @Parameter(description = "ID of the tasting field option") @PathVariable Long id) {
        return tastingFieldOptionService.getOptionById(id);
    }
}
package lu.caepatriot.oenobox.winecatalog.controller;

import lu.caepatriot.oenobox.winecatalog.dto.WineTypeDto;
import lu.caepatriot.oenobox.winecatalog.entity.Wine;
import lu.caepatriot.oenobox.winecatalog.service.WineTypeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wine-types")
@Tag(name = "Wine Types", description = "API for managing wine types")
public class WineTypeController {
    private final WineTypeService wineTypeService;

    public WineTypeController(WineTypeService wineTypeService) {
        this.wineTypeService = wineTypeService;
    }

    @GetMapping
    @Operation(summary = "Get all wine types", description = "Retrieves a list of all wine types")
    public List<WineTypeDto> getAllWineTypes() {
        return wineTypeService.getAllWineTypes();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get wine type by ID", description = "Retrieves a specific wine type by its ID")
    public WineTypeDto getWineTypeById(@PathVariable Long id) {
        return wineTypeService.getWineTypeById(id);
    }
}


package lu.caepatriot.oenobox.winecatalog.controller;

import lu.caepatriot.oenobox.winecatalog.dto.CepageDto;
import lu.caepatriot.oenobox.winecatalog.service.WineService;
import lu.caepatriot.oenobox.winecatalog.service.CepageService;
import lu.caepatriot.oenobox.winecatalog.dto.WineDto;
import lu.caepatriot.oenobox.winecatalog.entity.Wine;
import lu.caepatriot.oenobox.winecatalog.entity.Cepage;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cepages")
@Tag(name = "Cepages", description = "API for managing wine grape varieties")
public class CepageController {
    private final CepageService cepageService;

    public CepageController(CepageService cepageService) {
        this.cepageService = cepageService;
    }

    @GetMapping
    @Operation(summary = "Get all cepages", description = "Retrieves a list of all wine grape varieties")
    public List<CepageDto> getAllCepages() {
        return cepageService.getAllCepages();
    }

//    @PostMapping
//    public CepageDto createCepage(@RequestBody CepageDto cepageDto) {
//        return cepageService.createCepage(cepageDto);
//    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cepage by ID", description = "Retrieves a specific wine grape variety by its ID")
    public CepageDto getCepageById(@PathVariable Long id) {
        return cepageService.getCepageById(id);
    }

    @GetMapping("/wine-type/{wineTypeId}")
    @Operation(summary = "Get cepages by wine type", description = "Retrieves all cepages for a specific wine type")
    public List<CepageDto> getCepagesByWineType(@PathVariable Long wineTypeId) {
        return cepageService.getCepagesByWineTypeId(wineTypeId);
    }
}


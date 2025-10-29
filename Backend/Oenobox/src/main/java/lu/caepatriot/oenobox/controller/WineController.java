package lu.caepatriot.oenobox.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lu.caepatriot.oenobox.dto.WineDto;
import lu.caepatriot.oenobox.service.WineService;
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

//    @GetMapping
//    public List<WineDto> getAllWines() {
//        return wineService.getAllWines();
//    }

//    @PostMapping
//    public WineDto createUser(@RequestBody WineDto wineDto) {
//        return wineService.createWine(wineDto);
//    }
//
//    @GetMapping("/{id}")
//    public WineDto getUserById(@PathVariable Long id) {
//        return wineService.getWineById(id);
//    }
}
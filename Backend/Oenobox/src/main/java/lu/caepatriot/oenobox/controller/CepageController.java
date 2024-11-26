package lu.caepatriot.oenobox.controller;

import lu.caepatriot.oenobox.dto.CepageDto;
import lu.caepatriot.oenobox.dto.WineDto;
import lu.caepatriot.oenobox.service.CepageService;
import lu.caepatriot.oenobox.service.WineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cepages")
public class CepageController {
    private final CepageService cepageService;

    public CepageController(CepageService cepageService) {
        this.cepageService = cepageService;
    }

    @GetMapping
    public List<CepageDto> getAllCepages() {
        return cepageService.getAllCepages();
    }

//    @PostMapping
//    public CepageDto createCepage(@RequestBody CepageDto cepageDto) {
//        return cepageService.createCepage(cepageDto);
//    }

    @GetMapping("/{id}")
    public CepageDto getCepageById(@PathVariable Long id) {
        return cepageService.getCepageById(id);
    }
}
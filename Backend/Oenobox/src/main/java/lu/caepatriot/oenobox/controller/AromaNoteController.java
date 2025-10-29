package lu.caepatriot.oenobox.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lu.caepatriot.oenobox.dto.AromaNoteDto;
import lu.caepatriot.oenobox.service.AromaNoteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aroma-notes")
@Tag(name = "Aroma Notes", description = "API for managing wine aroma notes")
public class AromaNoteController {
    private final AromaNoteService aromaNoteService;

    public AromaNoteController(AromaNoteService aromaNoteService) {
        this.aromaNoteService = aromaNoteService;
    }

    @GetMapping
    @Operation(summary = "Get all aroma notes", description = "Retrieves a list of all wine aroma notes")
    public List<AromaNoteDto> getAllAromaNotes() {
        return aromaNoteService.getAllAromaNotes();
    }

    @GetMapping("/category/{categoryOptionId}")
    @Operation(summary = "Get aroma notes by category", description = "Retrieves a list of aroma notes for a specific category")
    public List<AromaNoteDto> getAromaNotesByCategory(@PathVariable Long categoryOptionId) {
        return aromaNoteService.getAromaNotesByCategory(categoryOptionId);
    }
}
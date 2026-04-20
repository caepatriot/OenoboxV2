package lu.caepatriot.oenobox.winecatalog.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lu.caepatriot.oenobox.winecatalog.dto.CatalogSubmissionDto;
import lu.caepatriot.oenobox.winecatalog.dto.CreateCatalogSubmissionRequest;
import lu.caepatriot.oenobox.winecatalog.service.CatalogSubmissionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalog/submissions")
@Tag(name = "Catalog Submissions", description = "API for moderated catalog contributions")
public class CatalogSubmissionController {
    private final CatalogSubmissionService catalogSubmissionService;

    public CatalogSubmissionController(CatalogSubmissionService catalogSubmissionService) {
        this.catalogSubmissionService = catalogSubmissionService;
    }

    @PostMapping
    public CatalogSubmissionDto createSubmission(@RequestBody CreateCatalogSubmissionRequest request) {
        return catalogSubmissionService.createSubmission(request);
    }
}

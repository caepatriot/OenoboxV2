package lu.caepatriot.oenobox.inventory.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lu.caepatriot.oenobox.inventory.dto.*;
import lu.caepatriot.oenobox.inventory.service.InventoryIntakeService;
import lu.caepatriot.oenobox.inventory.service.InventoryLabelScanFacade;
import lu.caepatriot.oenobox.inventory.service.InventoryLookupService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Tag(name = "Inventory Intake", description = "API for intake lookup, lot creation, and dispatch")
public class InventoryIntakeController {
    private final InventoryLookupService inventoryLookupService;
    private final InventoryIntakeService inventoryIntakeService;
    private final InventoryLabelScanFacade inventoryLabelScanFacade;

    public InventoryIntakeController(
            InventoryLookupService inventoryLookupService,
            InventoryIntakeService inventoryIntakeService,
            InventoryLabelScanFacade inventoryLabelScanFacade
    ) {
        this.inventoryLookupService = inventoryLookupService;
        this.inventoryIntakeService = inventoryIntakeService;
        this.inventoryLabelScanFacade = inventoryLabelScanFacade;
    }

    @GetMapping("/api/inventory/intake/search")
    public List<IntakeSearchResultDto> search(@RequestParam("q") String query) {
        return inventoryLookupService.search(query);
    }

    @PostMapping("/api/inventory/intake/ocr")
    public InventoryOcrScanResponse ocrLookup(@RequestBody InventoryOcrRequest request) {
        if (request == null || request.getExtractedText() == null || request.getExtractedText().isBlank()) {
            throw new IllegalArgumentException("extractedText is required");
        }
        return inventoryLabelScanFacade.analyzeExtractedText(request.getExtractedText());
    }

    @PostMapping(value = "/api/inventory/intake/ocr/scan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public InventoryOcrScanResponse scanLabel(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "language", required = false) String language
    ) {
        return inventoryLabelScanFacade.scan(file, language);
    }

    @PostMapping("/api/inventory/lots")
    public AcquisitionLotDto createLot(@RequestBody CreateAcquisitionLotRequest request) {
        return inventoryIntakeService.createLot(request);
    }

    @GetMapping("/api/inventory/lots")
    public List<AcquisitionLotDto> getLots() {
        return inventoryIntakeService.getLots();
    }

    @GetMapping("/api/inventory/lots/{id}")
    public AcquisitionLotDto getLotById(@PathVariable("id") Long lotId) {
        return inventoryIntakeService.getLotById(lotId);
    }

    @PostMapping("/api/inventory/lots/{id}/dispatch")
    public AcquisitionLotDto dispatchLotItem(
            @PathVariable("id") Long lotId,
            @RequestBody DispatchLotItemRequest request
    ) {
        return inventoryIntakeService.dispatchLotItem(lotId, request);
    }
}

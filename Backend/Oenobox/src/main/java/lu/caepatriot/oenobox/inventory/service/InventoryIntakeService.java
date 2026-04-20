package lu.caepatriot.oenobox.inventory.service;

import lu.caepatriot.oenobox.common.exception.ResourceNotFoundException;
import lu.caepatriot.oenobox.inventory.dto.*;
import lu.caepatriot.oenobox.inventory.entity.AcquisitionLot;
import lu.caepatriot.oenobox.inventory.entity.AcquisitionLotItem;
import lu.caepatriot.oenobox.inventory.entity.StockTransaction;
import lu.caepatriot.oenobox.inventory.entity.StoredBottle;
import lu.caepatriot.oenobox.inventory.repository.AcquisitionLotItemRepository;
import lu.caepatriot.oenobox.inventory.repository.AcquisitionLotRepository;
import lu.caepatriot.oenobox.inventory.repository.StockTransactionRepository;
import lu.caepatriot.oenobox.inventory.repository.StoredBottleRepository;
import lu.caepatriot.oenobox.winecatalog.entity.Wine;
import lu.caepatriot.oenobox.winecatalog.entity.WineVintage;
import lu.caepatriot.oenobox.winecatalog.repository.WineVintageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class InventoryIntakeService {
    private static final String STATUS_UNASSIGNED = "UNASSIGNED";
    private static final String STATUS_AVAILABLE = "AVAILABLE";

    private final AcquisitionLotRepository acquisitionLotRepository;
    private final AcquisitionLotItemRepository acquisitionLotItemRepository;
    private final WineVintageRepository wineVintageRepository;
    private final StoredBottleRepository storedBottleRepository;
    private final StockTransactionRepository stockTransactionRepository;

    public InventoryIntakeService(
            AcquisitionLotRepository acquisitionLotRepository,
            AcquisitionLotItemRepository acquisitionLotItemRepository,
            WineVintageRepository wineVintageRepository,
            StoredBottleRepository storedBottleRepository,
            StockTransactionRepository stockTransactionRepository
    ) {
        this.acquisitionLotRepository = acquisitionLotRepository;
        this.acquisitionLotItemRepository = acquisitionLotItemRepository;
        this.wineVintageRepository = wineVintageRepository;
        this.storedBottleRepository = storedBottleRepository;
        this.stockTransactionRepository = stockTransactionRepository;
    }

    @Transactional
    public AcquisitionLotDto createLot(CreateAcquisitionLotRequest request) {
        validateCreateRequest(request);

        AcquisitionLot lot = new AcquisitionLot();
        lot.setHouseholdId(request.getHouseholdId());
        lot.setSource(trimToNull(request.getSource()));
        lot.setAcquiredOn(request.getAcquiredOn() != null ? request.getAcquiredOn() : LocalDate.now());
        lot.setTotalPrice(request.getTotalPrice());
        lot.setCurrency(defaultCurrency(request.getCurrency()));
        lot.setNotes(trimToNull(request.getNotes()));
        lot = acquisitionLotRepository.save(lot);

        boolean createStoredBottlesNow = Boolean.TRUE.equals(request.getCreateStoredBottles());
        List<AcquisitionLotItem> persistedItems = new ArrayList<>();
        for (CreateAcquisitionLotItemRequest itemRequest : request.getItems()) {
            WineVintage vintage = wineVintageRepository.findById(itemRequest.getWineVintageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Wine vintage not found: " + itemRequest.getWineVintageId()));

            AcquisitionLotItem item = new AcquisitionLotItem();
            item.setAcquisitionLot(lot);
            item.setWineVintage(vintage);
            item.setQuantityReceived(itemRequest.getQuantityReceived());
            item.setQuantityAvailable(itemRequest.getQuantityReceived());
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setCurrency(defaultCurrency(itemRequest.getCurrency() != null ? itemRequest.getCurrency() : request.getCurrency()));
            item.setBottleFormat(trimToNull(itemRequest.getBottleFormat()));
            item.setNotes(trimToNull(itemRequest.getNotes()));
            item = acquisitionLotItemRepository.save(item);
            persistedItems.add(item);

            if (createStoredBottlesNow) {
                createStoredBottles(item, lot, itemRequest.getQuantityReceived(), STATUS_UNASSIGNED, "RECEIVED", "Received through intake");
            }
        }

        lot.setItems(persistedItems);
        return mapLotToDto(lot, persistedItems);
    }

    @Transactional(readOnly = true)
    public List<AcquisitionLotDto> getLots() {
        List<AcquisitionLot> lots = acquisitionLotRepository.findAllByOrderByAcquiredOnDescIdDesc();
        List<AcquisitionLotDto> dtos = new ArrayList<>();
        for (AcquisitionLot lot : lots) {
            List<AcquisitionLotItem> items = acquisitionLotItemRepository.findByAcquisitionLotIdOrderByIdAsc(lot.getId());
            dtos.add(mapLotToDto(lot, items));
        }
        return dtos;
    }

    @Transactional(readOnly = true)
    public AcquisitionLotDto getLotById(Long lotId) {
        AcquisitionLot lot = acquisitionLotRepository.findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException("Acquisition lot not found"));
        List<AcquisitionLotItem> items = acquisitionLotItemRepository.findByAcquisitionLotIdOrderByIdAsc(lot.getId());
        return mapLotToDto(lot, items);
    }

    @Transactional
    public AcquisitionLotDto dispatchLotItem(Long lotId, DispatchLotItemRequest request) {
        AcquisitionLot lot = acquisitionLotRepository.findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException("Acquisition lot not found"));
        if (request == null || request.getLotItemId() == null || request.getQuantity() == null) {
            throw new IllegalArgumentException("lotItemId and quantity are required");
        }
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Dispatch quantity must be greater than 0");
        }

        AcquisitionLotItem item = acquisitionLotItemRepository.findById(request.getLotItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Acquisition lot item not found"));
        if (!Objects.equals(item.getAcquisitionLot().getId(), lot.getId())) {
            throw new IllegalArgumentException("Lot item does not belong to this lot");
        }
        if (item.getQuantityAvailable() < request.getQuantity()) {
            throw new IllegalArgumentException("Not enough quantity available to dispatch");
        }

        List<StoredBottle> unassignedBottles = storedBottleRepository
                .findByAcquisitionLotItemIdAndStatusOrderByIdAsc(item.getId(), STATUS_UNASSIGNED);
        int toDispatch = request.getQuantity();
        int reused = Math.min(unassignedBottles.size(), toDispatch);
        for (int i = 0; i < reused; i++) {
            StoredBottle bottle = unassignedBottles.get(i);
            bottle.setStatus(STATUS_AVAILABLE);
            storedBottleRepository.save(bottle);
            createTransaction(bottle, "DISPATCHED_TO_CELLAR", 1, request.getNotes());
        }
        if (toDispatch > reused) {
            createStoredBottles(item, lot, toDispatch - reused, STATUS_AVAILABLE, "DISPATCHED_TO_CELLAR", request.getNotes());
        }

        item.setQuantityAvailable(item.getQuantityAvailable() - request.getQuantity());
        acquisitionLotItemRepository.save(item);

        return getLotById(lotId);
    }

    private void validateCreateRequest(CreateAcquisitionLotRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("At least one lot item is required");
        }
        for (CreateAcquisitionLotItemRequest item : request.getItems()) {
            if (item.getWineVintageId() == null) {
                throw new IllegalArgumentException("wineVintageId is required for each lot item");
            }
            if (item.getQuantityReceived() == null || item.getQuantityReceived() <= 0) {
                throw new IllegalArgumentException("quantityReceived must be greater than 0");
            }
        }
    }

    private void createStoredBottles(
            AcquisitionLotItem item,
            AcquisitionLot lot,
            int quantity,
            String status,
            String transactionType,
            String transactionNotes
    ) {
        for (int i = 0; i < quantity; i++) {
            StoredBottle bottle = new StoredBottle();
            bottle.setWineVintage(item.getWineVintage());
            bottle.setAcquisitionLot(lot);
            bottle.setAcquisitionLotItem(item);
            bottle.setStatus(status);
            bottle.setPurchasePrice(item.getUnitPrice());
            bottle.setOwnedSince(lot.getAcquiredOn());
            bottle.setNotes(item.getNotes());
            bottle = storedBottleRepository.save(bottle);
            createTransaction(bottle, transactionType, 1, transactionNotes);
        }
    }

    private void createTransaction(StoredBottle bottle, String type, Integer quantity, String notes) {
        StockTransaction tx = new StockTransaction();
        tx.setStoredBottle(bottle);
        tx.setTransactionType(type);
        tx.setQuantity(quantity);
        tx.setTransactionAt(LocalDateTime.now());
        tx.setNotes(trimToNull(notes));
        stockTransactionRepository.save(tx);
    }

    private AcquisitionLotDto mapLotToDto(AcquisitionLot lot, List<AcquisitionLotItem> items) {
        AcquisitionLotDto dto = new AcquisitionLotDto();
        dto.setId(lot.getId());
        dto.setHouseholdId(lot.getHouseholdId());
        dto.setSource(lot.getSource());
        dto.setAcquiredOn(lot.getAcquiredOn());
        dto.setTotalPrice(lot.getTotalPrice());
        dto.setCurrency(lot.getCurrency());
        dto.setNotes(lot.getNotes());

        int totalReceived = 0;
        int totalAvailable = 0;
        List<AcquisitionLotItemDto> itemDtos = new ArrayList<>();
        for (AcquisitionLotItem item : items) {
            totalReceived += safeInt(item.getQuantityReceived());
            totalAvailable += safeInt(item.getQuantityAvailable());
            itemDtos.add(mapItemToDto(item));
        }
        dto.setTotalReceived(totalReceived);
        dto.setTotalAvailable(totalAvailable);
        dto.setTotalDispatched(totalReceived - totalAvailable);
        dto.setItems(itemDtos);
        return dto;
    }

    private AcquisitionLotItemDto mapItemToDto(AcquisitionLotItem item) {
        AcquisitionLotItemDto dto = new AcquisitionLotItemDto();
        WineVintage vintage = item.getWineVintage();
        Wine wine = vintage.getWine();

        dto.setId(item.getId());
        dto.setWineId(wine.getId());
        dto.setWineVintageId(vintage.getId());
        dto.setWineLabel(wine.getName());
        dto.setProducer(wine.getProducer());
        dto.setVintageYear(vintage.getVintageYear());
        dto.setBottleFormat(item.getBottleFormat() != null ? item.getBottleFormat() : vintage.getBottleFormat());
        dto.setQuantityReceived(item.getQuantityReceived());
        dto.setQuantityAvailable(item.getQuantityAvailable());
        dto.setQuantityDispatched(safeInt(item.getQuantityReceived()) - safeInt(item.getQuantityAvailable()));
        dto.setUnitPrice(item.getUnitPrice());
        dto.setCurrency(item.getCurrency());
        dto.setNotes(item.getNotes());
        return dto;
    }

    private int safeInt(Integer value) {
        return value != null ? value : 0;
    }

    private String defaultCurrency(String currency) {
        String normalized = trimToNull(currency);
        return normalized != null ? normalized : "EUR";
    }

    private String trimToNull(String input) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

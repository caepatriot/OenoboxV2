package lu.caepatriot.oenobox.winecatalog.service;

import lu.caepatriot.oenobox.common.exception.ResourceNotFoundException;
import lu.caepatriot.oenobox.winecatalog.dto.WineDto;
import lu.caepatriot.oenobox.winecatalog.entity.Cepage;
import lu.caepatriot.oenobox.winecatalog.entity.Wine;
import lu.caepatriot.oenobox.winecatalog.entity.WineType;
import lu.caepatriot.oenobox.winecatalog.repository.CepageRepository;
import lu.caepatriot.oenobox.winecatalog.repository.WineRepository;
import lu.caepatriot.oenobox.winecatalog.repository.WineTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WineService {
    private final lu.caepatriot.oenobox.winecatalog.repository.WineRepository wineRepository;
    private final lu.caepatriot.oenobox.winecatalog.repository.WineTypeRepository wineTypeRepository;
    private final CepageRepository cepageRepository;

    public WineService(
            WineRepository wineRepository,
            WineTypeRepository wineTypeRepository,
            CepageRepository cepageRepository
    ) {
        this.wineRepository = wineRepository;
        this.wineTypeRepository = wineTypeRepository;
        this.cepageRepository = cepageRepository;
    }

    @Transactional(readOnly = true)
    public List<WineDto> getAllWines() {
        return wineRepository.findAll().stream()
                .sorted(Comparator.comparing(Wine::getName, String.CASE_INSENSITIVE_ORDER))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WineDto> search(String query) {
        if (query == null || query.isBlank()) {
            return getAllWines();
        }
        return wineRepository.findByNameContainingIgnoreCaseOrRegionContainingIgnoreCaseOrderByNameAsc(query.trim(), query.trim())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WineDto getWineById(Long id) {
        return mapToDto(wineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wine not found")));
    }

    @Transactional
    public WineDto createWine(WineDto wineDto) {
        Wine wine = new Wine();
        updateWineFromDto(wine, wineDto);
        return mapToDto(wineRepository.save(wine));
    }

    @Transactional
    public WineDto updateWine(Long id, WineDto wineDto) {
        Wine wine = wineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wine not found"));
        updateWineFromDto(wine, wineDto);
        return mapToDto(wineRepository.save(wine));
    }

    @Transactional
    public void deleteWine(Long id) {
        if (!wineRepository.existsById(id)) {
            throw new ResourceNotFoundException("Wine not found");
        }
        wineRepository.deleteById(id);
    }

    private void updateWineFromDto(Wine wine, WineDto dto) {
        wine.setName(dto.getName());
        wine.setRegion(dto.getRegion());
        wine.setYear(dto.getYear());
        wine.setProducer(dto.getProducer());
        wine.setCountry(dto.getCountry());
        wine.setAppellation(dto.getAppellation());
        wine.setDescription(dto.getDescription());
        wine.setImageUrl(dto.getImageUrl());

        if (dto.getWineTypeName() != null && !dto.getWineTypeName().isBlank()) {
            WineType wineType = wineTypeRepository.findByNameIgnoreCase(dto.getWineTypeName().trim())
                    .orElseGet(() -> {
                        WineType created = new WineType();
                        created.setName(dto.getWineTypeName().trim());
                        return wineTypeRepository.save(created);
                    });
            wine.setWineType(wineType);
        } else {
            wine.setWineType(null);
        }

        if (dto.getCepages() != null && !dto.getCepages().isEmpty()) {
            List<Cepage> resolvedCepages = new ArrayList<>();
            for (String cepageName : dto.getCepages()) {
                if (cepageName == null || cepageName.isBlank()) {
                    continue;
                }
                Optional<Cepage> existing = cepageRepository.findByNameContainingIgnoreCase(cepageName.trim()).stream()
                        .filter(cepage -> cepage.getName().equalsIgnoreCase(cepageName.trim()))
                        .findFirst();
                existing.ifPresent(resolvedCepages::add);
            }
            wine.setCepages(resolvedCepages);
        } else {
            wine.setCepages(List.of());
        }
    }

    private WineDto mapToDto(Wine wine) {
        WineDto dto = new WineDto();
        dto.setId(wine.getId());
        dto.setName(wine.getName());
        dto.setRegion(wine.getRegion());
        dto.setYear(wine.getYear());
        dto.setProducer(wine.getProducer());
        dto.setCountry(wine.getCountry());
        dto.setAppellation(wine.getAppellation());
        dto.setDescription(wine.getDescription());
        dto.setImageUrl(wine.getImageUrl());
        if (wine.getWineType() != null) {
            dto.setWineTypeName(wine.getWineType().getName());
        }
        if (wine.getCepages() != null) {
            dto.setCepages(wine.getCepages().stream()
                    .filter(Objects::nonNull)
                    .map(Cepage::getName)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}

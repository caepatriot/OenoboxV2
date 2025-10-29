package lu.caepatriot.oenobox.service;

import lu.caepatriot.oenobox.dto.WineTypeDto;
import lu.caepatriot.oenobox.entity.WineType;
import lu.caepatriot.oenobox.exception.ResourceNotFoundException;
import lu.caepatriot.oenobox.repository.WineTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WineTypeService {
    private final WineTypeRepository wineTypeRepository;

    public WineTypeService(WineTypeRepository wineTypeRepository) {
        this.wineTypeRepository = wineTypeRepository;
    }

    public List<WineTypeDto> getAllWineTypes() {
        return wineTypeRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public WineTypeDto getWineTypeById(Long id) {
        WineType wineType = wineTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WineType not found"));
        return convertToDto(wineType);
    }

    public WineTypeDto findByName(String name) {
        WineType wineType = wineTypeRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("WineType not found with name: " + name));
        return convertToDto(wineType);
    }

    private WineTypeDto convertToDto(WineType wineType) {
        return new WineTypeDto(
                wineType.getId(),
                wineType.getName(),
                wineType.getColorCode(),
                wineType.getDescription(),
                null, // createdAt not in entity
                null, // updatedAt not in entity
                null  // cepages not in entity
        );
    }
}
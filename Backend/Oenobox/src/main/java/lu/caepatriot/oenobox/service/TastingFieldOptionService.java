package lu.caepatriot.oenobox.service;

import lu.caepatriot.oenobox.dto.AromaNoteDto;
import lu.caepatriot.oenobox.dto.TastingFieldOptionDto;
import lu.caepatriot.oenobox.entity.TastingFieldOption;
import lu.caepatriot.oenobox.exception.ResourceNotFoundException;
import lu.caepatriot.oenobox.repository.TastingFieldOptionRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TastingFieldOptionService {
    private final TastingFieldOptionRepository tastingFieldOptionRepository;

    public TastingFieldOptionService(TastingFieldOptionRepository tastingFieldOptionRepository) {
        this.tastingFieldOptionRepository = tastingFieldOptionRepository;
    }

    public List<TastingFieldOptionDto> getOptionsByFieldId(Long fieldId) {
        List<TastingFieldOption> options = tastingFieldOptionRepository.findByFieldIdWithAromaNotes(fieldId);
        return options.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TastingFieldOptionDto> getOptionsByFieldIdAndWineType(Long fieldId, String wineType) {
        List<TastingFieldOption> options = tastingFieldOptionRepository.findByFieldIdAndWineTypeWithAromaNotes(fieldId, wineType);
        return options.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TastingFieldOptionDto getOptionById(Long id) {
        TastingFieldOption option = tastingFieldOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tasting field option not found"));
        return convertToDto(option);
    }

    private TastingFieldOptionDto convertToDto(TastingFieldOption option) {
        List<AromaNoteDto> notes = option.getAromaNotes() != null ?
                option.getAromaNotes().stream()
                        .map(note -> new AromaNoteDto(
                                note.getId(),
                                note.getName(),
                                note.getDescription()
                        ))
                        .collect(Collectors.toList()) : null;

        return new TastingFieldOptionDto(
                option.getId(),
                null, // field not set
                option.getValue(),
                option.getLabel(),
                option.getColorCode(),
                option.getIsNegative(),
                option.getSortOrder(),
                option.getIcon(),
                option.getWineTypeRestriction(),
                null, // createdAt not in entity
                null, // updatedAt not in entity
                notes,
                null, // searchTerm not in entity
                null, // filterWineTypes not in entity
                null  // filterNegative not in entity
        );
    }
}
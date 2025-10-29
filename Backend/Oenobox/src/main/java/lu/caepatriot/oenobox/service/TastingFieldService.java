package lu.caepatriot.oenobox.service;

import lu.caepatriot.oenobox.dto.TastingFieldDto;
import lu.caepatriot.oenobox.dto.TastingFieldOptionDto;
import lu.caepatriot.oenobox.entity.TastingField;
import lu.caepatriot.oenobox.exception.ResourceNotFoundException;
import lu.caepatriot.oenobox.repository.TastingFieldRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TastingFieldService {
    private final TastingFieldRepository tastingFieldRepository;

    public TastingFieldService(TastingFieldRepository tastingFieldRepository) {
        this.tastingFieldRepository = tastingFieldRepository;
    }

    public List<TastingFieldDto> getFieldsByStep(Long stepId) {
        List<TastingField> fields = tastingFieldRepository.findByStepIdWithOptions(stepId);
        return fields.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TastingFieldDto> getFieldsByStepAndWineType(Long stepId, String wineType) {
        List<TastingField> fields = tastingFieldRepository.findByStepIdWithOptions(stepId);
        return fields.stream()
                .filter(field -> field.getWineTypeRestriction() == null ||
                        field.getWineTypeRestriction().isEmpty() ||
                        field.getWineTypeRestriction().contains(wineType))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TastingFieldDto getFieldById(Long id) {
        TastingField field = tastingFieldRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tasting field not found"));
        return convertToDto(field);
    }

    private TastingFieldDto convertToDto(TastingField field) {
        List<TastingFieldOptionDto> options = field.getOptions() != null ?
                field.getOptions().stream()
                        .map(option -> new TastingFieldOptionDto(
                                option.getId(),
                                null, // field not set
                                option.getValue(),
                                option.getLabel(),
                                option.getColorCode(),
                                option.getIsNegative(),
                                option.getSortOrder(),
                                option.getIcon(),
                                null, // wineTypeRestriction not in entity
                                null, // createdAt not in entity
                                null, // updatedAt not in entity
                                null, // aromaNotes not in entity
                                null, // searchTerm not in entity
                                null, // filterWineTypes not in entity
                                null  // filterNegative not in entity
                        ))
                        .collect(Collectors.toList()) : null;

        return new TastingFieldDto(
                field.getId(),
                null, // step not set
                field.getFieldType(),
                field.getName(),
                field.getLabel(),
                field.getPlaceholder(),
                field.getRequired(),
                field.getMultiSelect(),
                field.getWineTypeRestriction(),
                null, // createdAt not in entity
                null, // updatedAt not in entity
                options,
                null, // searchTerm not in entity
                null, // filterWineTypes not in entity
                null, // filterRequired not in entity
                null  // filterMultiSelect not in entity
        );
    }
}
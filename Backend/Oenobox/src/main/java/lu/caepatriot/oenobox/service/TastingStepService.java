package lu.caepatriot.oenobox.service;

import lu.caepatriot.oenobox.dto.*;
import lu.caepatriot.oenobox.entity.*;
import lu.caepatriot.oenobox.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TastingStepService {

    private static final Logger logger = LoggerFactory.getLogger(TastingStepService.class);

    @Autowired
    private TastingStepRepository tastingStepRepository;

    @Autowired
    private TastingFieldRepository tastingFieldRepository;

    @Autowired
    private TastingFieldOptionRepository tastingFieldOptionRepository;

    @Autowired
    private AromaNoteRepository aromaNoteRepository;

    public List<TastingStepDto> getAllTastingSteps() {
        List<TastingStep> steps = tastingStepRepository.findAll();
        return steps.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<TastingStepDto> getTastingStepsByWineType(String wineType) {
        logger.info("Getting tasting steps for wineType: {}", wineType);
        List<TastingField> fields = tastingFieldRepository.findByWineTypeRestrictionContaining(wineType);
        logger.debug("Found {} fields for wineType: {}", fields.size(), wineType);

        // Group fields by step and create unique steps
        Map<Long, TastingStep> stepMap = new HashMap<>();
        for (TastingField field : fields) {
            TastingStep step = field.getStep();
            if (step == null) {
                logger.warn("Field {} has null step, skipping", field.getId());
                continue;
            }
            if (step.getId() == null) {
                logger.warn("Step for field {} has null ID, skipping", field.getId());
                continue;
            }
            stepMap.put(step.getId(), step);
        }

        List<TastingStep> steps = new ArrayList<>(stepMap.values());
        steps.sort(Comparator.comparing(TastingStep::getStepNumber,
            Comparator.nullsLast(Comparator.naturalOrder())));

        return steps.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private TastingStepDto convertToDto(TastingStep step) {
        TastingStepDto dto = new TastingStepDto();
        dto.setId(step.getId());
        dto.setStepNumber(step.getStepNumber());
        dto.setName(step.getName());
        dto.setTitle(step.getTitle());
        dto.setDescription(step.getDescription());

        List<TastingField> fields = tastingFieldRepository.findByStepIdWithOptions(step.getId());
        dto.setFields(fields.stream().map(this::convertFieldToDto).collect(Collectors.toList()));

        return dto;
    }

    private TastingFieldDto convertFieldToDto(TastingField field) {
        logger.debug("Converting field {} to DTO, type: {}, options count: {}",
            field.getId(), field.getFieldType(), field.getOptions() != null ? field.getOptions().size() : 0);

        TastingFieldDto dto = new TastingFieldDto();
        dto.setId(field.getId());
        dto.setFieldType(field.getFieldType());
        dto.setName(field.getName());
        dto.setLabel(field.getLabel());
        dto.setPlaceholder(field.getPlaceholder());
        dto.setRequired(field.getRequired());
        dto.setMultiSelect(field.getMultiSelect());
        dto.setWineTypeRestriction(field.getWineTypeRestriction());

        // For now, we'll handle groups and values based on field type
        // This is a simplified version - you may need to adjust based on your data structure
        if (field.getOptions() != null && !field.getOptions().isEmpty()) {
            List<TastingFieldOptionDto> options = field.getOptions().stream()
                .map(this::convertOptionToDto)
                .collect(Collectors.toList());

            if ("select-button".equals(field.getFieldType())) {
                // Create a group for select-button fields
                TastingFieldGroupDto group = new TastingFieldGroupDto();
                group.setId(1L); // Hardcoded ID - potential issue if multiple groups needed
                logger.warn("Using hardcoded group ID 1L for field {}, this may cause conflicts", field.getId());
                group.setType("select-button");
                group.setWineType(field.getWineTypeRestriction());
                group.setGroupValues(options);
                group.setMulti(field.getMultiSelect());
                group.setRequired(field.getRequired());
                // Note: TastingFieldDto doesn't have setGroups method, so we can't set groups
                logger.warn("TastingFieldDto does not have setGroups method, groups cannot be set for field {}", field.getId());
            } else {
                dto.setOptions(options);
            }
        }

        return dto;
    }

    private TastingFieldOptionDto convertOptionToDto(TastingFieldOption option) {
        logger.debug("Converting option {} to DTO", option.getId());

        TastingFieldOptionDto dto = new TastingFieldOptionDto();
        dto.setId(option.getId());
        dto.setValue(option.getValue());
        dto.setLabel(option.getLabel());
        dto.setColorCode(option.getColorCode());
        dto.setIcon(option.getIcon());
        // Note: TastingFieldOptionDto doesn't have setIconColor method, using colorCode as iconColor
        dto.setIsNegative(option.getIsNegative());

        // Convert aroma notes if present
        if (option.getAromaNotes() != null && !option.getAromaNotes().isEmpty()) {
            logger.debug("Converting {} aroma notes for option {}", option.getAromaNotes().size(), option.getId());
            List<AromaNoteDto> notes = option.getAromaNotes().stream()
                .map(note -> new AromaNoteDto(note.getId(), note.getName(), note.getDescription()))
                .collect(Collectors.toList());
            dto.setAromaNotes(notes); // Correct method name is setAromaNotes, not setNotes
        }

        return dto;
    }
}
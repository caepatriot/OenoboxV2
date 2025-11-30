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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        List<TastingField> fields = tastingFieldRepository.findAll();
        logger.debug("Found {} total fields", fields.size());
        fields = fields.stream()
                .filter(field -> field.getWineTypeRestriction() == null ||
                        field.getWineTypeRestriction().isEmpty() ||
                        field.getWineTypeRestriction().contains(wineType))
                .collect(Collectors.toList());
        logger.debug("Found {} fields for wineType: {}", fields.size(), wineType);

        // Group fields by step
        Map<Long, List<TastingField>> fieldsByStep = fields.stream()
                .collect(Collectors.groupingBy(field -> field.getStep().getId()));

        // Convert to DTOs
        List<TastingStepDto> steps = fieldsByStep.entrySet().stream()
                .map(entry -> {
                    TastingStep step = tastingStepRepository.findById(entry.getKey()).orElse(null);
                    if (step != null) {
                        TastingStepDto dto = convertToDto(step);
                        // Filter fields for this step based on wine type
                        List<TastingFieldDto> filteredFields = dto.getFields().stream()
                                .filter(field -> field.getWineTypeRestriction() == null ||
                                        field.getWineTypeRestriction().isEmpty() ||
                                        field.getWineTypeRestriction().contains(wineType))
                                .collect(Collectors.toList());
                        dto.setFields(filteredFields);
                        return dto;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(TastingStepDto::getStepNumber))
                .collect(Collectors.toList());

        return steps;
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
                field.getId(), field.getFieldType(),
                field.getOptions() != null ? field.getOptions().size() : 0);

        TastingFieldDto dto = new TastingFieldDto();
        dto.setId(field.getId());
        dto.setFieldType(field.getFieldType());
        dto.setName(field.getName());
        dto.setLabel(field.getLabel());
        dto.setPlaceholder(field.getPlaceholder());
        dto.setRequired(field.getRequired());
        dto.setMultiSelect(field.getMultiSelect());
        dto.setWineTypeRestriction(field.getWineTypeRestriction());

        if (field.getOptions() != null && !field.getOptions().isEmpty()) {
            List<TastingFieldOptionDto> options = field.getOptions().stream()
                    .map(this::convertOptionToDto)
                    .collect(Collectors.toList());

            if ("select-button".equals(field.getFieldType())) {

                // 🔥 CAS SPÉCIAL : Arômes-Type -> plusieurs groupes
                if ("type_aromes".equals(field.getName())) {

                    Map<String, Integer> groupIndexByValue = new HashMap<>();
                    // Groupe 1 - obligatoire
                    for (String v : List.of("vif", "frais", "neutre", "suave", "lourd")) {
                        groupIndexByValue.put(v.toLowerCase(), 1);
                    }
                    // Groupe 2 - obligatoire
                    for (String v : List.of("désagréable", "desagreable", "commun", "agréable", "agreable")) {
                        groupIndexByValue.put(v.toLowerCase(), 2);
                    }
                    // Groupe 3 - optionnel
                    for (String v : List.of("épanoui", "epanoui", "évolué", "evolué", "evolue", "passé", "passe")) {
                        groupIndexByValue.put(v.toLowerCase(), 3);
                    }
                    // Groupe 4 - optionnel
                    for (String v : List.of("rôti", "roti")) {
                        groupIndexByValue.put(v.toLowerCase(), 4);
                    }
                    // Groupe 5 - optionnel
                    for (String v : List.of("franc", "strict", "violent")) {
                        groupIndexByValue.put(v.toLowerCase(), 5);
                    }

                    Map<Integer, TastingFieldGroupDto> groupsByIndex = new LinkedHashMap<>();

                    for (TastingFieldOptionDto opt : options) {
                        String value = opt.getValue() != null ? opt.getValue().toLowerCase() : "";
                        int groupIndex = groupIndexByValue.getOrDefault(value, 0); // 0 = "autres", si tu veux

                        if (groupIndex == 0) {
                            // soit on les ignore, soit on les met dans un groupe 0 "autres"
                            continue;
                        }

                        TastingFieldGroupDto group = groupsByIndex.computeIfAbsent(groupIndex, idx -> {
                            TastingFieldGroupDto g = new TastingFieldGroupDto();
                            g.setId((long) idx);
                            g.setType("select-button");
                            g.setWineType(field.getWineTypeRestriction());
                            g.setMulti(Boolean.TRUE); // Arômes-Type autorise multi
                            // Groupes 1 & 2 obligatoires, le reste optionnel
                            g.setRequired(idx == 1 || idx == 2);
                            g.setGroupValues(new ArrayList<>());
                            return g;
                        });

                        group.getGroupValues().add(opt);
                    }

                    dto.setGroups(new ArrayList<>(groupsByIndex.values()));

                } else {
                    // ✅ Comportement normal pour les autres select-button :
                    // un seul groupe avec toutes les options
                    TastingFieldGroupDto group = new TastingFieldGroupDto();
                    group.setId(1L);
                    logger.warn("Using hardcoded group ID 1L for field {}, this may cause conflicts", field.getId());
                    group.setType("select-button");
                    group.setWineType(field.getWineTypeRestriction());
                    group.setGroupValues(options);
                    group.setMulti(field.getMultiSelect());
                    group.setRequired(field.getRequired());
                    dto.setGroups(List.of(group));
                }

            } else {
                // Autres types : autocomplete etc.
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

    public List<Map<String, Object>> getTastingStepsFrontendStructure() {
        logger.info("Getting tasting steps in frontend structure");
        List<TastingStepDto> steps = getAllTastingSteps();

        return steps.stream()
                .sorted(Comparator.comparing(TastingStepDto::getStepNumber))
                .map(this::convertToFrontendStructure)
                .collect(Collectors.toList());
    }

    private Map<String, Object> convertToFrontendStructure(TastingStepDto step) {
        Map<String, Object> stepMap = new LinkedHashMap<>();
        stepMap.put("step", step.getStepNumber());
        stepMap.put("name", step.getName());
        stepMap.put("title", step.getTitle());

        List<Map<String, Object>> fields = step.getFields().stream()
                .map(this::convertFieldToFrontendStructure)
                .collect(Collectors.toList());

        stepMap.put("fields", fields);
        return stepMap;
    }

    private Map<String, Object> convertFieldToFrontendStructure(TastingFieldDto field) {
        Map<String, Object> fieldMap = new LinkedHashMap<>();
        fieldMap.put("id", field.getId());
        fieldMap.put("type", field.getFieldType());
        fieldMap.put("wineType", field.getWineTypeRestriction());
        fieldMap.put("label", field.getLabel());
        fieldMap.put("name", field.getName());
        fieldMap.put("placeholder", field.getPlaceholder());
        fieldMap.put("required", field.getRequired());
        fieldMap.put("multi", field.getMultiSelect());

        // Handle groups for select-button fields
        if ("select-button".equals(field.getFieldType()) && field.getGroups() != null && !field.getGroups().isEmpty()) {
            List<Map<String, Object>> groups = field.getGroups().stream()
                    .map(this::convertGroupToFrontendStructure)
                    .collect(Collectors.toList());
            fieldMap.put("groups", groups);
        }

        // Handle values for autocomplete fields
        if ("autocomplete".equals(field.getFieldType()) && field.getOptions() != null && !field.getOptions().isEmpty()) {
            List<Map<String, Object>> values = field.getOptions().stream()
                    .map(this::convertOptionToFrontendValue)
                    .collect(Collectors.toList());
            fieldMap.put("values", values);
        }

        return fieldMap;
    }

    private Map<String, Object> convertGroupToFrontendStructure(TastingFieldGroupDto group) {
        Map<String, Object> groupMap = new LinkedHashMap<>();
        groupMap.put("id", group.getId());
        groupMap.put("type", group.getType());
        groupMap.put("wineType", group.getWineType());
        groupMap.put("multi", group.getMulti());
        groupMap.put("required", group.getRequired());

        List<Map<String, Object>> groupValues = group.getGroupValues().stream()
                .map(this::convertOptionToFrontendGroupValue)
                .collect(Collectors.toList());
        groupMap.put("groupValues", groupValues);

        return groupMap;
    }

    private Map<String, Object> convertOptionToFrontendValue(TastingFieldOptionDto option) {
        Map<String, Object> valueMap = new LinkedHashMap<>();
        valueMap.put("id", option.getId());
        valueMap.put("value", option.getValue());
        valueMap.put("wineType", option.getWineTypeRestriction());
        valueMap.put("icon", option.getIcon());
        valueMap.put("iconColor", option.getColorCode()); // Using colorCode as iconColor
        return valueMap;
    }

    private Map<String, Object> convertOptionToFrontendGroupValue(TastingFieldOptionDto option) {
        Map<String, Object> valueMap = new LinkedHashMap<>();
        valueMap.put("id", option.getId());
        valueMap.put("value", option.getValue());
        valueMap.put("wineType", option.getWineTypeRestriction());
        valueMap.put("negatif", option.getIsNegative());
        valueMap.put("icon", option.getIcon());
        valueMap.put("color", option.getColorCode());
        valueMap.put("iconColor", option.getColorCode()); // Using colorCode as iconColor

        // Add notes from aromaNotes
        if (option.getAromaNotes() != null && !option.getAromaNotes().isEmpty()) {
            List<Map<String, Object>> notes = option.getAromaNotes().stream()
                    .map(note -> {
                        Map<String, Object> noteMap = new LinkedHashMap<>();
                        noteMap.put("id", note.getId());
                        noteMap.put("libelle", note.getLibelle());
                        noteMap.put("details", note.getDetails());
                        return noteMap;
                    })
                    .collect(Collectors.toList());
            valueMap.put("notes", notes);
        }

        return valueMap;
    }
}
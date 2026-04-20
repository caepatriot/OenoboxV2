package lu.caepatriot.oenobox.inventory.service;

import lu.caepatriot.oenobox.inventory.dto.InventoryOcrCandidateDto;
import lu.caepatriot.oenobox.inventory.dto.InventoryOcrScanResponse;
import lu.caepatriot.oenobox.inventory.dto.IntakeSearchResultDto;
import lu.caepatriot.oenobox.inventory.ocr.InventoryOcrService;
import lu.caepatriot.oenobox.inventory.ocr.OcrExtraction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class InventoryLabelScanFacade {
    private static final Pattern VINTAGE_PATTERN = Pattern.compile("\\b(19\\d{2}|20\\d{2})\\b");
    private static final Pattern ALCOHOL_PATTERN = Pattern.compile("(\\d{1,2}(?:[.,]\\d)?)\\s?%\\s?(?:vol)?", Pattern.CASE_INSENSITIVE);
    private static final Pattern VOLUME_PATTERN = Pattern.compile("\\b(75\\s?cl|750\\s?ml|150\\s?cl|1[.,]5\\s?l|37[.,]?5\\s?cl|375\\s?ml)\\b", Pattern.CASE_INSENSITIVE);
    private static final List<String> APPELLATION_KEYWORDS = List.of(
            "saint-émilion", "saint-emilion", "margaux", "pauillac", "pommerol", "pomerol", "chablis",
            "sancerre", "châteauneuf-du-pape", "chateauneuf-du-pape", "meursault", "gevrey-chambertin",
            "vosne-romanée", "vosne-romanee", "côte-rôtie", "cote-rotie", "bandol", "crozes-hermitage",
            "bordeaux", "bourgogne", "champagne", "alsace", "rioja", "barolo", "chianti"
    );
    private static final Set<String> STOPWORDS = Set.of(
            "vin", "wine", "mise", "bouteille", "mis", "au", "aux", "et", "de", "des", "du", "en", "par",
            "produit", "france", "appellation", "controlee", "contrôlée", "contain", "contains", "sulfites",
            "vol", "cl", "ml", "avec", "pour", "sur", "the", "estate", "misenbouteille", "proprietaire",
            "propriétaire", "product", "of", "embouteille", "bottle", "cave", "cellar", "grand", "reserve",
            "réserve", "misenbouteilleauchateau", "containssulfites"
    );

    private final InventoryOcrService inventoryOcrService;
    private final InventoryLookupService inventoryLookupService;

    public InventoryLabelScanFacade(
            InventoryOcrService inventoryOcrService,
            InventoryLookupService inventoryLookupService
    ) {
        this.inventoryOcrService = inventoryOcrService;
        this.inventoryLookupService = inventoryLookupService;
    }

    public InventoryOcrScanResponse scan(MultipartFile file, String language) {
        OcrExtraction extraction = inventoryOcrService.extract(file, language);
        return buildResponse(extraction);
    }

    public InventoryOcrScanResponse analyzeExtractedText(String extractedText) {
        OcrExtraction extraction = new OcrExtraction();
        extraction.setProvider("manual");
        extraction.setRawText(extractedText == null ? "" : extractedText.trim());
        extraction.setLines(splitLines(extraction.getRawText()));
        return buildResponse(extraction);
    }

    private InventoryOcrScanResponse buildResponse(OcrExtraction extraction) {
        String rawText = extraction.getRawText() == null ? "" : extraction.getRawText().trim();
        InventoryOcrScanResponse response = new InventoryOcrScanResponse();
        response.setProvider(extraction.getProvider());
        response.setRawText(rawText);
        response.setLines(extraction.getLines() == null ? List.of() : extraction.getLines());
        response.setDetectedVintage(extractVintage(rawText));
        response.setDetectedAlcoholPercent(extractAlcohol(rawText));
        response.setDetectedVolumeMl(extractVolumeMl(rawText));
        response.setDetectedAppellation(extractAppellation(rawText));
        response.setSuggestedQuery(buildSuggestedQuery(extraction));
        response.setExtractedFields(buildExtractedFields(response));

        List<IntakeSearchResultDto> candidates = rawText.isBlank()
                ? List.of()
                : inventoryLookupService.searchByExtractedText(rawText, response.getDetectedVintage());
        response.setCandidates(candidates);
        response.setNeedsUserConfirmation(Boolean.TRUE);
        return response;
    }

    private List<InventoryOcrCandidateDto> buildExtractedFields(InventoryOcrScanResponse response) {
        List<InventoryOcrCandidateDto> fields = new ArrayList<>();
        addField(fields, "suggestedQuery", response.getSuggestedQuery(), 0.65d);
        addField(fields, "appellation", response.getDetectedAppellation(), 0.7d);
        addField(fields, "vintage", response.getDetectedVintage(), 0.92d);
        addField(fields, "alcoholPercent", response.getDetectedAlcoholPercent(), 0.88d);
        addField(fields, "volumeMl", response.getDetectedVolumeMl(), 0.9d);
        return fields;
    }

    private void addField(List<InventoryOcrCandidateDto> fields, String name, Object value, double confidence) {
        if (value == null) {
            return;
        }
        String normalized = String.valueOf(value).trim();
        if (normalized.isBlank()) {
            return;
        }
        InventoryOcrCandidateDto dto = new InventoryOcrCandidateDto();
        dto.setField(name);
        dto.setValue(normalized);
        dto.setConfidence(confidence);
        fields.add(dto);
    }

    private String buildSuggestedQuery(OcrExtraction extraction) {
        List<String> lines = extraction.getLines() == null ? List.of() : extraction.getLines();
        if (!lines.isEmpty()) {
            String first = cleanForQuery(lines.get(0));
            if (!first.isBlank() && first.length() >= 3) {
                return first;
            }
        }

        List<String> tokens = tokenize(extraction.getRawText());
        return tokens.stream().limit(5).collect(Collectors.joining(" "));
    }

    private String cleanForQuery(String value) {
        return Arrays.stream(value.split("\\s+"))
                .map(token -> token.replaceAll("[^\\p{L}\\p{N}'’-]", ""))
                .filter(token -> token.length() >= 2)
                .limit(6)
                .collect(Collectors.joining(" "))
                .trim();
    }

    private Integer extractVintage(String rawText) {
        Matcher matcher = VINTAGE_PATTERN.matcher(rawText == null ? "" : rawText);
        Integer best = null;
        while (matcher.find()) {
            int candidate = Integer.parseInt(matcher.group(1));
            if (candidate >= 1900 && candidate <= 2099) {
                best = candidate;
            }
        }
        return best;
    }

    private Double extractAlcohol(String rawText) {
        Matcher matcher = ALCOHOL_PATTERN.matcher(rawText == null ? "" : rawText);
        if (!matcher.find()) {
            return null;
        }
        return Double.parseDouble(matcher.group(1).replace(',', '.'));
    }

    private Integer extractVolumeMl(String rawText) {
        Matcher matcher = VOLUME_PATTERN.matcher(rawText == null ? "" : rawText);
        if (!matcher.find()) {
            return null;
        }
        String normalized = matcher.group(1).toLowerCase(Locale.ROOT).replace(" ", "").replace(',', '.');
        if (normalized.endsWith("cl")) {
            double cl = Double.parseDouble(normalized.replace("cl", ""));
            return (int) Math.round(cl * 10);
        }
        if (normalized.endsWith("ml")) {
            return (int) Math.round(Double.parseDouble(normalized.replace("ml", "")));
        }
        if (normalized.endsWith("l")) {
            double liters = Double.parseDouble(normalized.replace("l", ""));
            return (int) Math.round(liters * 1000);
        }
        return null;
    }

    private String extractAppellation(String rawText) {
        String normalized = normalize(rawText);
        for (String keyword : APPELLATION_KEYWORDS) {
            if (normalized.contains(normalize(keyword))) {
                return keyword;
            }
        }
        return null;
    }

    private List<String> splitLines(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return List.of();
        }
        return Arrays.stream(rawText.split("\\r?\\n"))
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .toList();
    }

    private List<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return Arrays.stream(normalize(text).split("[^a-z0-9]+"))
                .map(String::trim)
                .filter(token -> token.length() >= 3)
                .filter(token -> !STOPWORDS.contains(token))
                .distinct()
                .limit(12)
                .toList();
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT);
        return normalized.replace('œ', 'o');
    }
}

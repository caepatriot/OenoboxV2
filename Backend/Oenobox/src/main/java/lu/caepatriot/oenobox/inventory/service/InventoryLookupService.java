package lu.caepatriot.oenobox.inventory.service;

import lu.caepatriot.oenobox.inventory.dto.IntakeSearchResultDto;
import lu.caepatriot.oenobox.winecatalog.entity.Wine;
import lu.caepatriot.oenobox.winecatalog.entity.WineVintage;
import lu.caepatriot.oenobox.winecatalog.repository.WineRepository;
import lu.caepatriot.oenobox.winecatalog.repository.WineVintageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventoryLookupService {
    private static final Set<String> STOPWORDS = Set.of(
            "vin", "wine", "mise", "bouteille", "mis", "appellation", "controlee", "controle",
            "france", "de", "des", "du", "la", "le", "les", "and", "avec", "contains", "sulfites",
            "estate", "grand", "reserve", "reserva", "vol", "cl", "ml", "product", "produit"
    );

    private final WineRepository wineRepository;
    private final WineVintageRepository wineVintageRepository;

    public InventoryLookupService(WineRepository wineRepository, WineVintageRepository wineVintageRepository) {
        this.wineRepository = wineRepository;
        this.wineVintageRepository = wineVintageRepository;
    }

    @Transactional(readOnly = true)
    public List<IntakeSearchResultDto> search(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        String q = query.trim();
        List<Wine> wines = wineRepository
                .findTop25ByNameContainingIgnoreCaseOrProducerContainingIgnoreCaseOrAppellationContainingIgnoreCaseOrderByNameAsc(
                        q, q, q
                );
        return mapRankedResults(wines, q, null, false);
    }

    @Transactional(readOnly = true)
    public List<IntakeSearchResultDto> searchByExtractedText(String extractedText, Integer detectedVintage) {
        if (extractedText == null || extractedText.isBlank()) {
            return List.of();
        }

        List<String> tokens = tokenize(extractedText);
        List<Wine> wines = wineRepository.findAll();
        if (wines.isEmpty()) {
            return List.of();
        }

        return wines.stream()
                .map(wine -> scoreWineAgainstTokens(wine, tokens, extractedText))
                .filter(scored -> scored.score() > 0)
                .sorted(Comparator.comparing(ScoredWine::score).reversed())
                .limit(20)
                .map(ScoredWine::wine)
                .collect(Collectors.collectingAndThen(Collectors.toList(), ranked -> mapRankedResults(ranked, extractedText, detectedVintage, true)));
    }

    private List<IntakeSearchResultDto> mapRankedResults(List<Wine> wines, String query, Integer detectedVintage, boolean scoreByTokens) {
        if (wines == null || wines.isEmpty()) {
            return List.of();
        }

        Map<Long, List<WineVintage>> vintagesByWineId = wineVintageRepository.findByWineIdIn(
                        wines.stream().map(Wine::getId).collect(Collectors.toList())
                ).stream()
                .collect(Collectors.groupingBy(v -> v.getWine().getId()));

        List<IntakeSearchResultDto> results = new ArrayList<>();
        for (Wine wine : wines) {
            List<WineVintage> vintages = vintagesByWineId.getOrDefault(wine.getId(), List.of());
            if (vintages.isEmpty()) {
                results.add(mapWineOnly(wine, query, scoreByTokens ? scoreTokens(wine, query) : score(wine.getName(), query)));
                continue;
            }
            vintages.stream()
                    .sorted(Comparator.comparing(
                            (WineVintage vintage) -> vintageMatches(detectedVintage, vintage) ? 1 : 0,
                            Comparator.reverseOrder())
                            .thenComparing(WineVintage::getVintageYear, Comparator.nullsLast(Comparator.reverseOrder())))
                    .limit(4)
                    .forEach(vintage -> results.add(mapVintage(
                            wine,
                            vintage,
                            query,
                            adjustVintageScore(scoreByTokens ? scoreTokens(wine, query) : score(wine.getName(), query), detectedVintage, vintage)
                    )));
        }

        return results.stream()
                .sorted(Comparator.comparing(IntakeSearchResultDto::getConfidence, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(30)
                .collect(Collectors.toList());
    }

    private IntakeSearchResultDto mapWineOnly(Wine wine, String query, double score) {
        IntakeSearchResultDto dto = new IntakeSearchResultDto();
        dto.setWineId(wine.getId());
        dto.setWineVintageId(null);
        dto.setLabel(wine.getName());
        dto.setProducer(wine.getProducer());
        dto.setAppellation(wine.getAppellation());
        dto.setRegion(wine.getRegion());
        dto.setCountry(wine.getCountry());
        dto.setWineType(wine.getWineType() != null ? wine.getWineType().getName() : null);
        dto.setImage(wine.getImageUrl());
        dto.setSource("LOCAL_CATALOG");
        dto.setSourceReference("wine:" + wine.getId());
        dto.setExistsLocally(Boolean.TRUE);
        dto.setConfidence(score);
        return dto;
    }

    private IntakeSearchResultDto mapVintage(Wine wine, WineVintage vintage, String query, double score) {
        IntakeSearchResultDto dto = mapWineOnly(wine, query, score);
        dto.setWineVintageId(vintage.getId());
        dto.setVintage(vintage.getVintageYear());
        dto.setLabel(buildLabel(wine.getName(), vintage.getVintageYear()));
        dto.setSourceReference("wineVintage:" + vintage.getId());
        return dto;
    }

    private String buildLabel(String wineName, Integer vintageYear) {
        if (vintageYear == null) {
            return wineName;
        }
        return wineName + " " + vintageYear;
    }

    private double score(String candidate, String query) {
        if (candidate == null || query == null) {
            return 0.3d;
        }
        String normalizedCandidate = normalize(candidate);
        String normalizedQuery = normalize(query);
        if (normalizedCandidate.equals(normalizedQuery)) {
            return 1.0d;
        }
        if (normalizedCandidate.startsWith(normalizedQuery)) {
            return 0.9d;
        }
        if (normalizedCandidate.contains(normalizedQuery)) {
            return 0.75d;
        }
        return 0.5d;
    }

    private double scoreTokens(Wine wine, String extractedText) {
        List<String> tokens = tokenize(extractedText);
        if (tokens.isEmpty()) {
            return score(wine.getName(), extractedText);
        }

        String haystack = normalize(String.join(" ", List.of(
                nullSafe(wine.getName()),
                nullSafe(wine.getProducer()),
                nullSafe(wine.getAppellation()),
                nullSafe(wine.getRegion()),
                nullSafe(wine.getCountry())
        )));

        double points = 0;
        for (String token : tokens) {
            if (haystack.contains(token)) {
                points += token.length() >= 6 ? 0.2d : 0.12d;
            }
        }

        if (!tokens.isEmpty()) {
            points = Math.min(1.0d, points + 0.2d);
        }
        return Math.max(0.1d, points);
    }

    private double adjustVintageScore(double baseScore, Integer detectedVintage, WineVintage vintage) {
        if (detectedVintage != null && vintageMatches(detectedVintage, vintage)) {
            return Math.min(1.0d, baseScore + 0.18d);
        }
        return baseScore;
    }

    private boolean vintageMatches(Integer detectedVintage, WineVintage vintage) {
        return detectedVintage != null && vintage != null && Objects.equals(detectedVintage, vintage.getVintageYear());
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
                .limit(10)
                .toList();
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replace('œ', 'o');
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    private ScoredWine scoreWineAgainstTokens(Wine wine, List<String> tokens, String extractedText) {
        if (wine == null) {
            return new ScoredWine(null, 0d);
        }
        String haystack = normalize(String.join(" ", List.of(
                nullSafe(wine.getName()),
                nullSafe(wine.getProducer()),
                nullSafe(wine.getAppellation()),
                nullSafe(wine.getRegion()),
                nullSafe(wine.getCountry())
        )));
        double points = 0;
        for (String token : tokens) {
            if (haystack.contains(token)) {
                points += token.length() >= 6 ? 0.25d : 0.15d;
            }
        }

        String fullText = normalize(extractedText);
        if (!nullSafe(wine.getName()).isBlank() && fullText.contains(normalize(wine.getName()))) {
            points += 0.35d;
        }
        if (!nullSafe(wine.getAppellation()).isBlank() && fullText.contains(normalize(wine.getAppellation()))) {
            points += 0.25d;
        }
        if (!nullSafe(wine.getProducer()).isBlank() && fullText.contains(normalize(wine.getProducer()))) {
            points += 0.25d;
        }

        return new ScoredWine(wine, Math.min(1.0d, points));
    }

    private record ScoredWine(Wine wine, double score) {
    }
}

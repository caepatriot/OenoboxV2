package lu.caepatriot.oenobox.winecatalog.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WineService {
    private static final long SAMPLE_API_CACHE_TTL_MS = 6 * 60 * 60 * 1000L;

    private final WineRepository wineRepository;
    private final WineTypeRepository wineTypeRepository;
    private final CepageRepository cepageRepository;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private volatile List<ExternalWineCandidate> sampleApiCatalogCache = Collections.emptyList();
    private volatile long sampleApiCatalogCacheTimestamp = 0L;

    public WineService(WineRepository wineRepository, WineTypeRepository wineTypeRepository, CepageRepository cepageRepository, ObjectMapper objectMapper) {
        this.wineRepository = wineRepository;
        this.wineTypeRepository = wineTypeRepository;
        this.cepageRepository = cepageRepository;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(4)).build();
    }

    @Transactional(readOnly = true)
    public List<WineDto> getWines(String query, String region, String country, String producer, String wineType) {
        List<Wine> wines = wineRepository.searchCatalog(
                normalizeNullable(query),
                normalizeNullable(region),
                normalizeNullable(country),
                normalizeNullable(producer),
                normalizeNullable(wineType)
        );

        return wines.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WineDto getWineById(Long id) {
        Wine wine = wineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wine not found with id: " + id));
        return mapToDto(wine);
    }

    @Transactional
    public WineDto createWine(WineDto wineDto) {
        validateCreateOrUpdate(wineDto);

        List<Wine> duplicates = wineRepository.findPotentialDuplicates(
                wineDto.getName().trim(),
                wineDto.getYear(),
                normalizeNullable(wineDto.getRegion())
        );

        if (!duplicates.isEmpty()) {
            return mapToDto(duplicates.get(0));
        }

        Wine wine = new Wine();
        updateEntityFromDto(wine, wineDto);
        wine.setDataSource(isBlank(wineDto.getDataSource()) ? "community" : wineDto.getDataSource().trim());

        return mapToDto(wineRepository.save(wine));
    }

    @Transactional
    public WineDto updateWine(Long id, WineDto wineDto) {
        validateCreateOrUpdate(wineDto);

        Wine wine = wineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wine not found with id: " + id));

        updateEntityFromDto(wine, wineDto);
        return mapToDto(wineRepository.save(wine));
    }

    @Transactional
    public void deleteWine(Long id) {
        if (!wineRepository.existsById(id)) {
            throw new ResourceNotFoundException("Wine not found with id: " + id);
        }
        wineRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<WineDto> getWineSuggestions(String query) {
        if (isBlank(query)) {
            return Collections.emptyList();
        }

        String normalizedQuery = query.trim();
        List<WineDto> localMatches = wineRepository.searchCatalog(normalizedQuery, null, null, null, null).stream()
                .limit(8)
                .map(this::mapToDto)
                .peek(dto -> dto.setDataSource("community"))
                .collect(Collectors.toList());

        Map<String, WineDto> merged = new LinkedHashMap<>();
        for (WineDto dto : localMatches) {
            merged.put(normalizeKey(dto.getName(), dto.getYear(), dto.getRegion()), dto);
        }

        for (WineDto candidate : fetchSampleApiSuggestions(normalizedQuery)) {
            String key = normalizeKey(candidate.getName(), candidate.getYear(), candidate.getRegion());
            merged.putIfAbsent(key, candidate);
            if (merged.size() >= 20) {
                break;
            }
        }

        return new ArrayList<>(merged.values());
    }

    private void validateCreateOrUpdate(WineDto wineDto) {
        if (wineDto == null || isBlank(wineDto.getName())) {
            throw new IllegalArgumentException("Wine name is required.");
        }
    }

    private void updateEntityFromDto(Wine wine, WineDto dto) {
        wine.setName(dto.getName().trim());
        wine.setRegion(normalizeNullable(dto.getRegion()));
        wine.setYear(dto.getYear());
        wine.setProducer(normalizeNullable(dto.getProducer()));
        wine.setCountry(normalizeNullable(dto.getCountry()));
        wine.setAppellation(normalizeNullable(dto.getAppellation()));
        wine.setDescription(normalizeNullable(dto.getDescription()));
        wine.setAromaNotes(normalizeStringList(dto.getAromaNotes()));
        wine.setFoodPairings(normalizeStringList(dto.getFoodPairings()));
        wine.setImageUrl(normalizeNullable(dto.getImageUrl()));
        wine.setDataSource(normalizeNullable(dto.getDataSource()));
        wine.setExternalId(normalizeNullable(dto.getExternalId()));

        WineType wineType = resolveWineType(dto.getWineTypeName());
        wine.setWineType(wineType);

        List<Cepage> cepages = resolveCepages(dto.getCepages(), wineType);
        wine.setCepages(cepages);
    }

    private WineDto mapToDto(Wine wine) {
        WineDto dto = new WineDto();
        dto.setId(wine.getId());
        dto.setName(wine.getName());
        dto.setWineTypeName(wine.getWineType() != null ? wine.getWineType().getName() : null);
        dto.setCepages(wine.getCepages() == null
                ? Collections.emptyList()
                : wine.getCepages().stream()
                    .map(Cepage::getName)
                    .filter(Objects::nonNull)
                    .sorted(String::compareToIgnoreCase)
                    .collect(Collectors.toList()));
        dto.setRegion(wine.getRegion());
        dto.setYear(wine.getYear());
        dto.setProducer(wine.getProducer());
        dto.setCountry(wine.getCountry());
        dto.setAppellation(wine.getAppellation());
        dto.setDescription(wine.getDescription());
        dto.setAromaNotes(wine.getAromaNotes() == null ? Collections.emptyList() : wine.getAromaNotes());
        dto.setFoodPairings(wine.getFoodPairings() == null ? Collections.emptyList() : wine.getFoodPairings());
        dto.setImageUrl(wine.getImageUrl());
        dto.setDataSource(wine.getDataSource());
        dto.setExternalId(wine.getExternalId());
        return dto;
    }

    private WineType resolveWineType(String wineTypeName) {
        if (isBlank(wineTypeName)) {
            return null;
        }

        String normalizedName = wineTypeName.trim();
        return wineTypeRepository.findByNameIgnoreCase(normalizedName)
                .orElseGet(() -> {
                    WineType generated = new WineType();
                    generated.setName(normalizedName);
                    generated.setColorCode("#8E6D55");
                    generated.setDescription("User-generated wine type");
                    return wineTypeRepository.save(generated);
                });
    }

    private List<Cepage> resolveCepages(List<String> cepageNames, WineType wineType) {
        if (cepageNames == null || cepageNames.isEmpty()) {
            return Collections.emptyList();
        }

        WineType effectiveWineType = wineType != null ? wineType : resolveOrCreateUnknownWineType();

        return cepageNames.stream()
                .map(this::normalizeNullable)
                .filter(Objects::nonNull)
                .distinct()
                .map(name -> cepageRepository.findFirstByNameIgnoreCase(name)
                        .orElseGet(() -> {
                            Cepage cepage = new Cepage();
                            cepage.setName(name);
                            cepage.setWineType(effectiveWineType);
                            cepage.setDescription("User-generated cepage");
                            return cepageRepository.save(cepage);
                        }))
                .collect(Collectors.toList());
    }

    private WineType resolveOrCreateUnknownWineType() {
        Optional<WineType> existing = wineTypeRepository.findByNameIgnoreCase("Unknown");
        if (existing.isPresent()) {
            return existing.get();
        }

        WineType fallback = new WineType();
        fallback.setName("Unknown");
        fallback.setColorCode("#8E6D55");
        fallback.setDescription("Fallback type for user-generated references");
        return wineTypeRepository.save(fallback);
    }

    private List<WineDto> fetchSampleApiSuggestions(String query) {
        String normalized = query.toLowerCase(Locale.ROOT);
        return getSampleApiCatalog().stream()
                .filter(candidate -> candidate.matches(normalized))
                .limit(12)
                .map(this::mapExternalCandidateToDto)
                .collect(Collectors.toList());
    }

    private List<ExternalWineCandidate> getSampleApiCatalog() {
        long now = System.currentTimeMillis();
        if (!sampleApiCatalogCache.isEmpty() && now - sampleApiCatalogCacheTimestamp < SAMPLE_API_CACHE_TTL_MS) {
            return sampleApiCatalogCache;
        }

        synchronized (this) {
            long refreshedNow = System.currentTimeMillis();
            if (!sampleApiCatalogCache.isEmpty() && refreshedNow - sampleApiCatalogCacheTimestamp < SAMPLE_API_CACHE_TTL_MS) {
                return sampleApiCatalogCache;
            }

            List<ExternalWineCandidate> loaded = new ArrayList<>();
            loaded.addAll(fetchSampleApiCategory("https://api.sampleapis.com/wines/reds", "Rouge"));
            loaded.addAll(fetchSampleApiCategory("https://api.sampleapis.com/wines/whites", "Blanc"));
            loaded.addAll(fetchSampleApiCategory("https://api.sampleapis.com/wines/rose", "Rose"));

            sampleApiCatalogCache = loaded;
            sampleApiCatalogCacheTimestamp = refreshedNow;
        }

        return sampleApiCatalogCache;
    }

    private List<ExternalWineCandidate> fetchSampleApiCategory(String url, String wineTypeName) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(6))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return Collections.emptyList();
            }

            JsonNode root = objectMapper.readTree(response.body());
            if (!root.isArray()) {
                return Collections.emptyList();
            }

            List<ExternalWineCandidate> suggestions = new ArrayList<>();
            for (JsonNode product : root) {
                String name = normalizeNullable(product.path("wine").asText(null));
                if (name == null) {
                    continue;
                }

                String winery = normalizeNullable(product.path("winery").asText(null));
                String location = normalizeNullable(product.path("location").asText(null));
                String imageUrl = normalizeNullable(product.path("image").asText(null));
                String id = normalizeNullable(product.path("id").asText(null));

                String ratingAverage = normalizeNullable(product.path("rating").path("average").asText(null));
                String ratingReviews = normalizeNullable(product.path("rating").path("reviews").asText(null));
                String description = normalizeRatingDescription(ratingAverage, ratingReviews);

                suggestions.add(new ExternalWineCandidate(
                        name,
                        wineTypeName,
                        winery,
                        location,
                        imageUrl,
                        id == null ? null : wineTypeName.toLowerCase(Locale.ROOT) + "-" + id,
                        description
                ));
            }

            return suggestions;
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    private WineDto mapExternalCandidateToDto(ExternalWineCandidate candidate) {
        WineDto dto = new WineDto();
        dto.setName(candidate.name());
        dto.setWineTypeName(candidate.wineTypeName());
        dto.setProducer(candidate.producer());
        dto.setRegion(candidate.region());
        dto.setImageUrl(candidate.imageUrl());
        dto.setExternalId(candidate.externalId());
        dto.setDescription(candidate.description());
        dto.setDataSource("sampleapis");
        dto.setAromaNotes(defaultAromasByType(candidate.wineTypeName()));
        dto.setFoodPairings(defaultPairingsByType(candidate.wineTypeName()));
        return dto;
    }

    private String normalizeRatingDescription(String average, String reviews) {
        if (isBlank(average) && isBlank(reviews)) {
            return null;
        }

        if (isBlank(reviews)) {
            return "Community rating: " + average;
        }

        if (isBlank(average)) {
            return "Community reviews: " + reviews;
        }

        return "Community rating: " + average + " (" + reviews + " reviews)";
    }

    private List<String> defaultAromasByType(String wineType) {
        if (isBlank(wineType)) {
            return Collections.emptyList();
        }

        String lower = wineType.toLowerCase(Locale.ROOT);
        if (lower.contains("rouge") || lower.contains("red")) {
            return Arrays.asList("black fruits", "spices", "oak");
        }
        if (lower.contains("blanc") || lower.contains("white")) {
            return Arrays.asList("citrus", "white flowers", "stone fruits");
        }
        if (lower.contains("rose")) {
            return Arrays.asList("strawberry", "citrus zest", "floral");
        }
        if (lower.contains("sparkling")) {
            return Arrays.asList("apple", "brioche", "citrus");
        }

        return Collections.emptyList();
    }

    private List<String> defaultPairingsByType(String wineType) {
        if (isBlank(wineType)) {
            return Collections.emptyList();
        }

        String lower = wineType.toLowerCase(Locale.ROOT);
        if (lower.contains("rouge") || lower.contains("red")) {
            return Arrays.asList("grilled red meat", "lamb", "aged cheese");
        }
        if (lower.contains("blanc") || lower.contains("white")) {
            return Arrays.asList("seafood", "roasted poultry", "goat cheese");
        }
        if (lower.contains("rose")) {
            return Arrays.asList("charcuterie", "summer salads", "grilled fish");
        }
        if (lower.contains("sparkling")) {
            return Arrays.asList("aperitif", "fried snacks", "fruit desserts");
        }

        return Collections.emptyList();
    }

    private List<String> normalizeStringList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }

        return values.stream()
                .map(this::normalizeNullable)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeKey(String name, Integer year, String region) {
        return safeString(name).toLowerCase(Locale.ROOT) + "|"
                + (year == null ? "" : year) + "|"
                + safeString(region).toLowerCase(Locale.ROOT);
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private record ExternalWineCandidate(
            String name,
            String wineTypeName,
            String producer,
            String region,
            String imageUrl,
            String externalId,
            String description
    ) {
        private boolean matches(String query) {
            return safe(name).contains(query)
                    || safe(producer).contains(query)
                    || safe(region).contains(query)
                    || safe(wineTypeName).contains(query);
        }

        private String safe(String value) {
            return value == null ? "" : value.toLowerCase(Locale.ROOT);
        }
    }
}


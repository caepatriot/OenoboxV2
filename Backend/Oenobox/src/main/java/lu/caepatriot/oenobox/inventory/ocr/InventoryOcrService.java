package lu.caepatriot.oenobox.inventory.ocr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryOcrService {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${app.ocr.ocrspace.url:https://api.ocr.space/parse/image}")
    private String ocrSpaceUrl;

    @Value("${app.ocr.ocrspace.api-key:}")
    private String ocrSpaceApiKey;

    public InventoryOcrService(ObjectMapper objectMapper) {
        this.restClient = RestClient.builder().build();
        this.objectMapper = objectMapper;
    }

    public OcrExtraction extract(MultipartFile file, String language) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is required");
        }
        if (ocrSpaceApiKey == null || ocrSpaceApiKey.isBlank()) {
            throw new IllegalStateException("OCR is not configured. Set app.ocr.ocrspace.api-key to enable label scanning.");
        }

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new NamedByteArrayResource(file.getBytes(), file.getOriginalFilename()));
            body.add("language", resolveLanguage(language));
            body.add("isOverlayRequired", "false");
            body.add("detectOrientation", "true");
            body.add("scale", "true");
            body.add("OCREngine", "2");

            String payload = restClient.post()
                    .uri(ocrSpaceUrl)
                    .header("apikey", ocrSpaceApiKey)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            return parseOcrSpaceResponse(payload);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read uploaded image", exception);
        }
    }

    private OcrExtraction parseOcrSpaceResponse(String payload) {
        if (payload == null || payload.isBlank()) {
            throw new IllegalStateException("OCR provider returned an empty response");
        }
        try {
            JsonNode root = objectMapper.readTree(payload);
            boolean isErrored = root.path("IsErroredOnProcessing").asBoolean(false);
            if (isErrored) {
                String errorMessage = readErrorMessage(root);
                throw new IllegalStateException(errorMessage.isBlank() ? "OCR provider failed to process the image" : errorMessage);
            }

            StringBuilder rawTextBuilder = new StringBuilder();
            List<String> lines = new ArrayList<>();
            for (JsonNode parsedResult : root.path("ParsedResults")) {
                String parsedText = parsedResult.path("ParsedText").asText("");
                if (!parsedText.isBlank()) {
                    if (rawTextBuilder.length() > 0) {
                        rawTextBuilder.append('\n');
                    }
                    rawTextBuilder.append(parsedText.trim());
                    for (String line : parsedText.split("\\r?\\n")) {
                        String trimmed = line.trim();
                        if (!trimmed.isBlank()) {
                            lines.add(trimmed);
                        }
                    }
                }
            }

            OcrExtraction extraction = new OcrExtraction();
            extraction.setProvider("ocrspace");
            extraction.setRawText(rawTextBuilder.toString().trim());
            extraction.setLines(lines);
            return extraction;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to parse OCR response", exception);
        }
    }

    private String readErrorMessage(JsonNode root) {
        List<String> messages = new ArrayList<>();
        JsonNode errorMessage = root.path("ErrorMessage");
        if (errorMessage.isArray()) {
            errorMessage.forEach(node -> {
                String value = node.asText("").trim();
                if (!value.isBlank()) {
                    messages.add(value);
                }
            });
        } else if (errorMessage.isTextual()) {
            String value = errorMessage.asText("").trim();
            if (!value.isBlank()) {
                messages.add(value);
            }
        }

        JsonNode errorDetails = root.path("ErrorDetails");
        if (errorDetails.isTextual()) {
            String value = errorDetails.asText("").trim();
            if (!value.isBlank()) {
                messages.add(value);
            }
        }
        return String.join(" | ", messages);
    }

    private String resolveLanguage(String language) {
        String normalized = language == null ? "fre" : language.trim().toLowerCase();
        return switch (normalized) {
            case "fr", "fra", "fre" -> "fre";
            case "en", "eng" -> "eng";
            case "pt", "por" -> "por";
            case "de", "ger", "deu" -> "ger";
            default -> "fre";
        };
    }

    private static final class NamedByteArrayResource extends ByteArrayResource {
        private final String filename;

        private NamedByteArrayResource(byte[] byteArray, String filename) {
            super(byteArray);
            this.filename = filename == null || filename.isBlank() ? "label-image" : filename;
        }

        @Override
        public String getFilename() {
            return filename;
        }

        @Override
        public long contentLength() {
            return getByteArray().length;
        }

    }
}

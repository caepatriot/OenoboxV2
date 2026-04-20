package lu.caepatriot.oenobox.inventory.ocr;

import java.util.ArrayList;
import java.util.List;

public class OcrExtraction {
    private String provider;
    private String rawText;
    private List<String> lines = new ArrayList<>();

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }
}

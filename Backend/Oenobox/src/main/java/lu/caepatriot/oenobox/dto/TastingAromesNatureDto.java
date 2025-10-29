package lu.caepatriot.oenobox.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TastingAromesNatureDto {
    private String category;
    private Long tastingId;
    private List<String> notes;

    public TastingAromesNatureDto() {}

    public TastingAromesNatureDto(String category, Long tastingId, List<String> notes) {
        this.category = category;
        this.tastingId = tastingId;
        this.notes = notes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getTastingId() {
        return tastingId;
    }

    public void setTastingId(Long tastingId) {
        this.tastingId = tastingId;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }
}
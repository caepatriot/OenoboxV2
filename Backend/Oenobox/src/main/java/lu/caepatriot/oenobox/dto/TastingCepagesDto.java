package lu.caepatriot.oenobox.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TastingCepagesDto {
    private Long id;
    private Long tastingId;
    private String cepage;

    public TastingCepagesDto() {}

    public TastingCepagesDto(Long id, Long tastingId, String cepage) {
        this.id = id;
        this.tastingId = tastingId;
        this.cepage = cepage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTastingId() {
        return tastingId;
    }

    public void setTastingId(Long tastingId) {
        this.tastingId = tastingId;
    }

    public String getCepage() {
        return cepage;
    }

    public void setCepage(String cepage) {
        this.cepage = cepage;
    }
}
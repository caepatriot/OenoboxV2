package lu.caepatriot.oenobox.dto;

public class AromaNoteDto {
    private Long id;
    private String libelle;
    private String details;

    // Constructors
    public AromaNoteDto() {}

    public AromaNoteDto(Long id, String libelle, String details) {
        this.id = id;
        this.libelle = libelle;
        this.details = details;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
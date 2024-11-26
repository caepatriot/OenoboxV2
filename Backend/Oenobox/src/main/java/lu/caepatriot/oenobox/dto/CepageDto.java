package lu.caepatriot.oenobox.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CepageDto {

    private Long id;
    private String nom_cepage;
    private String couleur;
    private String origine;
    private String caracteristiques;
    private String adaptabilite_climatique;

    public CepageDto() {
    }

    public CepageDto(Long id, String nom_cepage, String couleur) {
        this.id = id;
        this.nom_cepage = nom_cepage;
        this.couleur = couleur;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom_cepage() {
        return nom_cepage;
    }

    public void setNom_cepage(String nom_cepage) {
        this.nom_cepage = nom_cepage;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }
}
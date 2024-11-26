package lu.caepatriot.oenobox.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cepages")
public class Cepage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    //    @Column(name = "nom_cepage")
    private String nom_cepage;
    private String couleur;
    private String origine;
    private String caracteristiques;
    private String adaptabilite_climatique;


    // Getters, setters, and constructors


    public Cepage() {
    }

    public Cepage(Long id, String nom_cepage, String couleur, String origine, String caracteristiques, String adaptabilite_climatique) {
        this.id = id;
        this.nom_cepage = nom_cepage;
        this.couleur = couleur;
        this.origine = origine;
        this.caracteristiques = caracteristiques;
        this.adaptabilite_climatique = adaptabilite_climatique;
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

    public String getOrigine() {
        return origine;
    }

    public void setOrigine(String origine) {
        this.origine = origine;
    }

    public String getCaracteristiques() {
        return caracteristiques;
    }

    public void setCaracteristiques(String caracteristiques) {
        this.caracteristiques = caracteristiques;
    }

    public String getAdaptabilite_climatique() {
        return adaptabilite_climatique;
    }

    public void setAdaptabilite_climatique(String adaptabilite_climatique) {
        this.adaptabilite_climatique = adaptabilite_climatique;
    }

    @Override
    public String toString() {
        return "Cepage{" +
                "id=" + id +
                ", nom_cepage='" + nom_cepage + '\'' +
                ", couleur='" + couleur + '\'' +
                ", origine='" + origine + '\'' +
                ", caracteristiques='" + caracteristiques + '\'' +
                ", adaptabilite_climatique='" + adaptabilite_climatique + '\'' +
                '}';
    }
}
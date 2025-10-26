package lu.caepatriot.oenobox.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "tastings")
public class Tasting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Informations sur le vin
    @Column(name = "wine_type")
    private String wineType; // "red", "white", "rose"

    @ElementCollection
    @CollectionTable(name = "tasting_cepages", joinColumns = @JoinColumn(name = "tasting_id"))
    @Column(name = "cepage")
    private List<String> cepages;

    @Column(name = "region")
    private String region;

    @Column(name = "aop_igp_vdf")
    private String aopIgpVdf;

    @Column(name = "elevage")
    private String elevage;

    @Column(name = "wine_import")
    private String wineImport;

    @Column(name = "prix_lancement")
    private Double prixLancement;

    @Column(name = "prix_actuel")
    private Double prixActuel;

    // Aspect Visuel
    @Column(name = "robe_rouge")
    private String robeRouge;

    @Column(name = "robe_blanche")
    private String robeBlanche;

    @Column(name = "robe_rose")
    private String robeRose;

    @Column(name = "disque")
    private String disque;

    @Column(name = "intensite_visuelle")
    private String intensiteVisuelle;

    @Column(name = "limpidite")
    private String limpidite;

    @Column(name = "brillance")
    private String brillance;

    @Column(name = "evolution_visuelle")
    private String evolutionVisuelle;

    @Column(name = "remarques_visuelles")
    private String remarquesVisuelles;

    // Nez
    @Column(name = "intensite_nez")
    private String intensiteNez;

    @Column(name = "qualite_nez")
    private String qualiteNez;

    @Column(name = "type_aromes")
    private String typeAromes;

    @ElementCollection
    @CollectionTable(name = "tasting_aromes_nature", joinColumns = @JoinColumn(name = "tasting_id"))
    @MapKeyColumn(name = "category")
    @Column(name = "notes")
    private Map<String, List<String>> aromesNature; // Map of category -> list of notes

    @Column(name = "description_nez")
    private String descriptionNez;

    // Bouche
    @Column(name = "attaque")
    private String attaque;

    @Column(name = "evolution_bouche")
    private String evolutionBouche;

    @Column(name = "structure")
    private String structure;

    @Column(name = "texture")
    private String texture;

    // Longueur en bouche
    @Column(name = "persistance_aromatique")
    private String persistanceAromatique;

    @Column(name = "caudalies_longueur")
    private String caudaliesLongueur;

    @Column(name = "structure_longueur")
    private String structureLongueur;

    // Conclusion
    @Column(name = "note_finale")
    private String noteFinale;

    @Column(name = "caudalies_conclusion")
    private Integer caudaliesConclusion;

    @Column(name = "remarques_conclusion")
    private String remarquesConclusion;

    @Column(name = "accords_mets_vins")
    private String accordsMetsVins;

    // Autres informations
    @Column(name = "temperature_ideale")
    private Double temperatureIdeale;

    @Column(name = "date_ideale_consommation")
    private String dateIdealeConsommation;

    @Column(name = "evolution_probable")
    private String evolutionProbable;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Tasting() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getWineType() {
        return wineType;
    }

    public void setWineType(String wineType) {
        this.wineType = wineType;
    }

    public List<String> getCepages() {
        return cepages;
    }

    public void setCepages(List<String> cepages) {
        this.cepages = cepages;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAopIgpVdf() {
        return aopIgpVdf;
    }

    public void setAopIgpVdf(String aopIgpVdf) {
        this.aopIgpVdf = aopIgpVdf;
    }

    public String getElevage() {
        return elevage;
    }

    public void setElevage(String elevage) {
        this.elevage = elevage;
    }

    public String getWineImport() {
        return wineImport;
    }

    public void setWineImport(String wineImport) {
        this.wineImport = wineImport;
    }

    public Double getPrixLancement() {
        return prixLancement;
    }

    public void setPrixLancement(Double prixLancement) {
        this.prixLancement = prixLancement;
    }

    public Double getPrixActuel() {
        return prixActuel;
    }

    public void setPrixActuel(Double prixActuel) {
        this.prixActuel = prixActuel;
    }

    public String getRobeRouge() {
        return robeRouge;
    }

    public void setRobeRouge(String robeRouge) {
        this.robeRouge = robeRouge;
    }

    public String getRobeBlanche() {
        return robeBlanche;
    }

    public void setRobeBlanche(String robeBlanche) {
        this.robeBlanche = robeBlanche;
    }

    public String getRobeRose() {
        return robeRose;
    }

    public void setRobeRose(String robeRose) {
        this.robeRose = robeRose;
    }

    public String getDisque() {
        return disque;
    }

    public void setDisque(String disque) {
        this.disque = disque;
    }

    public String getIntensiteVisuelle() {
        return intensiteVisuelle;
    }

    public void setIntensiteVisuelle(String intensiteVisuelle) {
        this.intensiteVisuelle = intensiteVisuelle;
    }

    public String getLimpidite() {
        return limpidite;
    }

    public void setLimpidite(String limpidite) {
        this.limpidite = limpidite;
    }

    public String getBrillance() {
        return brillance;
    }

    public void setBrillance(String brillance) {
        this.brillance = brillance;
    }

    public String getEvolutionVisuelle() {
        return evolutionVisuelle;
    }

    public void setEvolutionVisuelle(String evolutionVisuelle) {
        this.evolutionVisuelle = evolutionVisuelle;
    }

    public String getRemarquesVisuelles() {
        return remarquesVisuelles;
    }

    public void setRemarquesVisuelles(String remarquesVisuelles) {
        this.remarquesVisuelles = remarquesVisuelles;
    }

    public String getIntensiteNez() {
        return intensiteNez;
    }

    public void setIntensiteNez(String intensiteNez) {
        this.intensiteNez = intensiteNez;
    }

    public String getQualiteNez() {
        return qualiteNez;
    }

    public void setQualiteNez(String qualiteNez) {
        this.qualiteNez = qualiteNez;
    }

    public String getTypeAromes() {
        return typeAromes;
    }

    public void setTypeAromes(String typeAromes) {
        this.typeAromes = typeAromes;
    }

    public Map<String, List<String>> getAromesNature() {
        return aromesNature;
    }

    public void setAromesNature(Map<String, List<String>> aromesNature) {
        this.aromesNature = aromesNature;
    }

    public String getDescriptionNez() {
        return descriptionNez;
    }

    public void setDescriptionNez(String descriptionNez) {
        this.descriptionNez = descriptionNez;
    }

    public String getAttaque() {
        return attaque;
    }

    public void setAttaque(String attaque) {
        this.attaque = attaque;
    }

    public String getEvolutionBouche() {
        return evolutionBouche;
    }

    public void setEvolutionBouche(String evolutionBouche) {
        this.evolutionBouche = evolutionBouche;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public String getPersistanceAromatique() {
        return persistanceAromatique;
    }

    public void setPersistanceAromatique(String persistanceAromatique) {
        this.persistanceAromatique = persistanceAromatique;
    }

    public String getCaudaliesLongueur() {
        return caudaliesLongueur;
    }

    public void setCaudaliesLongueur(String caudaliesLongueur) {
        this.caudaliesLongueur = caudaliesLongueur;
    }

    public String getStructureLongueur() {
        return structureLongueur;
    }

    public void setStructureLongueur(String structureLongueur) {
        this.structureLongueur = structureLongueur;
    }

    public String getNoteFinale() {
        return noteFinale;
    }

    public void setNoteFinale(String noteFinale) {
        this.noteFinale = noteFinale;
    }

    public Integer getCaudaliesConclusion() {
        return caudaliesConclusion;
    }

    public void setCaudaliesConclusion(Integer caudaliesConclusion) {
        this.caudaliesConclusion = caudaliesConclusion;
    }

    public String getRemarquesConclusion() {
        return remarquesConclusion;
    }

    public void setRemarquesConclusion(String remarquesConclusion) {
        this.remarquesConclusion = remarquesConclusion;
    }

    public String getAccordsMetsVins() {
        return accordsMetsVins;
    }

    public void setAccordsMetsVins(String accordsMetsVins) {
        this.accordsMetsVins = accordsMetsVins;
    }

    public Double getTemperatureIdeale() {
        return temperatureIdeale;
    }

    public void setTemperatureIdeale(Double temperatureIdeale) {
        this.temperatureIdeale = temperatureIdeale;
    }

    public String getDateIdealeConsommation() {
        return dateIdealeConsommation;
    }

    public void setDateIdealeConsommation(String dateIdealeConsommation) {
        this.dateIdealeConsommation = dateIdealeConsommation;
    }

    public String getEvolutionProbable() {
        return evolutionProbable;
    }

    public void setEvolutionProbable(String evolutionProbable) {
        this.evolutionProbable = evolutionProbable;
    }
}
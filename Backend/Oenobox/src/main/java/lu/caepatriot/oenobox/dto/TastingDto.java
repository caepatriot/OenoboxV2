package lu.caepatriot.oenobox.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class TastingDto {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Informations sur le vin
    private WineTypeDto wineType;
    private List<CepageDto> cepages;
    private String region;
    private String aopIgpVdf;
    private String elevage;
    private String wineImport;
    private Double prixLancement;
    private Double prixActuel;

    // Aspect Visuel
    private String robeRouge;
    private String robeBlanche;
    private String robeRose;
    private String disque;
    private String intensiteVisuelle;
    private String limpidite;
    private String brillance;
    private String evolutionVisuelle;
    private String remarquesVisuelles;

    // Nez
    private String intensiteNez;
    private String qualiteNez;
    private String typeAromes;
    private Map<String, List<String>> aromesNature;
    private String descriptionNez;

    // Bouche
    private String attaque;
    private String evolutionBouche;
    private String structure;
    private String texture;

    // Longueur en bouche
    private String persistanceAromatique;
    private String caudaliesLongueur;
    private String structureLongueur;

    // Conclusion
    private String noteFinale;
    private Integer caudaliesConclusion;
    private String remarquesConclusion;
    private String accordsMetsVins;

    // Autres informations
    private Double temperatureIdeale;
    private String dateIdealeConsommation;
    private String evolutionProbable;

    // Constructors
    public TastingDto() {}

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

    public WineTypeDto getWineType() {
        return wineType;
    }

    public void setWineType(WineTypeDto wineType) {
        this.wineType = wineType;
    }

    public List<CepageDto> getCepages() {
        return cepages;
    }

    public void setCepages(List<CepageDto> cepages) {
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
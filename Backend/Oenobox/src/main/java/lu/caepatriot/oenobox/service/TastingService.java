package lu.caepatriot.oenobox.service;

import lu.caepatriot.oenobox.dto.TastingDto;
import lu.caepatriot.oenobox.entity.Tasting;
import lu.caepatriot.oenobox.repository.TastingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TastingService {

    @Autowired
    private TastingRepository tastingRepository;

    // Convert Entity to DTO
    private TastingDto convertToDto(Tasting tasting) {
        TastingDto dto = new TastingDto();
        dto.setId(tasting.getId());
        dto.setCreatedAt(tasting.getCreatedAt());
        dto.setUpdatedAt(tasting.getUpdatedAt());

        // Informations sur le vin
        dto.setWineType(tasting.getWineType());
        dto.setCepages(tasting.getCepages());
        dto.setRegion(tasting.getRegion());
        dto.setAopIgpVdf(tasting.getAopIgpVdf());
        dto.setElevage(tasting.getElevage());
        dto.setWineImport(tasting.getWineImport());
        dto.setPrixLancement(tasting.getPrixLancement());
        dto.setPrixActuel(tasting.getPrixActuel());

        // Aspect Visuel
        dto.setRobeRouge(tasting.getRobeRouge());
        dto.setRobeBlanche(tasting.getRobeBlanche());
        dto.setRobeRose(tasting.getRobeRose());
        dto.setDisque(tasting.getDisque());
        dto.setIntensiteVisuelle(tasting.getIntensiteVisuelle());
        dto.setLimpidite(tasting.getLimpidite());
        dto.setBrillance(tasting.getBrillance());
        dto.setEvolutionVisuelle(tasting.getEvolutionVisuelle());
        dto.setRemarquesVisuelles(tasting.getRemarquesVisuelles());

        // Nez
        dto.setIntensiteNez(tasting.getIntensiteNez());
        dto.setQualiteNez(tasting.getQualiteNez());
        dto.setTypeAromes(tasting.getTypeAromes());
        dto.setAromesNature(tasting.getAromesNature());
        dto.setDescriptionNez(tasting.getDescriptionNez());

        // Bouche
        dto.setAttaque(tasting.getAttaque());
        dto.setEvolutionBouche(tasting.getEvolutionBouche());
        dto.setStructure(tasting.getStructure());
        dto.setTexture(tasting.getTexture());

        // Longueur en bouche
        dto.setPersistanceAromatique(tasting.getPersistanceAromatique());
        dto.setCaudaliesLongueur(tasting.getCaudaliesLongueur());
        dto.setStructureLongueur(tasting.getStructureLongueur());

        // Conclusion
        dto.setNoteFinale(tasting.getNoteFinale());
        dto.setCaudaliesConclusion(tasting.getCaudaliesConclusion());
        dto.setRemarquesConclusion(tasting.getRemarquesConclusion());
        dto.setAccordsMetsVins(tasting.getAccordsMetsVins());

        // Autres informations
        dto.setTemperatureIdeale(tasting.getTemperatureIdeale());
        dto.setDateIdealeConsommation(tasting.getDateIdealeConsommation());
        dto.setEvolutionProbable(tasting.getEvolutionProbable());

        return dto;
    }

    // Convert DTO to Entity
    private Tasting convertToEntity(TastingDto dto) {
        Tasting tasting = new Tasting();

        if (dto.getId() != null) {
            tasting.setId(dto.getId());
        }

        // Informations sur le vin
        tasting.setWineType(dto.getWineType());
        tasting.setCepages(dto.getCepages());
        tasting.setRegion(dto.getRegion());
        tasting.setAopIgpVdf(dto.getAopIgpVdf());
        tasting.setElevage(dto.getElevage());
        tasting.setWineImport(dto.getWineImport());
        tasting.setPrixLancement(dto.getPrixLancement());
        tasting.setPrixActuel(dto.getPrixActuel());

        // Aspect Visuel
        tasting.setRobeRouge(dto.getRobeRouge());
        tasting.setRobeBlanche(dto.getRobeBlanche());
        tasting.setRobeRose(dto.getRobeRose());
        tasting.setDisque(dto.getDisque());
        tasting.setIntensiteVisuelle(dto.getIntensiteVisuelle());
        tasting.setLimpidite(dto.getLimpidite());
        tasting.setBrillance(dto.getBrillance());
        tasting.setEvolutionVisuelle(dto.getEvolutionVisuelle());
        tasting.setRemarquesVisuelles(dto.getRemarquesVisuelles());

        // Nez
        tasting.setIntensiteNez(dto.getIntensiteNez());
        tasting.setQualiteNez(dto.getQualiteNez());
        tasting.setTypeAromes(dto.getTypeAromes());
        tasting.setAromesNature(dto.getAromesNature());
        tasting.setDescriptionNez(dto.getDescriptionNez());

        // Bouche
        tasting.setAttaque(dto.getAttaque());
        tasting.setEvolutionBouche(dto.getEvolutionBouche());
        tasting.setStructure(dto.getStructure());
        tasting.setTexture(dto.getTexture());

        // Longueur en bouche
        tasting.setPersistanceAromatique(dto.getPersistanceAromatique());
        tasting.setCaudaliesLongueur(dto.getCaudaliesLongueur());
        tasting.setStructureLongueur(dto.getStructureLongueur());

        // Conclusion
        tasting.setNoteFinale(dto.getNoteFinale());
        tasting.setCaudaliesConclusion(dto.getCaudaliesConclusion());
        tasting.setRemarquesConclusion(dto.getRemarquesConclusion());
        tasting.setAccordsMetsVins(dto.getAccordsMetsVins());

        // Autres informations
        tasting.setTemperatureIdeale(dto.getTemperatureIdeale());
        tasting.setDateIdealeConsommation(dto.getDateIdealeConsommation());
        tasting.setEvolutionProbable(dto.getEvolutionProbable());

        return tasting;
    }

    // CRUD Operations
    public List<TastingDto> findAll() {
        return tastingRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<TastingDto> findById(Long id) {
        return tastingRepository.findById(id)
                .map(this::convertToDto);
    }

    public TastingDto save(TastingDto tastingDto) {
        Tasting tasting = convertToEntity(tastingDto);
        Tasting savedTasting = tastingRepository.save(tasting);
        return convertToDto(savedTasting);
    }

    public TastingDto update(Long id, TastingDto tastingDto) {
        if (!tastingRepository.existsById(id)) {
            throw new RuntimeException("Tasting not found with id: " + id);
        }
        tastingDto.setId(id);
        Tasting tasting = convertToEntity(tastingDto);
        Tasting updatedTasting = tastingRepository.save(tasting);
        return convertToDto(updatedTasting);
    }

    public void deleteById(Long id) {
        tastingRepository.deleteById(id);
    }

    // Business logic methods
    public List<TastingDto> findByWineType(String wineType) {
        return tastingRepository.findByWineType(wineType).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TastingDto> findByRegion(String region) {
        return tastingRepository.findByRegion(region).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TastingDto> findByCepage(String cepage) {
        return tastingRepository.findByCepage(cepage).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TastingDto> findRecentTastings() {
        return tastingRepository.findRecentTastings().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Long countByWineType(String wineType) {
        return tastingRepository.countByWineType(wineType);
    }
}
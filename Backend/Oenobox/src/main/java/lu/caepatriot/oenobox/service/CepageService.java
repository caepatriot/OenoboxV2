package lu.caepatriot.oenobox.service;


import lu.caepatriot.oenobox.dto.CepageDto;
import lu.caepatriot.oenobox.entity.Cepage;
import lu.caepatriot.oenobox.exception.ResourceNotFoundException;
import lu.caepatriot.oenobox.repository.CepageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CepageService {
    private final CepageRepository cepageRepository;

    public CepageService(CepageRepository cepageRepository) {
        this.cepageRepository = cepageRepository;
    }

    public List<CepageDto> getAllCepages() {
        return cepageRepository.findAll().stream()
                .map(cepage -> new CepageDto(cepage.getId(), cepage.getNom_cepage(), cepage.getCouleur()))
                .collect(Collectors.toList());
    }

//    public CepageDto createCepage(CepageDto cepageDto) {
//        Cepage cepage = new Cepage();
//        cepage.setName(cepageDto.getName());
//        cepage.setEmail(cepageDto.getEmail());
//        Cepage savedUser = cepageRepository.save(cepage);
//        return new CepageDto(savedUser.getId(), savedUser.getName(), savedUser.getEmail());
//    }

    public CepageDto getCepageById(Long id) {
        Cepage user = cepageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return new CepageDto(user.getId(), user.getNom_cepage(), user.getCouleur());
    }

}

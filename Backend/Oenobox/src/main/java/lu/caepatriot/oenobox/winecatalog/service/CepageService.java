package lu.caepatriot.oenobox.winecatalog.service;

import lu.caepatriot.oenobox.winecatalog.dto.CepageDto;
import lu.caepatriot.oenobox.winecatalog.entity.Wine;
import lu.caepatriot.oenobox.winecatalog.entity.Cepage;
import lu.caepatriot.oenobox.winecatalog.repository.CepageRepository;


import lu.caepatriot.oenobox.common.exception.ResourceNotFoundException;
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
                .map(cepage -> new CepageDto(cepage.getId(), cepage.getName(), null, cepage.getDescription(), null, null))
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
        Cepage cepage = cepageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cepage not found"));
        return new CepageDto(cepage.getId(), cepage.getName(), null, cepage.getDescription(), null, null);
    }

    public List<CepageDto> getCepagesByWineTypeId(Long wineTypeId) {
        return cepageRepository.findByWineTypeId(wineTypeId).stream()
                .map(cepage -> new CepageDto(cepage.getId(), cepage.getName(), null, cepage.getDescription(), null, null))
                .collect(Collectors.toList());
    }

}


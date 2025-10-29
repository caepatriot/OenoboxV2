package lu.caepatriot.oenobox.service;

import lu.caepatriot.oenobox.dto.AromaNoteDto;
import lu.caepatriot.oenobox.entity.AromaNote;
import lu.caepatriot.oenobox.repository.AromaNoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AromaNoteService {
    private final AromaNoteRepository aromaNoteRepository;

    public AromaNoteService(AromaNoteRepository aromaNoteRepository) {
        this.aromaNoteRepository = aromaNoteRepository;
    }

    public List<AromaNoteDto> getAllAromaNotes() {
        return aromaNoteRepository.findAll().stream()
                .map(aromaNote -> new AromaNoteDto(aromaNote.getId(), aromaNote.getName(), aromaNote.getDescription()))
                .collect(Collectors.toList());
    }

    public List<AromaNoteDto> getAromaNotesByCategory(Long categoryOptionId) {
        return aromaNoteRepository.findByCategoryOptionIdOrderByName(categoryOptionId).stream()
                .map(aromaNote -> new AromaNoteDto(aromaNote.getId(), aromaNote.getName(), aromaNote.getDescription()))
                .collect(Collectors.toList());
    }
}
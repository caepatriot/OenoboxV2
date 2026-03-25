package lu.caepatriot.oenobox.service.tasting;

import lu.caepatriot.oenobox.entity.tasting.Tasting;
import lu.caepatriot.oenobox.repository.tasting.AromaNoteRepository;
import lu.caepatriot.oenobox.entity.tasting.AromaNote;
import lu.caepatriot.oenobox.dto.tasting.AromaNoteDto;

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

package lu.caepatriot.oenobox.service;

import lu.caepatriot.oenobox.dto.WineDto;
import lu.caepatriot.oenobox.repository.WineRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WineService {
    private final WineRepository wineRepository;

    public WineService(WineRepository wineRepository) {
        this.wineRepository = wineRepository;
    }

//    public List<WineDto> getAllWines() {
//        return wineRepository.findAll().stream()
//                .map(user -> new WineDto(user.getId(), user.getName(), user.getEmail()))
//                .collect(Collectors.toList());
//    }
//
//    public WineDto createWine(WineDto wineDto) {
//        User user = new User();
//        user.setName(wineDto.getName());
//        user.setEmail(wineDto.getEmail());
//        User savedUser = userRepository.save(user);
//        return new UserDto(savedUser.getId(), savedUser.getName(), savedUser.getEmail());
//    }
//
//    public UserDto getUserById(Long id) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//        return new UserDto(user.getId(), user.getName(), user.getEmail());
//    }

}

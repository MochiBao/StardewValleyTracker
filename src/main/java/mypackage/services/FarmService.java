package mypackage.services;

import jakarta.transaction.Transactional;
import mypackage.entities.Farm;
import mypackage.entities.User;
import mypackage.repositories.FarmRepository;
import mypackage.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FarmService {
    private final FarmRepository farmRepository;
    private final UserRepository userRepository;

    public FarmService(FarmRepository farmRepository, UserRepository userRepository) {
        this.farmRepository = farmRepository;
        this.userRepository = userRepository;
    }

    public Farm createFarm (Long userId, String farmName, String farmType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Користувача з ID " + userId + " не знайдено!"));

        Farm newFarm = new Farm();
        newFarm.setFarmName(farmName);
        newFarm.setFarmType(farmType);

        newFarm.setUser(user);

        return farmRepository.save(newFarm);
    }

    public void deleteFarm(Long farmId) {
        if (farmRepository.existsById(farmId)) {
            farmRepository.deleteById(farmId);
        } else {
            throw new RuntimeException("Ферму з ID " + farmId + " не знайдено");
        }
    }
}

package mypackage.services;

import mypackage.entities.Farm;
import mypackage.entities.User;
import mypackage.repositories.FarmRepository;
import mypackage.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FarmServiceTest {
    @Mock
    private FarmRepository farmRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FarmService farmService;

    @Test
    void createFarm_whenUserIsCorrect_ShouldCreateAndSaveFarm () {
        Long userId = 1L;
        String farmName = "testFarm";
        String farmType = "Standart";

        User user  = new User();
        user.setId(userId);
        user.setUsername("StardewPro");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Farm farm = new Farm ();
        farm.setFarmName(farmName);
        farm.setFarmType(farmType);
        farm.setUser(user);

        when(farmRepository.save(any(Farm.class))).thenReturn(farm);

        Farm result = farmService.createFarm(userId, farmName, farmType);

        assertNotNull(result, "Створена ферма не повинна бути null");
        assertEquals(farmName, farm.getFarmName());
        assertEquals(farmType, farm.getFarmType());
        assertEquals(user, farm.getUser());

        verify(farmRepository, times(1)).save(any(Farm.class));
    }

    @Test
    void createFarm_whenUserNotFound_ShouldThrowException () {
        Long wrongUserId = 9999L;

        when(farmRepository.findById(wrongUserId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            farmService.createFarm(wrongUserId, "xz", "Standart");
        } );

        assertTrue(exception.getMessage().contains("не знайдено"));
        verify(farmRepository, never()).save(any(Farm.class));
    }

    @Test
    void deleteFarm_WhenFarmExists_ShouldDelete() {
        Long farmId = 1L;
        when(farmRepository.existsById(farmId)).thenReturn(true);
        farmService.deleteFarm(farmId);
        verify(farmRepository, times(1)).deleteById(farmId);
    }

    @Test
    void deleteFarm_WhenFarmDoesNotExists_ShouldThrowException() {
        Long farmId = 1L;
        when(farmRepository.existsById(farmId)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> {
            farmService.deleteFarm(farmId);
        });
        verify(farmRepository, never()).deleteById(farmId);
    }
}

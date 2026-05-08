package mypackage.services;

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
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private FarmRepository farmRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_WhenUsernameIsUnique_ShouldSaveUser () {
        String username = "StardewPro";
        String password = "12345";
        String email = "stardew@gmail.com";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        User testUser = new User();
        testUser.setUsername(username);
        testUser.setPassword(password);
        testUser.setEmail(email);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.registerUser(username, email, password);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(email, result.getEmail());

        verify(userRepository,times(1)).save(any(User.class));
    }

    @Test
    void registerUser_WhenUsernameIsNotUnique_ShouldThrowException () {
        String username = "StardewPro";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(username, "testemail@gmail.com", "123");
        });
        assertEquals("Помилка: Користувач з таким іменем вже існує!", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUser_WhenDataAreCorrect_ShouldReturnUser () {
        String username = "StardewPro";
        String password = "12345";

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User result = userService.loginUser(username, password);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    void loginUser_WhenUsernameIsNotFound_ShouldThrowException () {
        String username = "StardewPro";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.loginUser(username, "123");
        });

        assertEquals("Помилка: Фермера з таким іменем не знайдено!", exception.getMessage());
    }

    @Test
    void loginUser_WhenPasswordIsWrong_ShouldThrowException () {
        String username = "StardewPro";
        String password = "123";
        String wrongPassword = "wrong";

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.loginUser(username, wrongPassword);
        });

        assertEquals("Помилка: Неправильний пароль!", exception.getMessage());
    }
}


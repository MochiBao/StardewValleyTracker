package mypackage.services;

import mypackage.entities.Farm;
import mypackage.entities.User;
import mypackage.repositories.FarmRepository;
import org.springframework.stereotype.Service;
import mypackage.repositories.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService (UserRepository userRepository, FarmRepository farmRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String username, String email, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Помилка: Користувач з таким іменем вже існує!");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);

        return userRepository.save(user);
    }

    public User loginUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Помилка: Фермера з таким іменем не знайдено!"));
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Помилка: Неправильний пароль!");
        }
        return user;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
 }

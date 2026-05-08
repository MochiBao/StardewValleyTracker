package mypackage.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import mypackage.entities.User;
import mypackage.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void registerUser_WhenSuccessful_ShouldReturn200AndUser() throws Exception {
        UserController.AuthRequest request = new UserController.AuthRequest();
        request.setUsername("StardewPro");
        request.setEmail("pro@test.com");
        request.setPassword("password123");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("StardewPro");
        mockUser.setEmail("pro@test.com");

        when(userService.registerUser("StardewPro", "pro@test.com", "password123")).thenReturn(mockUser);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("StardewPro"))
                .andExpect(jsonPath("$.email").value("pro@test.com"));
    }

    @Test
    void registerUser_WhenError_ShouldReturn400() throws Exception {
        UserController.AuthRequest request = new UserController.AuthRequest();
        request.setUsername("StardewPro");
        request.setEmail("pro@test.com");
        request.setPassword("password123");

        when(userService.registerUser(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Помилка: Користувач з таким іменем вже існує!"));

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Помилка: Користувач з таким іменем вже існує!"));
    }

    @Test
    void loginUser_WhenSuccessful_ShouldReturn200AndUser() throws Exception {
        UserController.AuthRequest request = new UserController.AuthRequest();
        request.setUsername("StardewPro");
        request.setPassword("password123");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("StardewPro");

        when(userService.loginUser("StardewPro", "password123")).thenReturn(mockUser);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("StardewPro"));
    }

    @Test
    void loginUser_WhenError_ShouldReturn400() throws Exception {
        UserController.AuthRequest request = new UserController.AuthRequest();
        request.setUsername("StardewPro");
        request.setPassword("wrongpassword");

        when(userService.loginUser(anyString(), anyString()))
                .thenThrow(new RuntimeException("Помилка: Невірний пароль!"));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Помилка: Невірний пароль!"));
    }
}
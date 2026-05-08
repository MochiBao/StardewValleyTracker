package mypackage.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import mypackage.entities.Farm;
import mypackage.services.FarmService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FarmController.class)
class FarmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FarmService farmService;

    @Test
    void createFarm_WhenSuccessful_ShouldReturn200AndMessage() throws Exception {

        FarmController.CreateFarmRequest request = new FarmController.CreateFarmRequest();
        request.setUserId(1L);
        request.setFarmName("Sunny Farm");
        request.setFarmType("Standard");

        Farm mockFarm = new Farm();
        mockFarm.setFarmName("Sunny Farm");

        when(farmService.createFarm(1L, "Sunny Farm", "Standard")).thenReturn(mockFarm);

        mockMvc.perform(post("/api/farms/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Ферма 'Sunny Farm' успішно створена для юзера з ID: 1"));
    }

    @Test
    void createFarm_WhenErrorOccurs_ShouldReturn400AndErrorMessage() throws Exception {

        FarmController.CreateFarmRequest request = new FarmController.CreateFarmRequest();
        request.setUserId(99L);
        request.setFarmName("Ghost Farm");
        request.setFarmType("Standard");

        when(farmService.createFarm(anyLong(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Користувача з ID 99 не знайдено!"));

        mockMvc.perform(post("/api/farms/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Користувача з ID 99 не знайдено!"));
    }

    @Test
    void deleteFarm_WhenSuccessful_ShouldReturn200AndMessage() throws Exception {
        mockMvc.perform(delete("/api/farms/10"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ферму та весь прогрес видалено"));
        verify(farmService, times(1)).deleteFarm(10L);
    }

    @Test
    void deleteFarm_WhenErrorOccurs_ShouldReturn400AndErrorMessage() throws Exception {
        doThrow(new RuntimeException("Ферму з ID 99 не знайдено"))
                .when(farmService).deleteFarm(99L);

        mockMvc.perform(delete("/api/farms/99"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Помилка при видаленні: Ферму з ID 99 не знайдено"));
    }
}
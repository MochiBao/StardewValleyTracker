package mypackage.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import mypackage.entities.FarmCollectedItem;
import mypackage.entities.Item;
import mypackage.services.ProgressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProgressController.class)
class ProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProgressService progressService;

    @Test
    void collectItem_WhenSuccessful_ShouldReturn200AndMessage() throws Exception {
        ProgressController.CollectRequest request = new ProgressController.CollectRequest();
        request.setFarmId(1L);
        request.setItemId(10L);

        Item mockItem = new Item();
        mockItem.setName("Томат");
        FarmCollectedItem mockResult = new FarmCollectedItem();
        mockResult.setItem(mockItem);

        when(progressService.collectItem(1L, 10L)).thenReturn(mockResult);

        mockMvc.perform(post("/api/progress/collect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Ура! Ви успішно додали предмет 'Томат' до ферми!"));
    }

    @Test
    void collectItem_WhenErrorOccurs_ShouldReturn400() throws Exception {
        ProgressController.CollectRequest request = new ProgressController.CollectRequest();
        request.setFarmId(1L);
        request.setItemId(10L);

        when(progressService.collectItem(anyLong(), anyLong()))
                .thenThrow(new RuntimeException("Цей предмет вже є у вашій колекції!"));

        mockMvc.perform(post("/api/progress/collect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Цей предмет вже є у вашій колекції!"));
    }

    @Test
    void getProgress_WhenSuccessful_ShouldReturnListOfItems() throws Exception {
        Item item1 = new Item();
        item1.setName("Яблуко");
        Item item2 = new Item();
        item2.setName("Виноград");

        List<Item> mockItems = Arrays.asList(item1, item2);

        when(progressService.getCollectedItemsForFarm(1L)).thenReturn(mockItems);

        mockMvc.perform(get("/api/progress/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Яблуко"))
                .andExpect(jsonPath("$[1].name").value("Виноград"));
    }

    @Test
    void getProgress_WhenFarmNotFound_ShouldReturn400() throws Exception {
        when(progressService.getCollectedItemsForFarm(99L))
                .thenThrow(new RuntimeException("Ферму не знайдено!"));

        mockMvc.perform(get("/api/progress/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Ферму не знайдено!"));
    }

    @Test
    void removeCollectedItem_WhenSuccessful_ShouldReturn200AndMessage() throws Exception {
        mockMvc.perform(delete("/api/progress/1/delete/10"))
                .andExpect(status().isOk())
                .andExpect(content().string("Предмет успішно видалено з колекції ферми!"));

        verify(progressService, times(1)).removeCollectedItem(1L, 10L);
    }

    @Test
    void removeCollectedItem_WhenErrorOccurs_ShouldReturn400() throws Exception {
        doThrow(new RuntimeException("Цього предмета і так немає у вашій колекції!"))
                .when(progressService).removeCollectedItem(1L, 10L);

        mockMvc.perform(delete("/api/progress/1/delete/10"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Цього предмета і так немає у вашій колекції!"));
    }
}
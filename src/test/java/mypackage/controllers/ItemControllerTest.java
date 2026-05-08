package mypackage.controllers;

import mypackage.entities.Item;
import mypackage.services.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Test
    void getAllItems_ShouldReturnListOfItemsAndStatus200() throws Exception {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Сокира");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Лійка");

        when(itemService.getAllItems()).thenReturn(Arrays.asList(item1, item2));

        mockMvc.perform(get("/api/items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Сокира"))
                .andExpect(jsonPath("$[1].name").value("Лійка"));

        verify(itemService, times(1)).getAllItems();
    }

    @Test
    void getAllItems_WhenListIsEmpty_ShouldReturnEmptyArrayAndStatus200() throws Exception {
        when(itemService.getAllItems()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
        verify(itemService, times(1)).getAllItems();
    }
}
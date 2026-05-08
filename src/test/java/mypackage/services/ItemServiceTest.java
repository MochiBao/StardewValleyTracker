package mypackage.services;

import mypackage.entities.Item;
import mypackage.repositories.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @Test
    void getAllItems_ShouldReturnListOfItems() {
        Item item1 = new Item();
        item1.setName("Пастернак");
        Item item2 = new Item();
        item2.setName("Анчоус");
        when(itemRepository.findAll()).thenReturn(List.of(item1, item2));
        List<Item> result = itemService.getAllItems();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Пастернак", result.get(0).getName());
        verify(itemRepository, times(1)).findAll();
    }

}
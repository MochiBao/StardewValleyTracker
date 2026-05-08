package mypackage.services;

import mypackage.entities.Farm;
import mypackage.entities.FarmCollectedItem;
import mypackage.entities.FarmCollectedItemId;
import mypackage.entities.Item;
import mypackage.repositories.FarmCollectedItemRepository;
import mypackage.repositories.FarmRepository;
import mypackage.repositories.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {
    @Mock
    private FarmCollectedItemRepository farmCollectedItemRepository;

    @Mock
    private FarmRepository farmRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ProgressService progressService;

    @Test
    void collectItem_WhenValid_ShouldSaveAndReturnProgress() {
        Long farmId = 1L;
        Long itemId = 2L;
        Farm farm = new Farm();
        farm.setId(farmId);

        Item item  = new Item();
        item.setId(itemId);
        item.setName("Анчоус");

        when(farmRepository.findById(farmId)).thenReturn(Optional.of(farm));
        when(itemRepository.findById(farmId)).thenReturn(Optional.of(item));

        when(farmCollectedItemRepository.existsById(any(FarmCollectedItemId.class))).thenReturn(false);

        FarmCollectedItem progress = new FarmCollectedItem();
        progress.setItem(item);
        progress.setFarm(farm);
        when(farmCollectedItemRepository.save(any(FarmCollectedItem.class))).thenReturn(progress);

        FarmCollectedItem result = progressService.collectItem(farmId, itemId);

        assertNotNull(result);
        assertEquals(farm, result.getFarm());
        assertEquals(item, result.getItem());

        verify(farmCollectedItemRepository, times(1)).save(any(FarmCollectedItem.class));
    }

    @Test
    void collectItem_WhenInvalid_ShouldThrowException() {
        Long farmId = 1L;
        Long itemId = 2L;

        Farm farm = new Farm();
        farm.setId(farmId);

        Item item  = new Item();
        item.setId(itemId);
        item.setName("Анчоус");

        when(farmRepository.findById(farmId)).thenReturn(Optional.of(farm));
        when(itemRepository.findById(farmId)).thenReturn(Optional.of(item));

        when(farmCollectedItemRepository.existsById(any(FarmCollectedItemId.class))).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            progressService.collectItem(farmId, itemId);
        });

        assertTrue(exception.getMessage().contains("вже є у вашій колекції"));

        verify(farmCollectedItemRepository, never()).save(any());
    }

    @Test
    void getCollectedItemsForFarm_WhenFarmExists_ShouldReturnListOfItems() {
        Long farmId = 1L;
        when(farmRepository.existsById(farmId)).thenReturn(true);

        Item item1 = new Item();
        item1.setName("Риба");
        Item item2 = new Item();
        item2.setName("Камінь");

        FarmCollectedItem progress1 = new FarmCollectedItem();
        progress1.setItem(item1);
        FarmCollectedItem progress2 = new FarmCollectedItem();
        progress2.setItem(item2);

        when(farmCollectedItemRepository.findAllByFarmId(farmId)).thenReturn(List.of(progress1, progress2));

        List<Item> result = progressService.getCollectedItemsForFarm(farmId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Риба", result.get(0).getName());
        assertEquals("Камінь", result.get(1).getName());
    }

    @Test
    void getCollectedItemsForFarm_WhenFarmDoesNotExist_ShouldThrowException() {
        Long farmId = 99L;
        when(farmRepository.existsById(farmId)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            progressService.getCollectedItemsForFarm(farmId);
        });

        assertTrue(exception.getMessage().contains("не знайдено"));
    }

    @Test
    void removeCollectedItem_WhenItemExists_ShouldDelete() {
        Long farmId = 1L;
        Long itemId = 100L;

        when(farmCollectedItemRepository.existsById(any(FarmCollectedItemId.class))).thenReturn(true);

        progressService.removeCollectedItem(farmId, itemId);

        verify(farmCollectedItemRepository, times(1)).deleteById(any(FarmCollectedItemId.class));
    }

    @Test
    void removeCollectedItem_WhenItemNotCollected_ShouldThrowException() {
        Long farmId = 1L;
        Long itemId = 100L;

        when(farmCollectedItemRepository.existsById(any(FarmCollectedItemId.class))).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            progressService.removeCollectedItem(farmId, itemId);
        });

        assertEquals("Цього предмета і так немає у вашій колекції!", exception.getMessage());
        verify(farmCollectedItemRepository, never()).deleteById(any());
    }


}
package mypackage.services;

import mypackage.entities.Farm;
import mypackage.entities.FarmCollectedItem;
import mypackage.entities.FarmCollectedItemId;
import mypackage.entities.Item;
import mypackage.repositories.FarmCollectedItemRepository;
import mypackage.repositories.FarmRepository;
import mypackage.repositories.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProgressService {
    private final FarmCollectedItemRepository progressRepository;
    private final FarmRepository farmRepository;
    private final ItemRepository itemRepository;

    public ProgressService(FarmCollectedItemRepository progressRepository, FarmRepository farmRepository, ItemRepository itemRepository) {
        this.progressRepository = progressRepository;
        this.farmRepository = farmRepository;
        this.itemRepository = itemRepository;
    }


    public FarmCollectedItem collectItem(Long farmId, Long itemId) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new RuntimeException("Ферму з ID " + farmId + " не знайдено!"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Предмет з ID " + itemId + " не знайдено!"));

        FarmCollectedItemId compositeId = new FarmCollectedItemId();
        compositeId.setFarmId(farmId);
        compositeId.setItemId(itemId);

        if (progressRepository.existsById(compositeId)) {
            throw new RuntimeException("Цей предмет ('" + item.getName() + "') вже є у вашій колекції!");
        }

        FarmCollectedItem collectedItem = new FarmCollectedItem();
        collectedItem.setId(compositeId);
        collectedItem.setFarm(farm);
        collectedItem.setItem(item);

        return progressRepository.save(collectedItem);
    }

    public List<Item> getCollectedItemsForFarm(Long farmId) {
        if (!farmRepository.existsById(farmId)) {
            throw new RuntimeException("Ферму з ID " + farmId + " не знайдено!");
        }

        List<FarmCollectedItem> collectedRecords = progressRepository.findAllByFarmId(farmId);

        return collectedRecords.stream()
                .map(FarmCollectedItem::getItem)
                .collect(Collectors.toList());
    }


    public void removeCollectedItem(Long farmId, Long itemId) {
        FarmCollectedItemId compositeId = new FarmCollectedItemId();
        compositeId.setFarmId(farmId);
        compositeId.setItemId(itemId);

        if (!progressRepository.existsById(compositeId)) {
            throw new RuntimeException("Цього предмета і так немає у вашій колекції!");
        }

        progressRepository.deleteById(compositeId);
    }
}

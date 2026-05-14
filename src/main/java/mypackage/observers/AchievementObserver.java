package mypackage.observers;

import mypackage.events.ItemCollectedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
// Заміни на свій реальний репозиторій прогресу (наприклад, ProgressRepository або FarmItemRepository)
import mypackage.repositories.FarmCollectedItemRepository;

@Component
public class AchievementObserver {
    private final FarmCollectedItemRepository progressRepository;

    public AchievementObserver(FarmCollectedItemRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    @EventListener
    public void handleItemCollected(ItemCollectedEvent event) {
        Long farmId = event.getFarmId();
        long totalCollected = progressRepository.countByFarmId(farmId);

        if (totalCollected == 1) {
            event.addAchievement("Перший крок: Ви зібрали свій перший предмет!");
        } else if (totalCollected == 10) {
            event.addAchievement("Десяточка: Зібрано 10 предметів. Рюкзак стає важчим!");
        } else if (totalCollected == 50) {
            event.addAchievement("Місцевий герой: 50 предметів! Гюнтер був би вами задоволений.");
        }
    }

}
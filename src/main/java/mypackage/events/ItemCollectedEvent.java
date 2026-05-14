package mypackage.events;

import java.util.ArrayList;
import java.util.List;

public class ItemCollectedEvent {
    private final Long farmId;
    private final Long itemId;
    private final List<String> unlockedAchievements = new ArrayList<>();

    public ItemCollectedEvent(Long farmId, Long itemId) {
        this.farmId = farmId;
        this.itemId = itemId;
    }

    public Long getFarmId() { return farmId; }
    public Long getItemId() { return itemId; }

    public void addAchievement(String achievement) {
        this.unlockedAchievements.add(achievement);
    }

    public List<String> getUnlockedAchievements() {
        return unlockedAchievements;
    }
}
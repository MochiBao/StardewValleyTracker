package mypackage.controllers;

import mypackage.entities.FarmCollectedItem;
import mypackage.events.ItemCollectedEvent;
import mypackage.services.ProgressService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {
    private final ProgressService progressService;
    private final ApplicationEventPublisher eventPublisher;

    public ProgressController (ProgressService progressService, ApplicationEventPublisher eventPublisher) {
        this.progressService = progressService;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping
    public ResponseEntity<List<String>> collectItem(@RequestBody CollectRequest request) {
        try {
            progressService.collectItem(request.getFarmId(), request.getItemId());

            ItemCollectedEvent event = new ItemCollectedEvent(request.getFarmId(), request.getItemId());
            eventPublisher.publishEvent(event);

            return ResponseEntity.status(HttpStatus.CREATED).body(event.getUnlockedAchievements());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{farmId}")
    public ResponseEntity<?> getProgress(@PathVariable Long farmId) {
        try {
            return ResponseEntity.ok(progressService.getCollectedItemsForFarm(farmId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{farmId}/items/{itemId}")
    public ResponseEntity<Void> removeCollectedItem(@PathVariable Long farmId, @PathVariable Long itemId) {
        try {
            progressService.removeCollectedItem(farmId, itemId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public static class CollectRequest {
        private Long farmId;
        private Long itemId;

        public CollectRequest() {}

        public Long getFarmId() {
            return farmId;
        }
        public void setFarmId(Long farmId) {
            this.farmId = farmId;
        }

        public Long getItemId() {
            return itemId;
        }
        public void setItemId(Long itemId) {
            this.itemId = itemId;
        }
    }
}
//ідемпотентність, в чому мінуси використовувати на все один метод, шо таке кешебле, шо таке mvс (module),
//наскільки мачурні мої ендпоїнти
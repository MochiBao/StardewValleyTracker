package mypackage.controllers;

import mypackage.entities.Farm;
import mypackage.services.FarmService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/farms")
public class FarmController {
    private final FarmService farmService;

    public FarmController(FarmService farmService) {
        this.farmService = farmService;
    }

    @PostMapping
    public ResponseEntity<Farm> createFarm(@RequestBody CreateFarmRequest request) {
        try {
            Farm newFarm = farmService.createFarm(
                    request.getUserId(),
                    request.getFarmName(),
                    request.getFarmType()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(newFarm);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{farmId}")
    public ResponseEntity<Void> deleteFarm(@PathVariable Long farmId) {
        try {
            farmService.deleteFarm(farmId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public static class CreateFarmRequest {
        private Long userId;
        private String farmName;
        private String farmType;

        public CreateFarmRequest() {}

        public Long getUserId() {
            return userId;
        }
        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getFarmName() {
            return farmName;
        }
        public void setFarmName(String farmName) {
            this.farmName = farmName;
        }

        public String getFarmType() {
            return farmType;
        }
        public void setFarmType(String farmType) {
            this.farmType = farmType;
        }
    }
}

//package mypackage.controllers;
//
//import mypackage.entities.Farm;
//import mypackage.services.FarmService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/farms")
//public class FarmController {
//    private final FarmService farmService;
//
//    public FarmController(FarmService farmService) {
//        this.farmService = farmService;
//    }
//
//    @PostMapping
//    public ResponseEntity<Farm> createFarm(@RequestBody CreateFarmRequest request) {
//        try {
//            Farm newFarm = farmService.createFarm(
//                    request.getUserId(),
//                    request.getFarmName(),
//                    request.getFarmType()
//            );
//            return ResponseEntity.status(HttpStatus.CREATED).body(newFarm);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @DeleteMapping("/{farmId}")
//    public ResponseEntity<Void> deleteFarm(@PathVariable Long farmId) {
//        try {
//            farmService.deleteFarm(farmId);
//            return ResponseEntity.noContent().build();
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    public static class CreateFarmRequest {
//        private Long userId;
//        private String farmName;
//        private String farmType;
//
//        public CreateFarmRequest() {}
//
//        public Long getUserId() {
//            return userId;
//        }
//        public void setUserId(Long userId) {
//            this.userId = userId;
//        }
//
//        public String getFarmName() {
//            return farmName;
//        }
//        public void setFarmName(String farmName) {
//            this.farmName = farmName;
//        }
//
//        public String getFarmType() {
//            return farmType;
//        }
//        public void setFarmType(String farmType) {
//            this.farmType = farmType;
//        }
//    }
//}

package mypackage.controllers;

import mypackage.entities.Farm;
import mypackage.entities.User;
import mypackage.services.FarmService;
import mypackage.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/farms")
public class FarmController {
    private final FarmService farmService;
    private final UserService userService; // Додали UserService, щоб шукати користувача в базі

    public FarmController(FarmService farmService, UserService userService) {
        this.farmService = farmService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Farm> createFarm(@RequestBody CreateFarmRequest request, Authentication authentication) {
        try {
            String email = "";
            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2User oauth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
                email = oauth2User.getAttribute("email");
            } else {
                email = authentication.getName();
            }


            User currentUser = userService.findByEmail(email);

            if (currentUser == null && authentication instanceof OAuth2AuthenticationToken) {
                OAuth2User oauth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
                String name = oauth2User.getAttribute("name");
                currentUser = userService.registerUser(name, email, "google_oauth_" + System.currentTimeMillis());
            }


            Farm newFarm = farmService.createFarm(
                    currentUser.getId(),
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
package hr.algebra.cugomat.controller;

import hr.algebra.cugomat.dto.UserDTO;
import hr.algebra.cugomat.mapper.UserMapper;
import hr.algebra.cugomat.models.User;
import hr.algebra.cugomat.service.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final ApiService service;

    public UserController(ApiService service) {
        this.service = service;
    }

    @GetMapping("/byUsername")
    public ResponseEntity<UserDTO> getUserByUsername(@RequestParam String username) {
        try {
            if (username != null && !username.isEmpty()) {
                log.info("Get user called for username: {}", username);
                User user = service.getUserByUsername(username);
                if (user != null) {
                    UserDTO dto = UserMapper.toDTO(user);
                    return ResponseEntity.ok(dto);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error while user by username: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Integer> registerUser(@RequestBody UserDTO dto) {
        try {
            if (dto != null) {
                log.info("Register user called with DTO: {}", dto);
                Integer userId = service.registerUser(dto);
                if (userId != null) {
                    return ResponseEntity.ok(userId);
                } else {
                    return ResponseEntity.badRequest().build();
                }
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error on user register: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/checkExist")
    public ResponseEntity<Boolean> checkUserExist(@RequestParam String username) {
        try {
            if (username != null && !username.isEmpty()) {
                log.info("Check user exist called for: {}", username);
                Boolean exist = service.checkUserExist(username);
                if (exist != null) {
                    return ResponseEntity.ok(exist);
                } else {
                    return ResponseEntity.badRequest().build();
                }
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error checking user exist: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}

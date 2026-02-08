package hr.algebra.cugomat.controller;

import hr.algebra.cugomat.dto.CategoryCreateDTO;
import hr.algebra.cugomat.dto.CategoryDTO;
import hr.algebra.cugomat.dto.CategoryUpdateDTO;
import hr.algebra.cugomat.service.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final ApiService service;

    public CategoryController(ApiService service) {
        this.service = service;
    }

    @GetMapping("/list")
    public ResponseEntity<List<CategoryDTO>> getCategoryList(@RequestParam String clientCode) {
        try {
            if (clientCode != null && !clientCode.isEmpty()) {
                log.info("Category list called: {}", clientCode);
                List<CategoryDTO> categoryList = service.getCategoryList(clientCode);

                if (categoryList.isEmpty()) {
                    return ResponseEntity.notFound().build();
                } else {
                    return ResponseEntity.ok(categoryList);
                }
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error while getting category list: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Integer> createCategory(@RequestBody CategoryCreateDTO dto) {
        try {
            if (dto != null) {
                log.info("Category create called DTO: {}", dto);
                Integer createdId = service.createCategory(dto);
                return ResponseEntity.ok(createdId);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error while creating category: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Boolean> updateCategory(@RequestBody CategoryUpdateDTO dto) {
        try {
            if (dto != null) {
                log.info("Category update called DTO: {}", dto);
                boolean updatedId = service.updateCategory(dto);
                return ResponseEntity.ok(updatedId);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error while updating category: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteCategory(@RequestParam Integer categoryId) {
        try {
            if (categoryId != null) {
                log.info("Category delete called id: {}", categoryId);
                boolean deleted = service.deleteCategory(categoryId);
                return ResponseEntity.ok(deleted);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error while deleting category: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}

package hr.algebra.cugomat.controller;

import hr.algebra.cugomat.dto.ProductCreateDTO;
import hr.algebra.cugomat.dto.ProductDTO;
import hr.algebra.cugomat.dto.ProductUpdateDTO;
import hr.algebra.cugomat.service.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final ApiService service;

    public ProductController(ApiService service) {
        this.service = service;
    }

    @GetMapping("/list")
    public ResponseEntity<List<ProductDTO>> getProductList(@RequestParam String clientCode, @RequestParam(required = false) Integer categoryId) {
        try {
            if (clientCode != null && !clientCode.isEmpty()) {
                log.info("Product list called for client: {} categoryId: {}", clientCode, categoryId);
                List<ProductDTO> productList = service.getProductList(clientCode, categoryId);
                if (productList.isEmpty()) {
                    return ResponseEntity.notFound().build();
                } else {
                    return ResponseEntity.ok(productList);
                }
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error while getting product list: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<ProductDTO> getProductById(@RequestParam Integer productId) {
        try {
            if (productId != null) {
                log.info("Get product called for id: {}", productId);
                ProductDTO product = service.getProductById(productId);
                if (product == null) {
                    return ResponseEntity.notFound().build();
                } else {
                    return ResponseEntity.ok(product);
                }
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error while getting product by id: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Integer> createProduct(@RequestBody ProductCreateDTO dto) {
        try {
            if (dto != null) {
                log.info("Create product called with DTO: {}", dto);
                Integer createdId = service.createProduct(dto);
                if (createdId != null) {
                    return ResponseEntity.ok(createdId);
                } else {
                    return ResponseEntity.badRequest().body(null);
                }
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error while creating product: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Boolean> updateProduct(@RequestBody ProductUpdateDTO dto) {
        try {
            if (dto != null) {
                log.info("Update product called with DTO: {}", dto);
                boolean updated = service.updateProduct(dto);
                return ResponseEntity.ok(updated);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error while updating product: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteProduct(@RequestParam Integer productId) {
        try {
            if (productId != null) {
                log.info("Product delete called id: {}", productId);
                boolean deleted = service.deleteProduct(productId);
                return ResponseEntity.ok(deleted);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error while deleting product: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}

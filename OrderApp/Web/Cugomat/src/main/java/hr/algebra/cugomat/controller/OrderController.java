package hr.algebra.cugomat.controller;

import hr.algebra.cugomat.dto.*;
import hr.algebra.cugomat.service.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/order")
public class
OrderController {
    private final ApiService apiService;

    public OrderController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/active")
    public ResponseEntity<List<OrderDTO>> getOrdersByStatus(@RequestParam String clientCode) {
        try {
            if (clientCode != null && !clientCode.isEmpty()) {
                log.info("Order list by status called for client code: {}", clientCode);
                List<OrderDTO> orderList = apiService.getOrderListActive(clientCode);

                if (orderList.isEmpty()) {
                    return ResponseEntity.notFound().build();
                } else {
                    return ResponseEntity.ok(orderList);
                }
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error while getting orders by status: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/byUser")
    public ResponseEntity<List<OrderDTO>> getOrdersByUser(@RequestParam String userName) {
        try {
            if (userName != null && !userName.isEmpty()) {
                log.info("Order list by user called for: {}", userName);
                List<OrderDTO> orderList = apiService.getOrderListByUser(userName);
                if (orderList.isEmpty()) {
                    return ResponseEntity.notFound().build();
                } else {
                    return ResponseEntity.ok(orderList);
                }
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error while getting orders by user: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/byFilter")
    public ResponseEntity<List<OrderDTO>> getOrdersByFilter(@RequestBody OrderFilterDTO dto) {
        try {
            if (dto != null) {
                log.info("Order list by filter called with DTO: {}", dto);
                List<OrderDTO> orderList = apiService.getOrderListByFilter(dto);

                if (orderList.isEmpty()) {
                    return ResponseEntity.notFound().build();
                } else {
                    return ResponseEntity.ok(orderList);
                }
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error while getting orders by filter: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/setStatus")
    public ResponseEntity<Boolean> setOrderStatus(@RequestBody ChangeOrderStatusDTO dto) {
        try {
            if (dto != null) {
                log.info("Set order status called with DTO: {}", dto);
                Boolean updated = apiService.setOrderStatus(dto);
                return ResponseEntity.ok(updated);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error while setting order status: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Integer> creteOrder(@RequestBody OrderCreateDTO dto) {
        try {
            if (dto != null) {
                log.info("Create order called with DTO: {}", dto);
                Integer newId = apiService.createOrder(dto);

                if (newId != null) {
                    return ResponseEntity.ok(newId);
                } else {
                    return ResponseEntity.badRequest().build();
                }
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error while creating order: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/checkDiscount")
    public ResponseEntity<Boolean> checkDiscount(@RequestParam String user, @RequestParam String clientCode) {
        try {
            if (user != null && !user.isEmpty() && clientCode != null && !clientCode.isEmpty()) {
                log.info("Check discount called for user: {} client: {}", user, clientCode);
                Boolean actionAvailable = apiService.checkDiscount(user, clientCode);
                if (actionAvailable != null) {
                    return ResponseEntity.ok(actionAvailable);
                } else {
                    return ResponseEntity.badRequest().build();
                }
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error while checking discount: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}

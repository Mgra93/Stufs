package hr.algebra.cugomat.controller;

import hr.algebra.cugomat.dto.*;
import hr.algebra.cugomat.service.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/worker")
public class WorkerController {
    private final ApiService service;

    public WorkerController(ApiService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<WorkerDTO> getWorker(@RequestParam String username) {
        try {
            if (username != null && !username.isEmpty()) {
                log.info("Getting worker for user: {}", username);
                WorkerDTO worker = service.getWorker(username);

                if (worker == null) {
                    return ResponseEntity.notFound().build();
                } else {
                    return ResponseEntity.ok(worker);
                }
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error while getting worker: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<WorkerDTO>> getWorkerList(@RequestParam String clientCode) {
        try {
            if (clientCode != null && !clientCode.isEmpty()) {
                log.info("Getting worker list for client: {}", clientCode);
                List<WorkerDTO> workerList = service.getWorkerList(clientCode);

                if (workerList == null || workerList.isEmpty()) {
                    return ResponseEntity.notFound().build();
                } else {
                    return ResponseEntity.ok(workerList);
                }
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error while getting worker list: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Integer> createWorker(@RequestBody WorkerCreateDTO dto) {
        try {
            if (dto != null) {
                log.info("Create worker called with DTO: {}", dto);
                Integer createdId = service.createWorker(dto);
                return ResponseEntity.ok(createdId);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error while creating worker: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Boolean> updateWorker(@RequestBody WorkerUpdateDTO dto) {
        try {
            if (dto != null) {
                log.info("Update worker called with DTO: {}", dto);
                boolean updated = service.updateWorker(dto);
                return ResponseEntity.ok(updated);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error while updating worker: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteWorker(@RequestParam Integer workerId) {
        try {
            if (workerId != null) {
                log.info("Delete worker called for id: {}", workerId);
                boolean deleted = service.deleteWorker(workerId);
                return ResponseEntity.ok(deleted);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error while deleting worker: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}

package hr.algebra.cugomat.controller;

import hr.algebra.cugomat.dto.ClientDTO;
import hr.algebra.cugomat.dto.ClientUpdateDTO;
import hr.algebra.cugomat.service.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/client")
public class ClientController {

    private final ApiService service;

    public ClientController(ApiService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ClientDTO> getClient(@RequestParam String code) {
        try {
            if (code != null && !code.isEmpty()) {
                log.info("Client get called for code: {}", code);
                ClientDTO client = service.getClient(code);
                if (client == null) {
                    return ResponseEntity.notFound().build();
                } else {
                    return ResponseEntity.ok(client);
                }
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error while getting client: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Boolean> updateClient(@RequestBody ClientUpdateDTO dto) {
        try {
            if (dto != null) {
                log.info("Client update called DTO: {}", dto);
                Boolean updated = service.updateClient(dto);
                if (updated != null) {
                    return ResponseEntity.ok(updated);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error while updating client: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}

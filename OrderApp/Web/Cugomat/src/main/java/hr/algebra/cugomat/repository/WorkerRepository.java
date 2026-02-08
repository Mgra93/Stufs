package hr.algebra.cugomat.repository;

import hr.algebra.cugomat.models.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, Integer> {
    Optional<Worker> findByUserUsername(String username);
    List<Worker> findByClient_Code(String clientCode);
}

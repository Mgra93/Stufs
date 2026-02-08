package hr.algebra.cugomat.repository;

import hr.algebra.cugomat.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository  extends JpaRepository<Client, Integer> {
    Optional<Client> findByCode(String code);
}

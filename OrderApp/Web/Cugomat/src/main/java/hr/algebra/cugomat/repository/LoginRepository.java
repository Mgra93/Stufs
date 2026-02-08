package hr.algebra.cugomat.repository;

import hr.algebra.cugomat.models.Login;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login, Integer> {
}

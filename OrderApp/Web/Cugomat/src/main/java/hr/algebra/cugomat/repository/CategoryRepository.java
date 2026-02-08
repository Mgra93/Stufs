package hr.algebra.cugomat.repository;

import hr.algebra.cugomat.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByClientId(int clientId);
}

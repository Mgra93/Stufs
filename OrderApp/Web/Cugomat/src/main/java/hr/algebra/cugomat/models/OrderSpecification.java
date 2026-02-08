package hr.algebra.cugomat.models;

import hr.algebra.cugomat.dto.OrderFilterDTO;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import hr.algebra.cugomat.models.Order;

public class OrderSpecification {

    public static Specification<Order> filter(OrderFilterDTO filterDTO) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filterDTO.getCompanyCode() != null && !filterDTO.getCompanyCode().isEmpty()) {
                predicates.add(cb.equal(root.get("client").get("code"), filterDTO.getCompanyCode()));
            }

            if (filterDTO.getWorker() != null && !filterDTO.getWorker().isEmpty()) {
                predicates.add(cb.equal(root.get("worker").get("username"), filterDTO.getWorker()));
            }

            if (filterDTO.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filterDTO.getStatus()));
            }

            if (filterDTO.getDate() != null) {
                LocalDateTime startOfDay = filterDTO.getDate().atStartOfDay();
                LocalDateTime endOfDay = filterDTO.getDate().atTime(LocalTime.MAX);
                predicates.add(cb.between(root.get("createdOn"), startOfDay, endOfDay));
            }

            if (filterDTO.getUser() != null && !filterDTO.getUser().isEmpty()) {
                predicates.add(
                        cb.like(
                                cb.concat(cb.concat(root.get("user").get("firstName"), " "), root.get("user").get("lastName")),
                                "%" + filterDTO.getUser() + "%"
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

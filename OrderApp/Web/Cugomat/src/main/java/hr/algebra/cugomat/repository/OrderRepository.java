package hr.algebra.cugomat.repository;

import hr.algebra.cugomat.models.Client;
import hr.algebra.cugomat.models.Order;
import hr.algebra.cugomat.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository  extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
    List<Order> findByClient_CodeAndStatusIn(String clientCode, List<Integer> statusList);
    List<Order> findByUser(User user);
    List<Order> findByUserAndClient(User user, Client client);
}

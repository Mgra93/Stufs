package hr.algebra.cugomat.service;

import hr.algebra.cugomat.models.Client;
import hr.algebra.cugomat.models.Order;
import hr.algebra.cugomat.models.User;
import hr.algebra.cugomat.repository.ClientRepository;
import hr.algebra.cugomat.repository.OrderRepository;
import hr.algebra.cugomat.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApiServiceTest {
    private UserRepository userRepository;
    private ClientRepository clientRepository;
    private OrderRepository orderRepository;
    private ApiServiceImpl apiService;

    private final String userName = "pero";
    private final String clientCode = "CBS";

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        clientRepository = mock(ClientRepository.class);
        orderRepository = mock(OrderRepository.class);
        apiService = new ApiServiceImpl(
                clientRepository, null, null,
                orderRepository, null, userRepository,
                null, null
        );
    }

    @Test
    void testCheckDiscountUserHaveDiscount() {
        User user = new User();
        user.setUsername(userName);

        Client client = new Client();
        client.setCode(clientCode);
        client.setVipDayActive(true);
        client.setVipDayCode(LocalDate.now().getDayOfWeek().getValue());

        List<Order> orders = List.of(
                createOrder(LocalDate.now().minusWeeks(1)),
                createOrder(LocalDate.now().minusWeeks(1)),
                createOrder(LocalDate.now().minusWeeks(1))
        );

        when(userRepository.findByUsername(userName)).thenReturn(user);
        when(clientRepository.findByCode(clientCode)).thenReturn(Optional.of(client));
        when(orderRepository.findByUserAndClient(user, client)).thenReturn(orders);
        Boolean result = apiService.checkDiscount(userName, clientCode);
        assertTrue(result);
    }

    @Test
    void testCheckDiscountNotVipDay() {
        User user = new User();
        Client client = new Client();
        client.setVipDayActive(true);
        client.setVipDayCode((LocalDate.now().getDayOfWeek().getValue() % 7) + 1);

        when(userRepository.findByUsername(userName)).thenReturn(user);
        when(clientRepository.findByCode(clientCode)).thenReturn(Optional.of(client));
        when(orderRepository.findByUserAndClient(user, client)).thenReturn(null);
        Boolean result = apiService.checkDiscount(userName, clientCode);
        assertFalse(result);
    }

    private Order createOrder(LocalDate date) {
        Order order = new Order();
        order.setCreatedOn(date.atStartOfDay());
        return order;
    }
}

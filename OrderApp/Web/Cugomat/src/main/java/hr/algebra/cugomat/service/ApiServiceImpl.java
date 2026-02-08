package hr.algebra.cugomat.service;

import hr.algebra.cugomat.dto.*;
import hr.algebra.cugomat.enums.OrderStatus;
import hr.algebra.cugomat.enums.UserRole;
import hr.algebra.cugomat.mapper.*;
import hr.algebra.cugomat.models.*;
import hr.algebra.cugomat.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApiServiceImpl implements ApiService {
    private ClientRepository clientRepository;
    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;
    private OrderRepository orderRepository;
    private WorkerRepository workerRepository;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private LoginRepository loginRepository;

    private static final Logger log = LoggerFactory.getLogger(ApiServiceImpl.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ApiServiceImpl(
            ClientRepository clientRepository, CategoryRepository categoryRepository, ProductRepository productRepository,
            OrderRepository orderRepository, WorkerRepository workerRepository, UserRepository userRepository,
            RoleRepository roleRepository, LoginRepository loginRepository) {
        this.clientRepository = clientRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.workerRepository = workerRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.loginRepository = loginRepository;
    }

    @Override
    public List<CategoryDTO> getCategoryList(String clientCode) {
        Optional<Client> client = clientRepository.findByCode(clientCode);
        if (client.isEmpty()) {
            return List.of();
        }
        int clientId = client.get().getId();
        List<Category> categories = categoryRepository.findByClientId(clientId);
        return categories.stream()
                .filter(Category::getActive)
                .map(CategoryMapper::toDTO)
                .toList();
    }

    @Override
    public CategoryDTO getCategoryById(Integer id) {
        Optional<Category> category = categoryRepository.findById(id);
        return category.map(CategoryMapper::toDTO).orElse(null);
    }

    @Override
    public Integer createCategory(CategoryCreateDTO dto) {
        Optional<Client> opCategory = clientRepository.findByCode(dto.getClientCode());
        if (opCategory.isEmpty()) {
            log.error("Client with code {} not found", dto.getClientCode());
            return null;
        }
        Category category = CategoryMapper.toEntity(dto);
        category.setClient(opCategory.get());
        category.setActive(true);
        category.setCreatedOn(LocalDateTime.now());
        categoryRepository.save(category);
        return category.getId();
    }

    @Override
    public boolean updateCategory(CategoryUpdateDTO dto) {
        if (dto.getId() == null) {
            return false;
        }
        Optional<Category> existingOpt = categoryRepository.findById(dto.getId());
        if (existingOpt.isEmpty()) {
            return false;
        }
        Category category = existingOpt.get();
        category.setName(dto.getName());
        categoryRepository.save(category);
        return true;
    }

    @Override
    public boolean deleteCategory(Integer id) {
        Optional<Category> existingOpt = categoryRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return false;
        }
        Category deletedCategory = existingOpt.get();
        deletedCategory.setActive(false);

        categoryRepository.save(deletedCategory);
        return true;
    }

    @Override
    public List<ProductDTO> getProductList(String clientCode, Integer categoryId) {
        Optional<Client> clientOpt = clientRepository.findByCode(clientCode);
        if (clientOpt.isEmpty()) return List.of();

        int clientId = clientOpt.get().getId();
        List<Product> productList;

        if (categoryId != null) {
            productList = productRepository.findByClientIdAndCategoryId(clientId, categoryId);
        } else {
            productList = productRepository.findByClientId(clientId);
        }

        return productList.stream()
                .filter(Product::getActive)
                .map(ProductMapper::toDTO)
                .toList();
    }

    @Override
    public ProductDTO getProductById(Integer id) {
        Optional<Product> product = productRepository.findById(id);
        return product.map(ProductMapper::toDTO).orElse(null);
    }

    @Override
    public Integer createProduct(ProductCreateDTO dto) {
        Optional<Client> clientOptional = clientRepository.findByCode(dto.getClientCode());
        if (clientOptional.isEmpty()) {
            log.error("Client with code {} not found", dto.getClientCode());
            return null;
        }

        Optional<Category> categoryOpt = categoryRepository.findById(dto.getCategoryId());

        if (categoryOpt.isEmpty()) {
            log.error("Category with id {} not found", dto.getClientCode());
            return null;
        }

        Product product = ProductMapper.toEntity(dto);
        product.setActive(true);
        product.setCreatedOn(LocalDateTime.now());
        product.setCategory(categoryOpt.get());
        product.setClient(clientOptional.get());
        productRepository.save(product);
        return product.getId();
    }

    @Override
    public boolean updateProduct(ProductUpdateDTO dto) {
        if (dto.getId() == null) {
            return false;
        }

        Optional<Product> existingProduct = productRepository.findById(dto.getId());

        if (existingProduct.isEmpty()) {
            return false;
        }

        Optional<Category> existingCategory = categoryRepository.findById(dto.getCategoryId());
        if (existingCategory.isEmpty()) {
            return false;
        }

        Product product = existingProduct.get();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setCategory(existingCategory.get());
        productRepository.save(product);
        return true;
    }

    @Override
    public boolean deleteProduct(Integer id) {
        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isEmpty()) {
            return false;
        }
        Product deletedProduct = existingProduct.get();
        deletedProduct.setActive(false);
        productRepository.save(deletedProduct);
        return true;
    }

    @Override
    public Integer createOrder(OrderCreateDTO orderDTO) {
        Order order = new Order();
        order.setCreatedOn(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED.getCode());
        order.setTableCode(orderDTO.getTableCode());

        Client client = clientRepository.findByCode(orderDTO.getClientCode())
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));
        order.setClient(client);

        User user = userRepository.findByUsername(orderDTO.getUser());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        order.setUser(user);

        List<OrderProduct> orderProducts = new ArrayList<>();

        for (ProductDTO productDTO : orderDTO.getProductList()) {
            Product product = productRepository.findById(productDTO.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);
            int quantity = productDTO.getQuantity() != null ? productDTO.getQuantity() : 1;
            orderProduct.setQuantity(quantity);
            orderProducts.add(orderProduct);
        }

        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setFinalPrice(orderDTO.getFinalPrice());
        order.setHasDiscount(orderDTO.getHasDiscount());
        order.setOrderProducts(orderProducts);
        orderRepository.save(order);
        return order.getId();
    }

    @Override
    public Boolean setOrderStatus(ChangeOrderStatusDTO changeDTO) {
        User existingWorker = userRepository.findByUsername(changeDTO.getWorkerUsername());
        if (existingWorker == null) {
            return false;
        }

        Optional<Order> existingOrder = orderRepository.findById(changeDTO.getOrderId());

        if (existingOrder.isEmpty()) {
            return false;
        }

        Order order = existingOrder.get();
        order.setStatus(changeDTO.getStatus());
        order.setWorker(existingWorker);
        orderRepository.save(order);
        return true;
    }

    @Override
    public Boolean checkDiscount(String username, String clientCode) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("User {} not found", username);
            return false;
        }

        Optional<Client> clientOpt = clientRepository.findByCode(clientCode);
        if (clientOpt.isEmpty()) {
            log.error("Client {} not found", clientCode);
            return false;
        }

        Client client = clientOpt.get();
        if (client.getVipDayActive() == null || !client.getVipDayActive()) {
            return false;
        }

        Integer vipDayCode = client.getVipDayCode();
        if (vipDayCode == null) {
            return false;
        }

        int today = LocalDate.now().getDayOfWeek().getValue();
        if (today != vipDayCode) {
            return false;
        }

        List<Order> orders = orderRepository.findByUserAndClient(user, client);

        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate startOfLastWeek = startOfWeek.minusWeeks(1);

        long lastWeekOrderCount = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED.getCode())
                .filter(o -> {
                    LocalDate d = o.getCreatedOn().toLocalDate();
                    return !d.isBefore(startOfLastWeek) && d.isBefore(startOfWeek);
                })
                .count();

        long thisWeekOrderCount = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED.getCode())
                .filter(o -> {
                    LocalDate d = o.getCreatedOn().toLocalDate();
                    return !d.isBefore(startOfWeek);
                })
                .count();
        if (lastWeekOrderCount < 2) {
            return false;
        }
        if (thisWeekOrderCount > 0) {
            return false;
        }
        return true;
    }


    @Override
    public WorkerDTO getWorker(String username) {
        Optional<Worker> workerOpt = workerRepository.findByUserUsername(username);
        if (workerOpt.isEmpty()) {
            return null;
        }
        Worker worker = workerOpt.get();
        return WorkerMapper.toDTO(worker);
    }

    @Override
    public List<WorkerDTO> getWorkerList(String clientCode) {
        Optional<Client> clientOpt = clientRepository.findByCode(clientCode);
        if (clientOpt.isEmpty()) {
            return List.of();
        }
        List<Worker> workers = workerRepository.findByClient_Code(clientCode);
        return workers.stream()
                .filter(w -> w.getUser().getActive())
                .map(WorkerMapper::toDTO)
                .toList();
    }

    @Override
    public Integer createWorker(WorkerCreateDTO dto) {
        Optional<Client> opClient = clientRepository.findByCode(dto.getClientCode());

        if (opClient.isEmpty()) {
            log.error("Client with code {} not found", dto.getClientCode());
            return null;
        }

        Client client = opClient.get();
        UserDTO userDTO = dto.getWorker();
        User userOld = userRepository.findByUsername(dto.getWorker().getUsername());
        User user;
        if (userOld != null) {
            user = userOld;
        } else {
            user = new User();
            user.setUsername(userDTO.getUsername());
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setEmail(userDTO.getEmail());

            if (userDTO.getPhone() != null && !userDTO.getPhone().isEmpty()) {
                user.setPhone(userDTO.getPhone());
            }

            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setActive(true);
            user.setCreatedOn(LocalDateTime.now());

            Optional<Role> role = roleRepository.findByName(UserRole.WORKER.getCode());

            if (role.isEmpty()) {
                return null;
            }

            user.setRole(role.get());
            userRepository.save(user);
        }
        Worker worker = new Worker();
        worker.setUser(user);
        worker.setClient(client);
        workerRepository.save(worker);
        return worker.getId();
    }


    @Override
    public boolean updateWorker(WorkerUpdateDTO dto) {
        if (dto.getUsername() == null) {
            return false;
        }
        User user = userRepository.findByUsername(dto.getUsername());
        if (user == null) {
            log.error("User with username {} not found", dto.getUsername());
            return false;
        }
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        if (dto.getChangePassword()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean deleteWorker(Integer workerId) {
        Optional<Worker> opWorker = workerRepository.findById(workerId);

        if (opWorker.isEmpty()) {
            return false;
        }

        Worker worker = opWorker.get();
        User user = worker.getUser();
        if (user == null) {
            log.error("User not found for worker with id {}", workerId);
            return false;
        }

        user.setActive(false);
        userRepository.save(user);
        return true;
    }


    @Override
    public List<OrderDTO> getOrderListActive(String clientCode) {
        Optional<Client> clientOpt = clientRepository.findByCode(clientCode);

        if (clientOpt.isEmpty()) {
            return List.of();
        }

        List<Integer> statusList = List.of(OrderStatus.CREATED.getCode(), OrderStatus.RECEIVED.getCode());
        List<Order> orderList = orderRepository.findByClient_CodeAndStatusIn(clientCode, statusList);

        for (Order order : orderList) {
            if (order.getStatus() == OrderStatus.CREATED.getCode()) {
                order.setStatus(OrderStatus.RECEIVED.getCode());
                orderRepository.save(order);
            }
        }

        return orderList.stream()
                .map(OrderMapper::toDTO)
                .toList();
    }

    @Override
    public List<OrderDTO> getOrderListByUser(String userName) {
        User user = userRepository.findByUsername(userName);
        if (user == null) {
            return List.of();
        }


        List<Order> orderList = orderRepository.findByUser(user);
        return orderList.stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<OrderDTO> getOrderListByFilter(OrderFilterDTO filterDTO) {
        List<Order> orders = orderRepository.findAll(OrderSpecification.filter(filterDTO));
        return orders.stream()
                .map(OrderMapper::toDTO)
                .toList();
    }

    @Override
    public ClientDTO getClient(String clientCode) {
        Optional<Client> client = clientRepository.findByCode(clientCode);
        return client.map(ClientMapper::toDTO).orElse(null);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Integer registerUser(UserDTO userDTO) {
        User existingUser = userRepository.findByUsername(userDTO.getUsername());
        if (existingUser != null) {
            log.warn("User with username {} already exists", userDTO.getUsername());
            return null;
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setActive(true);
        user.setCreatedOn(LocalDateTime.now());

        Optional<Role> roleOpt = roleRepository.findByName(UserRole.USER.getCode());
        if (roleOpt.isEmpty()) {
            log.error("Default role USER not found");
            return null;
        }

        user.setRole(roleOpt.get());
        userRepository.save(user);
        return user.getId();
    }

    @Override
    public Boolean checkUserExist(String username) {
        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            log.warn("User with username {} exists", username);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateClient(ClientUpdateDTO dto) {
        if (dto.getClientCode() == null) {
            return false;
        }

        Optional<Client> client = clientRepository.findByCode(dto.getClientCode());
        if (client.isEmpty()) {
            log.error("Client with code {} not found", dto.getClientCode());
            return false;
        }

        Client existingClient = client.get();

        if (dto.getVipDayActive() != null) {
            existingClient.setVipDayActive(dto.getVipDayActive());
        }

        if (dto.getVipDayCode() != null) {
            existingClient.setVipDayCode(dto.getVipDayCode());
        }
        if (dto.getLocationSecret() != null) {
            existingClient.setLocationSecret(dto.getLocationSecret());
        }

        clientRepository.save(existingClient);
        return true;
    }

    @Override
    public void saveLogin(String username, String ip) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            Login login = new Login();
            login.setUser(user);
            login.setTime(LocalDateTime.now());
            login.setIp(ip);
            loginRepository.save(login);
        }
    }
}

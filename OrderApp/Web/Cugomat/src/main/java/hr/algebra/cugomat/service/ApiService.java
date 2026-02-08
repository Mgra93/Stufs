package hr.algebra.cugomat.service;

import hr.algebra.cugomat.dto.*;
import hr.algebra.cugomat.models.User;

import java.util.List;

public interface ApiService {
    List<CategoryDTO> getCategoryList(String clientCode);
    CategoryDTO getCategoryById(Integer id);
    Integer createCategory(CategoryCreateDTO dto);
    boolean updateCategory(CategoryUpdateDTO dto);
    boolean deleteCategory(Integer id);

    List<ProductDTO> getProductList(String clientCode, Integer categoryId);
    ProductDTO getProductById(Integer id);
    Integer createProduct(ProductCreateDTO dto);
    boolean updateProduct(ProductUpdateDTO dto);
    boolean deleteProduct(Integer id);

    WorkerDTO getWorker(String username);
    List<WorkerDTO> getWorkerList(String clientCode);
    Integer createWorker(WorkerCreateDTO dto);
    boolean updateWorker(WorkerUpdateDTO dto);
    boolean deleteWorker(Integer id);

    Integer createOrder(OrderCreateDTO orderDTO);
    Boolean setOrderStatus(ChangeOrderStatusDTO changeDTO);
    Boolean checkDiscount(String user, String clientCode);
    List<OrderDTO> getOrderListByUser(String username);
    List<OrderDTO> getOrderListActive(String clientCode);
    List<OrderDTO> getOrderListByFilter(OrderFilterDTO filterDTO);

    User getUserByUsername(String username);
    Integer registerUser(UserDTO userDTO);
    Boolean checkUserExist(String username);

    ClientDTO getClient(String clientCode);
    boolean updateClient(ClientUpdateDTO dto);

    void saveLogin(String username, String ip);
}

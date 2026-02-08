package hr.algebra.cugomat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.algebra.cugomat.dto.ProductCreateDTO;
import hr.algebra.cugomat.dto.ProductUpdateDTO;
import hr.algebra.cugomat.models.Category;
import hr.algebra.cugomat.models.Client;
import hr.algebra.cugomat.models.Product;
import hr.algebra.cugomat.repository.CategoryRepository;
import hr.algebra.cugomat.repository.ClientRepository;
import hr.algebra.cugomat.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Client testClient;
    private Category testCategory;
    private Product existingProduct;

    private final String URL_GET = "/api/product";
    private final String URL_LIST = "/api/product/list";
    private final String URL_CREATE = "/api/product/create";
    private final String URL_UPDATE = "/api/product/update";
    private final String URL_DELETE = "/api/product/delete";

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setName("Caffe Bar Sunset");
        testClient.setCode("CBS");
        testClient.setOib("12345678901");
        testClient.setActive(true);
        testClient.setCreatedOn(LocalDateTime.now());
        testClient.setLicenseExpiryTime(LocalDateTime.now().plusYears(1));
        testClient.setLocationSecret("mojbar123");
        testClient.setWebPageUrl("https://www.facebook.com/profile.php?id=61556095522110");
        testClient.setVipDayActive(true);
        testClient.setVipDayCode(1);
        testClient = clientRepository.save(testClient);

        testCategory = new Category();
        testCategory.setName("Topli napici");
        testCategory.setActive(true);
        testCategory.setCreatedOn(LocalDateTime.now());
        testCategory.setClient(testClient);
        testCategory = categoryRepository.save(testCategory);

        existingProduct = new Product();
        existingProduct.setName("Espresso");
        existingProduct.setPrice(new BigDecimal("1.50"));
        existingProduct.setActive(true);
        existingProduct.setCreatedOn(LocalDateTime.now());
        existingProduct.setClient(testClient);
        existingProduct.setCategory(testCategory);
        existingProduct = productRepository.save(existingProduct);
    }


    @Test
    void testGetProductById() throws Exception {
        mockMvc.perform(get(URL_GET)
                        .param("productId", existingProduct.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(existingProduct.getName()));
    }

    @Test
    void testGetProductList() throws Exception {
    mockMvc.perform(get(URL_LIST)
                        .param("clientCode", testClient.getCode()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(existingProduct.getName()));
    }
    @Test
    void testCreateProduct() throws Exception {
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setName("Topla čokolada");
        dto.setPrice(BigDecimal.valueOf(199.99));
        dto.setClientCode(testClient.getCode());
        dto.setCategoryId(testCategory.getId());

        mockMvc.perform(post(URL_CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    void testUpdateProduct() throws Exception {
        ProductUpdateDTO updateDTO = new ProductUpdateDTO();
        updateDTO.setId(existingProduct.getId());
        updateDTO.setName("Updated Product");
        updateDTO.setPrice(BigDecimal.valueOf(123.45));
        updateDTO.setCategoryId(testCategory.getId());

        mockMvc.perform(put(URL_UPDATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void testDeleteProduct() throws Exception {
        mockMvc.perform(delete(URL_DELETE)
                        .param("productId", existingProduct.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
        Product deletedProduct = productRepository.findById(existingProduct.getId()).orElseThrow();
        assert !deletedProduct.getActive();
    }
}

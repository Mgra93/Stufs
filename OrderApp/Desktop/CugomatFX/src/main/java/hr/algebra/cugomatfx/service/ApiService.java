package hr.algebra.cugomatfx.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hr.algebra.cugomatfx.CugomatFXApplication;
import hr.algebra.cugomatfx.dto.*;
import hr.algebra.cugomatfx.enums.OrderStatus;
import hr.algebra.cugomatfx.helpers.AppConfig;
import hr.algebra.cugomatfx.helpers.CertHelper;
import hr.algebra.cugomatfx.helpers.HeadersHelper;
import hr.algebra.cugomatfx.helpers.JwtHelper;
import hr.algebra.cugomatfx.mapper.*;
import hr.algebra.cugomatfx.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ApiService {
    private static ApiService instance;
    private static final String URL_BASE = AppConfig.get("service.url");
    private static final String URL_LOGIN = URL_BASE + "/api/auth/login";
    private static final String URL_REFRESH_TOKEN = URL_BASE + "/api/auth/refreshToken";

    private static final String URL_GET_WORKER = URL_BASE + "/api/worker";
    private static final String URL_GET_WORKERS_LIST = URL_BASE + "/api/worker/list";
    private static final String URL_CREATE_WORKER = URL_BASE + "/api/worker/create";
    private static final String URL_UPDATE_WORKER = URL_BASE + "/api/worker/update";
    private static final String URL_DELETE_WORKER = URL_BASE + "/api/worker/delete";

    private static final String URL_GET_ORDERS_ACTIVE = URL_BASE + "/api/order/active";
    private static final String URL_GET_ORDERS_BY_FILTER = URL_BASE + "/api/order/byFilter";
    private static final String URL_SET_ORDER_STATUS = URL_BASE + "/api/order/setStatus";

    private static final String URL_GET_CATEGORY_LIST = URL_BASE + "/api/category/list";
    private static final String URL_CREATE_CATEGORY = URL_BASE + "/api/category/create";
    private static final String URL_UPDATE_CATEGORY = URL_BASE + "/api/category/update";
    private static final String URL_DELETE_CATEGORY = URL_BASE + "/api/category/delete";

    private static final String URL_GET_PRODUCT_LIST = URL_BASE + "/api/product/list";
    private static final String URL_CREATE_PRODUCT = URL_BASE + "/api/product/create";
    private static final String URL_UPDATE_PRODUCT = URL_BASE + "/api/product/update";
    private static final String URL_DELETE_PRODUCT = URL_BASE + "/api/product/delete";

    private static final String URL_GET_CLIENT = URL_BASE + "/api/client";
    private static final String URL_UPDATE_CLIENT = URL_BASE + "/api/client/update";

    private static final String URL_CHECK_USER_EXIST = URL_BASE + "/api/user/check";
    private static final String URL_SERVER_PING = URL_BASE + "/api/auth/ping";

    private static final Integer HTTP_STATUS_OK = 200;
    private static final Integer HTTP_STATUS_FORBIDDEN = 403;
    private static final Integer HTTP_STATUS_NOT_FOUND = 404;

    private final HttpClient client;
    private final ObjectMapper mapper;
    private AccessData accessData;

    public void setAccessData(AccessData accessData) {
        this.accessData = accessData;
    }

    public String getRole() {
        return this.accessData.getRole();
    }

    private ApiService() {
        try {
            CertHelper certHelper = new CertHelper();
            this.client = certHelper.getClient();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize HttpClient", e);
        }

        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static synchronized ApiService getInstance() {
        if (instance == null) {
            instance = new ApiService();
        }
        return instance;
    }

    public void clearAccessData() {
        this.accessData = null;
    }

    private void refreshAccessToken() throws Exception {
        if (accessData == null || accessData.getRefreshToken() == null) {
            throw new RuntimeException("No refresh token available");
        }

        RefreshTokenRequestDTO refreshRequest = new RefreshTokenRequestDTO();
        refreshRequest.setToken(accessData.getRefreshToken());

        String jsonBody = mapper.writeValueAsString(refreshRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_REFRESH_TOKEN))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header(HeadersHelper.ACCEPT, HeadersHelper.APP_JSON)
                .header(HeadersHelper.CONTENT_TYPE, HeadersHelper.APP_JSON)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == HTTP_STATUS_OK) {
            JwtResponseDTO dto = mapper.readValue(response.body(), JwtResponseDTO.class);
            JwtResponse jwtResponse = JWTMapper.toEntity(dto);

            AccessData newAccessData = JwtHelper.extractAccessData(jwtResponse);
            newAccessData.setRefreshToken(accessData.getRefreshToken());
            this.setAccessData(newAccessData);
        } else {
            throw new RuntimeException("Token refresh error with status: " + response.statusCode());
        }
    }

    private HttpResponse<String> sendWithAutoRefresh(HttpRequest request) throws Exception {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == HTTP_STATUS_FORBIDDEN && accessData != null && accessData.getRefreshToken() != null) {
            try {
                refreshAccessToken();

                HttpRequest.Builder builder = HttpRequest.newBuilder()
                        .uri(request.uri())
                        .method(request.method(), request.bodyPublisher().orElse(HttpRequest.BodyPublishers.noBody()));

                request.headers().map().forEach((key, values) -> {
                    if (!key.equalsIgnoreCase("Authorization")) {
                        values.forEach(value -> builder.header(key, value));
                    }
                });

                builder.header(HeadersHelper.AUTHORIZATION, accessData.getBearerToken());
                response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            } catch (Exception e) {
                throw new RuntimeException("Token refresh error:", e);
            }
        }

        return response;
    }

    // LOGIN
    public JwtResponse postLogin(AuthRequestDTO authRequestDTO) throws Exception {
        String jsonBody = mapper.writeValueAsString(authRequestDTO);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_LOGIN))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header(HeadersHelper.ACCEPT, HeadersHelper.APP_JSON)
                .header(HeadersHelper.CONTENT_TYPE, HeadersHelper.APP_JSON)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HTTP_STATUS_OK) {
            throw new Exception("Get worker error. Status: " + response.statusCode());
        }

        JwtResponseDTO dto = mapper.readValue(response.body(), JwtResponseDTO.class);
        return JWTMapper.toEntity(dto);
    }

    // WORKER
    public Worker getWorker(String username) throws Exception {
        String endpoint = URL_GET_WORKER + "?username=" + URLEncoder.encode(username, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .GET()
                .header(HeadersHelper.ACCEPT, HeadersHelper.APP_JSON)
                .header(HeadersHelper.AUTHORIZATION, accessData.getBearerToken())
                .build();

        HttpResponse<String> response = sendWithAutoRefresh(request);

        if (response.statusCode() != HTTP_STATUS_OK) {
            throw new Exception("Get worker error. Status: " + response.statusCode());
        }

        WorkerDTO dto = mapper.readValue(response.body(), WorkerDTO.class);
        return WorkerMapper.toEntity(dto);
    }

    public List<Worker> getWorkerList() throws Exception {
        String endpoint = URL_GET_WORKERS_LIST + "?clientCode=" + URLEncoder.encode(
                CugomatFXApplication.getCurrentWorker().getClient().getCode(), StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .GET()
                .header(HeadersHelper.ACCEPT, HeadersHelper.APP_JSON)
                .header(HeadersHelper.AUTHORIZATION, accessData.getBearerToken())
                .build();

        HttpResponse<String> response = sendWithAutoRefresh(request);

        if (response.statusCode() == HTTP_STATUS_NOT_FOUND) {
            return new ArrayList<>();
        } else if (response.statusCode() != HTTP_STATUS_OK) {
            throw new Exception("Get worker list error. Status: " + response.statusCode());
        }

        List<WorkerDTO> workerList = mapper.readValue(response.body(), new TypeReference<>() {
        });

        return WorkerMapper.toEntityList(workerList);
    }

    public Integer createWorker(User worker) throws Exception {
        WorkerCreateDTO workerCreateDTO = new WorkerCreateDTO();
        workerCreateDTO.setWorker(UserMapper.toDTO(worker));
        workerCreateDTO.setClientCode(CugomatFXApplication.getCurrentWorker().getClient().getCode());

        String jsonBody = mapper.writeValueAsString(workerCreateDTO);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_CREATE_WORKER))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header(HeadersHelper.ACCEPT, HeadersHelper.APP_JSON)
                .header(HeadersHelper.CONTENT_TYPE, HeadersHelper.APP_JSON)
                .header(HeadersHelper.AUTHORIZATION, accessData.getBearerToken())
                .build();

        HttpResponse<String> response = sendWithAutoRefresh(request);

        if (response.statusCode() != HTTP_STATUS_OK) {
            throw new Exception("Create worker error. Status: " + response.statusCode());
        }

        return mapper.readValue(response.body(), Integer.class);
    }

    public boolean updateWorker(Worker worker, Boolean changePassword) throws Exception {
        WorkerUpdateDTO workerUpdateDTO = WorkerMapper.toUpdateDTO(worker);
        workerUpdateDTO.setChangePassword(changePassword);

        String jsonBody = mapper.writeValueAsString(workerUpdateDTO);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_UPDATE_WORKER))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header(HeadersHelper.ACCEPT, HeadersHelper.APP_JSON)
                .header(HeadersHelper.CONTENT_TYPE, HeadersHelper.APP_JSON)
                .header(HeadersHelper.AUTHORIZATION, accessData.getBearerToken())
                .build();

        HttpResponse<String> response = sendWithAutoRefresh(request);

        if (response.statusCode() != HTTP_STATUS_OK) {
            throw new Exception("Update worker error. Status: " + response.statusCode());
        }

        return mapper.readValue(response.body(), Boolean.class);
    }

    public boolean deleteWorker(Integer id) throws Exception {
        String endpoint = URL_DELETE_WORKER + "?workerId=" + id;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .DELETE()
                .header(HeadersHelper.ACCEPT, HeadersHelper.APP_JSON)
                .header(HeadersHelper.AUTHORIZATION, accessData.getBearerToken())
                .build();

        HttpResponse<String> response = sendWithAutoRefresh(request);

        if (response.statusCode() != HTTP_STATUS_OK) {
            throw new Exception("Delete worker error. Status: " + response.statusCode());
        }
        return mapper.readValue(response.body(), Boolean.class);
    }

    public List<Order> getActiveOrders() throws Exception {
        String endpoint = URL_GET_ORDERS_ACTIVE + "?clientCode=" + URLEncoder.encode(
                CugomatFXApplication.getCurrentWorker().getClient().getCode(), StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .GET()
                .header(HeadersHelper.ACCEPT, HeadersHelper.APP_JSON)
                .header(HeadersHelper.AUTHORIZATION, accessData.getBearerToken())
                .build();

        HttpResponse<String> response = sendWithAutoRefresh(request);

        if (response.statusCode() == HTTP_STATUS_NOT_FOUND) {
            return new ArrayList<>();
        } else if (response.statusCode() != HTTP_STATUS_OK) {
            throw new Exception("Get active orders error. Status: " + response.statusCode());
        }
        List<OrderDTO> dtoList = mapper.readValue(
                response.body(),
                mapper.getTypeFactory().constructCollectionType(List.class, OrderDTO.class)
        );
        return OrderMapper.toEntityList(dtoList);
    }

    public List<Order> getOrdersByFilter(OrderFilter orderFilter) throws Exception {
        OrderFilterDTO filterDTO = OrderFilterMapper.toDTO(orderFilter);
        filterDTO.setCompanyCode(CugomatFXApplication.getCurrentWorker().getClient().getCode());
        String jsonBody = mapper.writeValueAsString(filterDTO);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_GET_ORDERS_BY_FILTER))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header(HeadersHelper.ACCEPT, HeadersHelper.APP_JSON)
                .header(HeadersHelper.CONTENT_TYPE, HeadersHelper.APP_JSON)
                .header(HeadersHelper.AUTHORIZATION, accessData.getBearerToken())
                .build();

        HttpResponse<String> response = sendWithAutoRefresh(request);
        if (response.statusCode() == HTTP_STATUS_NOT_FOUND) {
            return new ArrayList<>();
        } else if (response.statusCode() != HTTP_STATUS_OK) {
            throw new Exception("Get order by filter error. Status: " + response.statusCode());
        }

        List<OrderDTO> orderList = mapper.readValue(response.body(), new TypeReference<>() {
        });
        return OrderMapper.toEntityList(orderList);
    }

    public Boolean setOrderStatus(Order order, OrderStatus status) throws Exception {
        ChangeOrderStatusDTO dto = new ChangeOrderStatusDTO();
        dto.setOrderId(order.getId());
        dto.setStatus(status.getCode());
        dto.setWorkerUsername(CugomatFXApplication.getCurrentWorker().getUser().getUsername());
        String jsonBody = mapper.writeValueAsString(dto);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_SET_ORDER_STATUS))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header(HeadersHelper.ACCEPT, HeadersHelper.APP_JSON)
                .header(HeadersHelper.CONTENT_TYPE, HeadersHelper.APP_JSON)
                .header(HeadersHelper.AUTHORIZATION, accessData.getBearerToken())
                .build();
        HttpResponse<String> response = sendWithAutoRefresh(request);

        if (response.statusCode() != HTTP_STATUS_OK) {
            throw new Exception("Update category error. Status: " + response.statusCode());
        }

        return Boolean.parseBoolean(response.body());
    }

    public List<Category> getCategoryList() throws Exception {
        String endpoint = URL_GET_CATEGORY_LIST + "?clientCode=" + URLEncoder.encode(
                CugomatFXApplication.getCurrentWorker().getClient().getCode(), StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .GET()
                .header(HeadersHelper.ACCEPT, HeadersHelper.APP_JSON)
                .header(HeadersHelper.AUTHORIZATION, accessData.getBearerToken())
                .build();

        HttpResponse<String> response = sendWithAutoRefresh(request);
        if (response.statusCode() == HTTP_STATUS_NOT_FOUND) {
            return new ArrayList<>();
        } else if (response.statusCode() != HTTP_STATUS_OK) {
            throw new Exception("Get category list error. Status: " + response.statusCode());
        }

        List<CategoryDTO> dtoList = mapper.readValue(
                response.body(),
                mapper.getTypeFactory().constructCollectionType(List.class, CategoryDTO.class)
        );
        return CategoryMapper.toEntityList(dtoList);
    }

    public Integer createCategory(Category category) throws Exception {
        CategoryCreateDTO categoryCreateDTO = CategoryMapper.toCreateDTO(category);
        categoryCreateDTO.setClientCode(CugomatFXApplication.getCurrentWorker().getClient().getCode());
        String jsonBody = mapper.writeValueAsString(categoryCreateDTO);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_CREATE_CATEGORY))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header(HeadersHelper.ACCEPT, HeadersHelper.APP_JSON)
                .header(HeadersHelper.CONTENT_TYPE, HeadersHelper.APP_JSON)
                .header(HeadersHelper.AUTHORIZATION, accessData.getBearerToken())
                .build();

        HttpResponse<String> response = sendWithAutoRefresh(request);

        if (response.statusCode() != HTTP_STATUS_OK) {
            throw new Exception("Create category error. Status: " + response.statusCode());
        }
        return mapper.readValue(response.body(), Integer.class);
    }

    public boolean updateCategory(Category category) throws Exception {
        CategoryUpdateDTO updateDTO = CategoryMapper.toUpdateDTO(category);
        String jsonBody = mapper.writeValueAsString(updateDTO);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_UPDATE_CATEGORY))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header(HeadersHelper.ACCEPT, HeadersHelper.APP_JSON)
                .header(HeadersHelper.CONTENT_TYPE, HeadersHelper.APP_JSON)
                .header(HeadersHelper.AUTHORIZATION, accessData.getBearerToken())
                .build();

        HttpResponse<String> response = sendWithAutoRefresh(request);

        if (response.statusCode() != HTTP_STATUS_OK) {
            throw new Exception("Update category error. Status: " + response.statusCode());
        }
        return mapper.readValue(response.body(), Boolean.class);
    }

    public Client getClient(String clientCode) throws Exception {
        String endpoint = URL_GET_CLIENT + "?code=" + URLEncoder.encode(
                clientCode, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .GET()
                .header(HeadersHelper.ACCEPT, HeadersHelper.APP_JSON)
                .header(HeadersHelper.AUTHORIZATION, accessData.getBearerToken())
                .build();

        HttpResponse<String> response = sendWithAutoRefresh(request);

        if (response.statusCode() != HTTP_STATUS_OK) {
            throw new Exception("Get client error. Status: " + response.statusCode());
        }

        ClientDTO dto = mapper.readValue(response.body(), ClientDTO.class);
        return ClientMapper.toEntity(dto);
    }


    public boolean updateClient(Client client) throws Exception {
        ClientUpdateDTO updateDTO = ClientMapper.toUpdateDTO(client);
        String jsonBody = mapper.writeValueAsString(updateDTO);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_UPDATE_CLIENT))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header(HeadersHelper.ACCEPT, HeadersHelper.APP_JSON)
                .header(HeadersHelper.CONTENT_TYPE, HeadersHelper.APP_JSON)
                .header(HeadersHelper.AUTHORIZATION, accessData.getBearerToken())
                .build();
        HttpResponse<String> response = sendWithAutoRefresh(request);

        if (response.statusCode() != HTTP_STATUS_OK) {
            throw new Exception("Update client error. Status: " + response.statusCode());
        }
        return mapper.readValue(response.body(), Boolean.class);
    }

    public boolean deleteCategory(Integer id) throws Exception {
        String endpoint = URL_DELETE_CATEGORY + "?categoryId=" + id;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .DELETE()
                .header(HeadersHelper.ACCEPT, HeadersHelper.APP_JSON)
                .header(HeadersHelper.AUTHORIZATION, accessData.getBearerToken())
                .build();

        HttpResponse<String> response = sendWithAutoRefresh(request);

        if (response.statusCode() != HTTP_STATUS_OK) {
            throw new Exception("Delete category error. Status: " + response.statusCode());
        }
        return mapper.readValue(response.body(), Boolean.class);
    }

    public List<Product> getProductList(Integer categoryId) throws Exception {
        String endpoint = URL_GET_PRODUCT_LIST + "?clientCode=" + URLEncoder.encode(
                CugomatFXApplication.getCurrentWorker().getClient().getCode(), StandardCharsets.UTF_8);

        if (categoryId != null) {
            endpoint += "&categoryId=" + categoryId;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .GET()
                .header(HeadersHelper.ACCEPT, HeadersHelper.APP_JSON)
                .header(HeadersHelper.AUTHORIZATION, accessData.getBearerToken())
                .build();

        HttpResponse<String> response = sendWithAutoRefresh(request);
        if (response.statusCode() == HTTP_STATUS_NOT_FOUND) {
            return new ArrayList<>();
        } else if (response.statusCode() != HTTP_STATUS_OK) {
            throw new Exception("Get product list error. Status: " + response.statusCode());
        }

        List<ProductDTO> dtoList = mapper.readValue(response.body(), new TypeReference<>() {
        });
        return ProductMapper.toEntityList(dtoList);
    }

    public Integer createProduct(Product product) throws Exception {
        ProductCreateDTO createDTO = ProductMapper.toCreateDto(product);
        createDTO.setClientCode(CugomatFXApplication.getCurrentWorker().getClient().getCode());
        String jsonBody = mapper.writeValueAsString(createDTO);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_CREATE_PRODUCT))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header(HeadersHelper.ACCEPT, HeadersHelper.APP_JSON)
                .header(HeadersHelper.CONTENT_TYPE, HeadersHelper.APP_JSON)
                .header(HeadersHelper.AUTHORIZATION, accessData.getBearerToken())
                .build();
        HttpResponse<String> response = sendWithAutoRefresh(request);

        if (response.statusCode() != HTTP_STATUS_OK) {
            throw new Exception("Create product error. Status: " + response.statusCode());
        }
        return mapper.readValue(response.body(), Integer.class);
    }

    public boolean updateProduct(Product product) throws Exception {
        ProductUpdateDTO updateDTO = ProductMapper.toUpdateDto(product);
        String jsonBody = mapper.writeValueAsString(updateDTO);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_UPDATE_PRODUCT))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header(HeadersHelper.ACCEPT, HeadersHelper.APP_JSON)
                .header(HeadersHelper.CONTENT_TYPE, HeadersHelper.APP_JSON)
                .header(HeadersHelper.AUTHORIZATION, accessData.getBearerToken())
                .build();

        HttpResponse<String> response = sendWithAutoRefresh(request);

        if (response.statusCode() != HTTP_STATUS_OK) {
            throw new Exception("Update product error. Status: " + response.statusCode());
        }

        return mapper.readValue(response.body(), Boolean.class);
    }

    public boolean deleteProduct(Integer id) throws Exception {
        String endpoint = URL_DELETE_PRODUCT + "?productId=" + id;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .DELETE()
                .header(HeadersHelper.AUTHORIZATION, accessData.getBearerToken())
                .build();

        HttpResponse<String> response = sendWithAutoRefresh(request);

        if (response.statusCode() != HTTP_STATUS_OK) {
            throw new Exception("Delete product error. Status: " + response.statusCode());
        }

        return mapper.readValue(response.body(), Boolean.class);
    }

    public boolean checkUserExist(String username) throws Exception {
        String endpoint = URL_CHECK_USER_EXIST + "?username=" +
                URLEncoder.encode(username, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .GET()
                .header(HeadersHelper.ACCEPT, HeadersHelper.APP_JSON)
                .header(HeadersHelper.AUTHORIZATION, accessData.getBearerToken())
                .build();
        HttpResponse<String> response = sendWithAutoRefresh(request);

        if (response.statusCode() != HTTP_STATUS_OK) {
            throw new Exception("Check user exist error. Status: " + response.statusCode());
        }
        return mapper.readValue(response.body(), Boolean.class);
    }

    public Boolean pingServer() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_SERVER_PING))
                .GET()
                .header(HeadersHelper.ACCEPT, HeadersHelper.APP_JSON)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == HTTP_STATUS_OK;
    }
}
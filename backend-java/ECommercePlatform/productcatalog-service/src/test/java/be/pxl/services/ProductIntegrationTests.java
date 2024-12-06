package be.pxl.services;

import be.pxl.services.audit.AuditLogService;
import be.pxl.services.domain.Category;
import be.pxl.services.domain.Label;
import be.pxl.services.domain.LabelColor;
import be.pxl.services.domain.Product;
import be.pxl.services.domain.dto.ProductRequest;
import be.pxl.services.repository.LabelRepository;
import be.pxl.services.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static be.pxl.services.RandomGenerator.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ProductCatalogServiceApplication.class)
@TestPropertySource(properties = {"image.storage.directory=/test/path/to/storage"})
@Testcontainers
@AutoConfigureMockMvc
public class ProductIntegrationTests {

    // Apple Silicon: ...
    // execute in terminal: sudo ln -s $HOME/.docker/run/docker.sock /var/run/docker.sock
    @Container
    private static final MySQLContainer<?> sqlContainer = new MySQLContainer<>("mysql:8.0").withCommand("--default" +
            "-authentication-plugin=mysql_native_password");

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private LabelRepository labelRepository;
    @MockBean
    private AuditLogService auditLogService;

    private Product product;
    private ProductRequest productRequest;
    private List<Label> labels;

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlContainer::getUsername);
        registry.add("spring.datasource.password", sqlContainer::getPassword);
        registry.add("spring.datasource.driverClassName", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.MySQL8Dialect");
        registry.add("eureka.client.enabled", () -> "false");
    }

    static Stream<ProductRequest> invalidProductRequests() {
        return Stream.of(
                // Missing name
                ProductRequest.builder().description(randomString()).price(randomDouble()).category(randomFromEnum(Category.class)).build(),
                // Missing description
                ProductRequest.builder().name(randomString()).price(randomDouble()).category(randomFromEnum(Category.class)).build(),
                // Missing price (NotNull)
                ProductRequest.builder().name(randomString()).description(randomString()).category(randomFromEnum(Category.class)).build(),
                // Negative price (Positive)
                ProductRequest.builder().name(randomString()).description(randomString()).price(randomDouble(-100,
                        -0.01)).category(randomFromEnum(Category.class)).build(),
                // Missing category
                ProductRequest.builder().name(randomString()).description(randomString()).price(randomDouble()).build(),
                // All fields missing
                ProductRequest.builder().build()
        );
    }

    @BeforeEach
    public void init() {
        productRepository.deleteAll();
        labelRepository.deleteAll();

        product = Product.builder()
                .name(randomString())
                .description(randomString())
                .price(randomInt(100))
                .category(randomFromEnum(Category.class))
                .build();

        labels = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            labels.add(Label.builder().name(randomString()).color(randomFromEnum(LabelColor.class)).build());
        }

        productRequest =
                ProductRequest.builder()
                        .name(randomString())
                        .description(randomString())
                        .price(randomInt(100))
                        .labelIds(randomIntList(randomInt(0, 3), 1, labels.size()))
                        .category(randomFromEnum(Category.class)).build();
    }

    @Test
    public void testCreateProduct() throws Exception {
        labelRepository.saveAll(labels);

        String productString = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/productcatalog")
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productString))
                .andExpect(status().isCreated());
        Assertions.assertEquals(1, productRepository.count());

        Long productId = productRepository.findAll().get(0).getId();
        product = productRepository.findById(productId).get();
        Assertions.assertEquals(
                productRequest.getLabelIds(),
                product.getLabels().stream().map(Label::getId).sorted().toList());

        verify(auditLogService).sendAuditLog(anyLong(), anyString(), anyString());
    }

    @ParameterizedTest
    @MethodSource("invalidProductRequests")
    public void testCreateProductThrowsExceptionWhenInvalidData(ProductRequest productRequest) throws Exception {
        String productString = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/productcatalog")
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productString))
                .andExpect(status().isBadRequest());

        Assertions.assertEquals(0, productRepository.count());

        verifyNoInteractions(auditLogService);
    }

    @Test
    public void testGetAllProducts() throws Exception {
        productRepository.save(product);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/productcatalog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void testGetProductById() throws Exception {
        productRepository.save(product);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/productcatalog/{id}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(product.getName()));
    }

    @Test
    public void testGetProductByIdThrowsExceptionWhenProductNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/productcatalog/{id}", 1000))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateProduct() throws Exception {
        productRepository.save(product);

        productRequest.setName(randomString());
        productRequest.setDescription(randomString());
        productRequest.setPrice(randomInt(100));
        productRequest.setCategory(randomFromEnum(Category.class));
        productRequest.setLabelIds(new ArrayList<>());

        String productString = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/productcatalog/{id}", product.getId())
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productString))
                .andExpect(status().isAccepted());

        Assertions.assertEquals(1, productRepository.count());
        Assertions.assertEquals(productRequest.getName(), productRepository.findById(product.getId()).get().getName());
        Assertions.assertEquals(productRequest.getDescription(),
                productRepository.findById(product.getId()).get().getDescription());

        verify(auditLogService).sendAuditLog(anyLong(), anyString(), anyString());
    }

    @Test
    public void testUpdateProductThrowsExceptionWhenProductNotFound() throws Exception {
        productRequest.setName(randomString());
        productRequest.setDescription(randomString());
        productRequest.setPrice(randomInt(100));
        productRequest.setCategory(randomFromEnum(Category.class));

        String productString = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/productcatalog/{id}", 1000)
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productString))
                .andExpect(status().isNotFound());

        verifyNoInteractions(auditLogService);
    }

    @ParameterizedTest
    @MethodSource("invalidProductRequests")
    public void testUpdateProductThrowsExceptionWhenInvalidData(ProductRequest productRequest) throws Exception {
        productRepository.save(product);

        String productString = objectMapper.writeValueAsString(productRequest);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/productcatalog/{id}", product.getId())
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productString))
                .andExpect(status().isBadRequest());

        Assertions.assertEquals(1, productRepository.count());
        Assertions.assertEquals(product.getName(), productRepository.findById(product.getId()).get().getName());
        Assertions.assertEquals(product.getDescription(),
                productRepository.findById(product.getId()).get().getDescription());

        verifyNoInteractions(auditLogService);
    }

    @Test
    public void testDeleteProduct() throws Exception {
        productRepository.save(product);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/productcatalog/delete/{id}", product.getId())
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isNoContent());

        Assertions.assertEquals(0, productRepository.count());
        Assertions.assertFalse(productRepository.findById(product.getId()).isPresent());
    }
}

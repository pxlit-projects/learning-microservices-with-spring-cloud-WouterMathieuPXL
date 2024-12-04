package be.pxl.services;

import be.pxl.services.client.ProductCatalogClient;
import be.pxl.services.domain.*;
import be.pxl.services.repository.CustomerOrderRepository;
import be.pxl.services.repository.ShoppingCartRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;

import static be.pxl.services.RandomGenerator.randomInt;
import static be.pxl.services.RandomGenerator.randomString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShoppingCartApplication.class)
@Testcontainers
@AutoConfigureMockMvc
public class OrderIntegrationTests {

    // Apple Silicon: ...
    // execute in terminal: sudo ln -s $HOME/.docker/run/docker.sock /var/run/docker.sock
    @Container
    private static final MySQLContainer<?> sqlContainer = new MySQLContainer<>("mysql:8.0").withCommand("--default" +
            "-authentication-plugin=mysql_native_password");
    ShoppingCart shoppingCart;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private CustomerOrderRepository customerOrderRepository;
    @MockBean
    private ProductCatalogClient productCatalogClient;

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

    @BeforeEach
    public void init() {
        shoppingCartRepository.deleteAll();
        customerOrderRepository.deleteAll();

        Product product = Product.builder()
                .id((long) 1)
                .name(randomString())
                .price(randomInt(999))
                .build();
        given(productCatalogClient.getProductById(anyLong())).willReturn(product);

        shoppingCart = ShoppingCart.builder()
                .shoppingCartItems(new ArrayList<>())
                .build();
        shoppingCartRepository.save(shoppingCart);

        for (int i = 0; i < randomInt(10); i++) {
            ShoppingCartItem shoppingCartItem = ShoppingCartItem.builder()
                    .shoppingCart(shoppingCart)
                    .productId((long) i)
                    .quantity(randomInt(10))
                    .build();
            shoppingCart.addShoppingCartItem(shoppingCartItem);
        }
        shoppingCartRepository.save(shoppingCart);
    }

    @Test
    void shouldCheckoutShoppingCart() throws Exception {
        mockMvc.perform(post("/api/shoppingcart/{id}/checkout", shoppingCart.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.orderItems.length()", is(shoppingCart.getShoppingCartItems().size())));

        CustomerOrder createdOrder = customerOrderRepository.findByShoppingCartId(shoppingCart.getId())
                .orElseThrow(() -> new AssertionError("Order not found"));

        for (OrderItem item : createdOrder.getOrderItems()) {
            assertNotNull(item.getId(), "OrderItem ID should have been generated");
            assertEquals(createdOrder, item.getOrder(), "Each OrderItem should have the correct Order associated");
        }
    }
}

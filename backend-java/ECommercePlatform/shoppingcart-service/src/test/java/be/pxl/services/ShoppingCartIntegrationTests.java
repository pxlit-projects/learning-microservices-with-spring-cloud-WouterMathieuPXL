package be.pxl.services;

import be.pxl.services.client.ProductCatalogClient;
import be.pxl.services.domain.Product;
import be.pxl.services.domain.ShoppingCart;
import be.pxl.services.domain.ShoppingCartItem;
import be.pxl.services.repository.ShoppingCartRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShoppingCartApplication.class)
@Testcontainers
@AutoConfigureMockMvc
public class ShoppingCartIntegrationTests {

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
    private ShoppingCartRepository shoppingCartRepository;
    @MockBean
    private ProductCatalogClient productCatalogClient;
    private ShoppingCart shoppingCart;

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

        for (int i = 0; i < 10; i++) {
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
    public void testGetShoppingCart() throws Exception {
        mockMvc.perform(get("/api/shoppingcart/{id}", shoppingCart.getId())
                        .header("X-User-Role", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shoppingCartItems.length()", is(shoppingCart.getShoppingCartItems().size())));
    }
    @Test
    public void testGetShoppingCartWhenNotAuthenticatedShouldThrowForbidden() throws Exception {
        mockMvc.perform(get("/api/shoppingcart/{id}", shoppingCart.getId())
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/shoppingcart/{id}", shoppingCart.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetNonExistingShoppingCart() throws Exception {
        mockMvc.perform(get("/api/shoppingcart/{id}", randomInt(9999))
                        .header("X-User-Role", "USER"))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 3})
    public void testEditProductInShoppingCartWhenProductIsAlreadyInCart(int quantity) throws Exception {
        mockMvc.perform(put("/api/shoppingcart/{shoppingCartId}/products/{productId}?quantity={quantity}",
                        shoppingCart.getId(), 0, quantity)
                        .header("X-User-Role", "USER"))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 3})
    public void testEditProductInShoppingCartWhenProductIsNotInCart(int quantity) throws Exception {
        shoppingCart.getShoppingCartItems().clear();

        mockMvc.perform(put("/api/shoppingcart/{shoppingCartId}/products/{productId}?quantity={quantity}",
                        shoppingCart.getId(), randomInt(9999), quantity)
                        .header("X-User-Role", "USER"))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 3})
    public void testEditProductInShoppingCartWhenProductDoesNotExist(int quantity) throws Exception {
        given(productCatalogClient.getProductById(anyLong())).willThrow(new RuntimeException("Product not found"));
        mockMvc.perform(put("/api/shoppingcart/{shoppingCartId}/products/{productId}?quantity={quantity}",
                        shoppingCart.getId(), randomInt(9999), quantity)
                        .header("X-User-Role", "USER"))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 30})
    public void testEditProductInShoppingCartWhenProductDoesNotExist2(long productId) throws Exception {
        given(productCatalogClient.getProductById(2L)).willReturn(null);
        mockMvc.perform(put("/api/shoppingcart/{shoppingCartId}/products/{productId}?quantity={quantity}",
                        shoppingCart.getId(), productId, randomInt(10))
                        .header("X-User-Role", "USER"))
                .andExpect(status().isOk());
    }

    @Test
    public void testEditProductInShoppingCartWhenNotAuthenticatedShouldThrowForbidden() throws Exception {
        mockMvc.perform(put("/api/shoppingcart/{shoppingCartId}/products/{productId}?quantity={quantity}",
                        shoppingCart.getId(), randomInt(999), randomInt(10))
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/shoppingcart/{shoppingCartId}/products/{productId}?quantity={quantity}",
                        shoppingCart.getId(), randomInt(999), randomInt(10)))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 30})
    public void testRemoveProductInShoppingCart(long productId) throws Exception {
        mockMvc.perform(put("/api/shoppingcart/{shoppingCartId}/products/{productId}?quantity={quantity}",
                        shoppingCart.getId(), productId, 0)
                        .header("X-User-Role", "USER"))
                .andExpect(status().isOk());
    }

    @Test
    public void testRemoveProductInShoppingCartWhenNotAuthenticatedShouldThrowForbidden() throws Exception {
        mockMvc.perform(put("/api/shoppingcart/{shoppingCartId}/products/{productId}?quantity={quantity}",
                        shoppingCart.getId(), randomInt(999), randomInt(10))
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/shoppingcart/{shoppingCartId}/products/{productId}?quantity={quantity}",
                shoppingCart.getId(), randomInt(999), randomInt(10)))
                .andExpect(status().isForbidden());
    }
}

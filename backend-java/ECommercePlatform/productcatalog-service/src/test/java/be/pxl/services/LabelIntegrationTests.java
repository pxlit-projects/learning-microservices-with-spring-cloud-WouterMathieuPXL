package be.pxl.services;

import be.pxl.services.domain.Label;
import be.pxl.services.domain.LabelColor;
import be.pxl.services.domain.dto.LabelRequest;
import be.pxl.services.repository.LabelRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.stream.Stream;

import static be.pxl.services.RandomGenerator.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ProductCatalogServiceApplication.class)
@TestPropertySource(properties = {"image.storage.directory=/test/path/to/storage"})
@Testcontainers
@AutoConfigureMockMvc
public class LabelIntegrationTests {

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
    private LabelRepository labelRepository;

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

    static Stream<LabelRequest> invalidLabelRequests() {
        return Stream.of(
                LabelRequest.builder().color(randomFromEnum(LabelColor.class)).build(),
                LabelRequest.builder().name("").color(randomFromEnum(LabelColor.class)).build(),
                LabelRequest.builder().name(randomString()).build()
        );
    }

    @BeforeEach
    public void init() {
        labelRepository.deleteAll();
    }

    @Test
    public void testGetAllLabels() throws Exception {
        Label label1 = Label.builder()
                .name(randomString())
                .color(randomFromEnum(LabelColor.class))
                .build();
        Label label2 = Label.builder()
                .name(randomString())
                .color(randomFromEnum(LabelColor.class))
                .build();
        labelRepository.save(label1);
        labelRepository.save(label2);

        mockMvc.perform(get("/api/productcatalog/labels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(label1.getName()))
                .andExpect(jsonPath("$[1].name").value(label2.getName()));
    }

    @Test
    public void testCreateLabel() throws Exception {
        LabelRequest labelRequest = LabelRequest.builder()
                .name(randomString())
                .color(randomFromEnum(LabelColor.class))
                .build();

        mockMvc.perform(post("/api/productcatalog/labels")
                        .header("X-User-Role", "ADMIN")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(labelRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(labelRequest.getName()));
    }

    @ParameterizedTest
    @MethodSource("invalidLabelRequests")
    public void testCreateLabelWithInvalidRequests(LabelRequest invalidLabelRequest) throws Exception {
        mockMvc.perform(post("/api/productcatalog/labels")
                        .header("X-User-Role", "ADMIN")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidLabelRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateLabelWithoutAdminRightsReturnsForbidden() throws Exception {
        LabelRequest labelRequest = LabelRequest.builder()
                .name(randomString())
                .color(randomFromEnum(LabelColor.class))
                .build();

        mockMvc.perform(post("/api/productcatalog/labels")
                        .header("X-User-Role", "USER")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(labelRequest)))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/productcatalog/labels")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(labelRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testUpdateLabel() throws Exception {
        Label label = Label.builder()
                .name(randomString())
                .color(randomFromEnum(LabelColor.class))
                .build();
        labelRepository.save(label);

        LabelRequest labelRequest = LabelRequest.builder()
                .name(randomString())
                .color(randomFromEnum(LabelColor.class))
                .build();

        mockMvc.perform(put("/api/productcatalog/labels/{id}", label.getId())
                        .header("X-User-Role", "ADMIN")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(labelRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(labelRequest.getName()));
    }

    @Test
    public void testUpdateLabelWithoutAdminRightsReturnsForbidden() throws Exception {
        LabelRequest labelRequest = LabelRequest.builder()
                .name(randomString())
                .color(randomFromEnum(LabelColor.class))
                .build();

        mockMvc.perform(put("/api/productcatalog/labels/{id}", randomInt(999))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(labelRequest)))
                .andExpect(status().isForbidden());


        mockMvc.perform(put("/api/productcatalog/labels/{id}", randomInt(999))
                        .header("X-User-Role", "USER")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(labelRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteLabel() throws Exception {
        Label label = Label.builder()
                .name(randomString())
                .color(randomFromEnum(LabelColor.class))
                .build();
        labelRepository.save(label);

        mockMvc.perform(delete("/api/productcatalog/labels/{id}", label.getId())
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isNoContent());

        Assertions.assertEquals(0, labelRepository.count());
    }

    @Test
    public void testDeleteLabelWithoutAdminRightsReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/api/productcatalog/labels/{id}", randomInt(999))
                        .header("X-User-Role", "USER"))
                .andExpect(status().isForbidden());


        mockMvc.perform(delete("/api/productcatalog/labels/{id}", randomInt(999)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteNonExistentLabel() throws Exception {
        mockMvc.perform(delete("/api/productcatalog/labels/{id}", 1)
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllLabelColors() throws Exception {
        mockMvc.perform(get("/api/productcatalog/labels/colors"))
                .andExpect(status().isOk());
    }
}

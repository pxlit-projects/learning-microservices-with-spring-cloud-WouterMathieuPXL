package be.pxl.services;

import be.pxl.services.audit.AuditLog;
import be.pxl.services.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static be.pxl.services.RandomGenerator.randomString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = LogBookServiceApplication.class)
@Testcontainers
@AutoConfigureMockMvc
public class LogBookIntegrationTests {

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
    private AuditLogRepository auditLogRepository;

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
        auditLogRepository.deleteAll();
    }

    @Test
    public void testGetAuditLogs() throws Exception {
        AuditLog label1 = AuditLog.builder()
                .action(randomString())
                .performedBy(randomString())
                .timestamp(LocalDateTime.now())
                .build();
        AuditLog label2 = AuditLog.builder()
                .action(randomString())
                .performedBy(randomString())
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(label1);
        auditLogRepository.save(label2);

        mockMvc.perform(get("/api/logbook")
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(label1.getId()))
                .andExpect(jsonPath("$[1].id").value(label2.getId()));
    }

    @Test
    public void testGetAuditLogsWhenNotAuthenticatedShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/logbook")
                        .header("X-User-Role", "USER"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/logbook"))
                .andExpect(status().isForbidden());
    }
}

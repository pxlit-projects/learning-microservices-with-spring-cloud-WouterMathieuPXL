package be.pxl.services;

import be.pxl.services.audit.AuditLog;
import be.pxl.services.audit.AuditLogRequest;
import be.pxl.services.repository.AuditLogRepository;
import be.pxl.services.services.QueueService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static be.pxl.services.RandomGenerator.randomInt;
import static be.pxl.services.RandomGenerator.randomString;

@SpringBootTest(classes = LogBookServiceApplication.class)
@Testcontainers
public class QueueServiceIntegrationTests {

    // Apple Silicon: ...
    // execute in terminal: sudo ln -s $HOME/.docker/run/docker.sock /var/run/docker.sock
    @Container
    private static final MySQLContainer<?> sqlContainer = new MySQLContainer<>("mysql:8.0").withCommand("--default" +
            "-authentication-plugin=mysql_native_password");

    @Autowired
    private QueueService queueService;
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
    public void receiveAuditLogShouldSaveToRepository() {
        AuditLogRequest auditLogRequest = AuditLogRequest.builder()
                .productId((long) randomInt(999))
                .action(randomString())
                .performedBy(randomString())
                .timestamp(LocalDateTime.now())
                .build();

        queueService.listen(auditLogRequest);

        Assertions.assertEquals(1, auditLogRepository.count());
    }
}

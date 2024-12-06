package be.pxl.services;

import be.pxl.services.audit.AuditLogRequest;
import be.pxl.services.audit.AuditLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static be.pxl.services.RandomGenerator.randomInt;
import static be.pxl.services.RandomGenerator.randomString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuditLogServiceTests {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Captor
    private ArgumentCaptor<AuditLogRequest> auditLogRequestCaptor;

    @InjectMocks
    private AuditLogService auditLogService;

    @Test
    public void testSendAuditLog() {
        Long productId = (long) randomInt(9999);
        String action = randomString();
        String performedBy =  randomString();

        auditLogService.sendAuditLog(productId, action, performedBy);

        verify(rabbitTemplate).convertAndSend(eq("auditQueue"), auditLogRequestCaptor.capture());

        AuditLogRequest capturedAuditLogRequest = auditLogRequestCaptor.getValue();
        assertEquals(productId, capturedAuditLogRequest.getProductId());
        assertEquals(action, capturedAuditLogRequest.getAction());
        assertEquals(performedBy, capturedAuditLogRequest.getPerformedBy());
        assertNotNull(capturedAuditLogRequest.getTimestamp());
    }
}

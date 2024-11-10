package be.pxl.services.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final RabbitTemplate rabbitTemplate;

    public void sendAuditLog(Long productId, String action, String performedBy) {
        AuditLogRequest auditLog = AuditLogRequest.builder()
                .productId(productId)
                .action(action)
                .performedBy(performedBy)
                .timestamp(LocalDateTime.now())
                .build();
        rabbitTemplate.convertAndSend("auditQueue", auditLog);
    }
}

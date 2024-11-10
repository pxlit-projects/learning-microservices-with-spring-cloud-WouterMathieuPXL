package be.pxl.services.services;

import be.pxl.services.audit.AuditLogRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final AuditLogService auditLogService;

    @RabbitListener(queues = "auditQueue")
    public void listen(AuditLogRequest auditLog) {
        auditLogService.log(auditLog.getProductId(), auditLog.getAction(), auditLog.getPerformedBy(), auditLog.getTimestamp());
    }
}

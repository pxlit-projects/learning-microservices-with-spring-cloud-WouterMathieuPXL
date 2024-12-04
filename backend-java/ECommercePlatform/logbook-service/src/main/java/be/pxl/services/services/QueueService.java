package be.pxl.services.services;

import be.pxl.services.audit.AuditLogRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueService {

    private static final Logger logger = LoggerFactory.getLogger(QueueService.class);
    private final AuditLogService auditLogService;

    @RabbitListener(queues = "auditQueue")
    public void listen(AuditLogRequest auditLogRequest) {
        logger.info("Received message from auditQueue: {}", auditLogRequest);
        auditLogService.log(
                auditLogRequest.getProductId(),
                auditLogRequest.getAction(),
                auditLogRequest.getPerformedBy(),
                auditLogRequest.getTimestamp());
        logger.info("Audit log processed successfully");
    }
}

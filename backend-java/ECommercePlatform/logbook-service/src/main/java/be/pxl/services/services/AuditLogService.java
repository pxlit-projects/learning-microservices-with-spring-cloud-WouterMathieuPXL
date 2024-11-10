package be.pxl.services.services;


import be.pxl.services.audit.AuditLog;
import be.pxl.services.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(Long productId, String action, String performedBy, LocalDateTime timestamp) {
        AuditLog auditLog = new AuditLog();
        auditLog.setProductId(productId);
        auditLog.setAction(action);
        auditLog.setPerformedBy(performedBy);
        auditLog.setTimestamp(timestamp);
        auditLogRepository.save(auditLog);
    }
}

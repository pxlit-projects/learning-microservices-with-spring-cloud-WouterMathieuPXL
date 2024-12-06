package be.pxl.services.services;

import be.pxl.services.audit.AuditLog;
import be.pxl.services.audit.AuditLogResponse;
import be.pxl.services.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService implements IAuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(Long productId, String action, String performedBy, LocalDateTime timestamp) {
        AuditLog auditLog = new AuditLog();
        auditLog.setProductId(productId);
        auditLog.setAction(action);
        auditLog.setPerformedBy(performedBy);
        auditLog.setTimestamp(timestamp);
        auditLogRepository.save(auditLog);
    }

    @Override
    public List<AuditLogResponse> getAllAuditLogs() {
        return auditLogRepository.findAll().stream().map(this::mapAuditLogToAuditLogReponse).toList();
    }

    private AuditLogResponse mapAuditLogToAuditLogReponse(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .productId(auditLog.getProductId())
                .action(auditLog.getAction())
                .performedBy(auditLog.getPerformedBy())
                .timestamp(auditLog.getTimestamp())
                .build();
    }
}

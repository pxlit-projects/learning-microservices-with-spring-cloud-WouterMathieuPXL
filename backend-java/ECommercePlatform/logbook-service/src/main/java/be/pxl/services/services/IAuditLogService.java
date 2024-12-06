package be.pxl.services.services;

import be.pxl.services.audit.AuditLogResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface IAuditLogService {

    void log(Long productId, String action, String performedBy, LocalDateTime timestamp);

    List<AuditLogResponse> getAllAuditLogs();
}

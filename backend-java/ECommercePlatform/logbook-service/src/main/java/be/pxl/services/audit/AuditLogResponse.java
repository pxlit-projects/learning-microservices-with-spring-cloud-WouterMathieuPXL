package be.pxl.services.audit;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AuditLogResponse {

    private Long id;
    private Long productId;
    private String action;
    private String performedBy;
    private LocalDateTime timestamp;
}

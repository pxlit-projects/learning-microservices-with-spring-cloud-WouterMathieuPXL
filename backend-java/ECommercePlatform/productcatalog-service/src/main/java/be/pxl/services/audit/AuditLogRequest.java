package be.pxl.services.audit;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Builder
public class AuditLogRequest implements Serializable {

    private Long productId;
    private String action;
    private String performedBy;
    private LocalDateTime timestamp;
}

package be.pxl.services.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogRequest implements Serializable {

    private Long productId;
    private String action;
    private String performedBy;
    private LocalDateTime timestamp;
}

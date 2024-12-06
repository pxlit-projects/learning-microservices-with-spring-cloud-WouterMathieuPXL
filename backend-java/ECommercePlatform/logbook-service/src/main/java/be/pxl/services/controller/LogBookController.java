package be.pxl.services.controller;

import be.pxl.services.audit.AuditLogResponse;
import be.pxl.services.exceptions.ForbiddenException;
import be.pxl.services.services.IAuditLogService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logbook")
@RequiredArgsConstructor
public class LogBookController {

    private static final Logger log = LoggerFactory.getLogger(LogBookController.class);

    private final IAuditLogService auditLogService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AuditLogResponse> getAuditLogs(
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (role == null || !role.equals("ADMIN")) {
            log.warn("Unauthorized attempt to get access to audit logs by user with role: {}", role);
            throw new ForbiddenException("You do not have access to this resource.");
        }

        log.info("Received request to fetch all audit logs");
        return auditLogService.getAllAuditLogs();
    }
}

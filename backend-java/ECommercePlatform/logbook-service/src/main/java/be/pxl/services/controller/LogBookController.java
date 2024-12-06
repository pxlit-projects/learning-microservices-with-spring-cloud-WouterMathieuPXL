package be.pxl.services.controller;

import be.pxl.services.audit.AuditLogResponse;
import be.pxl.services.services.IAuditLogService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logbook")
@RequiredArgsConstructor
public class LogBookController {

    private static final Logger log = LoggerFactory.getLogger(LogBookController.class);

    private final IAuditLogService auditLogService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AuditLogResponse> getAuditLogs() {
        log.info("Received request to fetch all audit logs");
        return auditLogService.getAllAuditLogs();
    }
}

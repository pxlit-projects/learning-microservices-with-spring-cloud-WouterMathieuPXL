package be.pxl.services.controller;

import be.pxl.services.domain.dto.LabelRequest;
import be.pxl.services.domain.dto.LabelResponse;
import be.pxl.services.exceptions.ForbiddenException;
import be.pxl.services.services.ILabelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productcatalog/labels")
@RequiredArgsConstructor
public class LabelController {

    private static final Logger log = LoggerFactory.getLogger(LabelController.class);

    private final ILabelService labelService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<LabelResponse> getAllLabels() {
        log.info("Received request to fetch all labels");
        return labelService.getAllLabels();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponse createLabel(
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestBody @Valid LabelRequest labelRequest) {
        if (role == null || !role.equals("ADMIN")) {
            log.warn("Unauthorized attempt to create a label by user with role: {}", role);
            throw new ForbiddenException("You do not have access to this resource.");
        }

        log.info("Received request to create a new label");
        return labelService.createLabel(labelRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LabelResponse updateLabel(
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @PathVariable Long id,
            @RequestBody @Valid LabelRequest labelRequest) {
        if (role == null || !role.equals("ADMIN")) {
            log.warn("Unauthorized attempt to edit the label with ID {} by user with role: {}", id, role);
            throw new ForbiddenException("You do not have access to this resource.");
        }

        log.info("Received request to update label with ID: {}", id);
        return labelService.updateLabel(id, labelRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLabel(
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @PathVariable Long id) {
        if (role == null || !role.equals("ADMIN")) {
            log.warn("Unauthorized attempt to delete the label with ID {} by user with role: {}", id, role);
            throw new ForbiddenException("You do not have access to this resource.");
        }

        log.info("Received request to delete label with ID: {}", id);
        labelService.deleteLabel(id);
    }

    @GetMapping("/colors")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getLabelColors() {
        log.info("Received request to fetch label colors");
        return labelService.getLabelColors();
    }
}

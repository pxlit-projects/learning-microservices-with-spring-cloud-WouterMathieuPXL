package be.pxl.services.controller;

import be.pxl.services.domain.dto.LabelRequest;
import be.pxl.services.domain.dto.LabelResponse;
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

//    @GetMapping("/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public LabelResponse getLabelById(@PathVariable Long id) {
//        log.info("Received request to fetch label with ID: {}", id);
//        return labelService.getLabelById(id);
//    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponse createLabel(@RequestBody @Valid LabelRequest labelRequest) {
        log.info("Received request to create a new label");
        return labelService.createLabel(labelRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LabelResponse updateLabel(@PathVariable Long id, @RequestBody @Valid LabelRequest labelRequest) {
        log.info("Received request to update label with ID: {}", id);
        return labelService.updateLabel(id, labelRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLabel(@PathVariable Long id) {
        log.info("Received request to delete label with ID: {}", id);
        labelService.deleteLabel(id);
    }
}

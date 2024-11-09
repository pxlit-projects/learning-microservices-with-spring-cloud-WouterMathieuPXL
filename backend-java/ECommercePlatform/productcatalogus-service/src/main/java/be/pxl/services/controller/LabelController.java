package be.pxl.services.controller;

import be.pxl.services.domain.dto.LabelRequest;
import be.pxl.services.domain.dto.LabelResponse;
import be.pxl.services.domain.dto.ProductResponse;
import be.pxl.services.services.ILabelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productcatalogus/labels")
@RequiredArgsConstructor
public class LabelController {

    private final ILabelService labelService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<LabelResponse> getAllLabels() {
        return labelService.getAllLabels();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LabelResponse getLabelByUd(@PathVariable Long id) {
        return labelService.getLabelById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponse createLabel(@RequestBody @Valid LabelRequest labelRequest) {
        return labelService.createLabel(labelRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public LabelResponse updateLabel(@PathVariable Long id, @RequestBody @Valid LabelRequest labelRequest) {
        return labelService.updateLabel(id, labelRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLabel(@PathVariable Long id) {
        labelService.deleteLabel(id);
    }
}

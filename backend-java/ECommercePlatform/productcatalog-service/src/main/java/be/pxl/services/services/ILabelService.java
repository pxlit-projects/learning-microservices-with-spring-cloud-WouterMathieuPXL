package be.pxl.services.services;

import be.pxl.services.domain.Label;
import be.pxl.services.domain.dto.LabelRequest;
import be.pxl.services.domain.dto.LabelResponse;

import java.util.List;

public interface ILabelService {

    List<LabelResponse> getAllLabels();
    LabelResponse getLabelById(Long id);

    LabelResponse createLabel(LabelRequest label);

    LabelResponse updateLabel(Long id, LabelRequest label);

    void deleteLabel(Long labelId);
}

package be.pxl.services.services.implementation;

import be.pxl.services.domain.Label;
import be.pxl.services.domain.dto.LabelRequest;
import be.pxl.services.domain.dto.LabelResponse;
import be.pxl.services.exceptions.ResourceNotFoundException;
import be.pxl.services.repository.LabelRepository;
import be.pxl.services.repository.ProductRepository;
import be.pxl.services.services.ILabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabelService implements ILabelService {

    private final LabelRepository labelRepository;
    private final ProductRepository productRepository;

    @Override
    public List<LabelResponse> getAllLabels() {
        return labelRepository.findAll().stream().map(this::mapToLabelResponse).toList();
    }
    @Override
    public LabelResponse getLabelById(Long id) {
        return labelRepository.findById(id).map(this::mapToLabelResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found."));
    }

    @Override
    public LabelResponse createLabel(LabelRequest labelRequest) {
        Label label = Label.builder()
                .name(labelRequest.getName())
                .color(labelRequest.getColor())
                .build();

        labelRepository.save(label);
        return mapToLabelResponse(label);
    }

    @Override
    public LabelResponse updateLabel(Long id, LabelRequest labelRequest) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found."));

        label.setName(labelRequest.getName());
        label.setColor(labelRequest.getColor());

        labelRepository.save(label);

        return mapToLabelResponse(label);
    }

    @Override
    @Transactional
    public void deleteLabel(Long labelId) {
        Label label = labelRepository.findById(labelId)
                .orElseThrow(() -> new RuntimeException("Label not found"));

        // Remove the label from all associated products
        productRepository.removeLabelAssociations(labelId);

        labelRepository.delete(label);
    }

    private LabelResponse mapToLabelResponse(Label label) {
        return LabelResponse.builder()
                .id(label.getId())
                .name(label.getName())
                .color(label.getColor())
                .build();
    }
}

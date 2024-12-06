package be.pxl.services.services.implementation;

import be.pxl.services.domain.Category;
import be.pxl.services.domain.Label;
import be.pxl.services.domain.LabelColor;
import be.pxl.services.domain.dto.LabelRequest;
import be.pxl.services.domain.dto.LabelResponse;
import be.pxl.services.exceptions.ResourceNotFoundException;
import be.pxl.services.repository.LabelRepository;
import be.pxl.services.repository.ProductRepository;
import be.pxl.services.services.ILabelService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import be.pxl.services.domain.LabelColor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabelService implements ILabelService {

    private static final Logger log = LoggerFactory.getLogger(LabelService.class);

    private final LabelRepository labelRepository;
    private final ProductRepository productRepository;

    @Override
    public List<LabelResponse> getAllLabels() {
        log.info("Fetching all labels");
        return labelRepository.findAll().stream().map(this::mapToLabelResponse).toList();
    }

//    @Override
//    public LabelResponse getLabelById(Long labelId) {
//        log.info("Fetching label with ID: {}", labelId);
//        return mapToLabelResponse(findLabelById(labelId));
//    }

    @Override
    public LabelResponse createLabel(LabelRequest labelRequest) {
        log.info("Creating a {} label with name: '{}'", labelRequest.getColor(), labelRequest.getName());
        Label label = Label.builder()
                .name(labelRequest.getName())
                .color(labelRequest.getColor())
                .build();

        labelRepository.save(label);
        log.info("Created a {} label with name: '{}'", labelRequest.getColor(), labelRequest.getName());

        return mapToLabelResponse(label);
    }

    @Override
    public LabelResponse updateLabel(Long labelId, LabelRequest labelRequest) {
        log.info("Updating label with ID: {}", labelId);

        Label label = findLabelById(labelId);
        label.setName(labelRequest.getName());
        label.setColor(labelRequest.getColor());

        labelRepository.save(label);
        log.info("Updated label with ID: {}", labelId);

        return mapToLabelResponse(label);
    }

    @Override
    @Transactional
    public void deleteLabel(Long labelId) {
        log.info("Deleting label with ID: {}", labelId);
        Label label = findLabelById(labelId);
        System.out.println(label);
        // Remove the label from all associated products
        productRepository.removeLabelAssociations(labelId);
        log.info("Removed label with ID: {} from all associated products", labelId);

        labelRepository.delete(label);
        log.info("Deleted label with ID: {}", labelId);
    }

    @Override
    public List<String> getLabelColors() {
        log.info("Fetching label colors");
        return Arrays.stream(LabelColor.values())
                     .map(Enum::name)
                     .toList();
    }

    private Label findLabelById(Long labelId) {
        return labelRepository.findById(labelId)
                .orElseThrow(() -> {
                    log.error("Label with ID: {} not found", labelId);
                    return new ResourceNotFoundException("Label not found");
                });
    }

    private LabelResponse mapToLabelResponse(Label label) {
        return LabelResponse.builder()
                .id(label.getId())
                .name(label.getName())
                .color(label.getColor())
                .build();
    }
}

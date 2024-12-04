package be.pxl.services.domain.dto;

import be.pxl.services.domain.Category;
import be.pxl.services.domain.Label;
import lombok.*;

import java.util.List;

@Getter
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private double price;
    private Category category;
    private List<Label> labels;
}

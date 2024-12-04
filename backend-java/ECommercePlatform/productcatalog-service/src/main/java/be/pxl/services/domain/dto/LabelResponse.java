package be.pxl.services.domain.dto;

import be.pxl.services.domain.LabelColor;
import lombok.*;

@Getter
@Builder
public class LabelResponse {

    private Long id;
    private String name;
    private LabelColor color;
}

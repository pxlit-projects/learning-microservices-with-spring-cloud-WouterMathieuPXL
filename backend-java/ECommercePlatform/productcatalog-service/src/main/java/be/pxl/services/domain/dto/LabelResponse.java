package be.pxl.services.domain.dto;

import be.pxl.services.domain.LabelColor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabelResponse {

    private Long id;
    private String name;
    private LabelColor color;
}

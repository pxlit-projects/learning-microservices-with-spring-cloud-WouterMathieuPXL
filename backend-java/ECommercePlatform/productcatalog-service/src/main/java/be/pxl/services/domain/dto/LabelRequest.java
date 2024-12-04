package be.pxl.services.domain.dto;

import be.pxl.services.domain.LabelColor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabelRequest {

    @NotBlank
    private String name;
    @NotNull
    private LabelColor color;
}

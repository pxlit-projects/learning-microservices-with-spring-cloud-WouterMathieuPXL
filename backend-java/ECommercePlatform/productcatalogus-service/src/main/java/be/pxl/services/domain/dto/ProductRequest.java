package be.pxl.services.domain.dto;

import be.pxl.services.domain.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    @Positive
    private double price;
    @NotNull
    private Category category;
}

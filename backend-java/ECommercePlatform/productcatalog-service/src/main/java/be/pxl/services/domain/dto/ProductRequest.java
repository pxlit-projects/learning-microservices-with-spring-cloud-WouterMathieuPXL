package be.pxl.services.domain.dto;

import be.pxl.services.domain.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
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
    @NotNull
    private List<Long> labelIds;

    private MultipartFile image;
}

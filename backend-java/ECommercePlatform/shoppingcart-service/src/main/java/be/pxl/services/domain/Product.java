package be.pxl.services.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
public class Product {

    private Long id;
    private String name;
    private double price;
}

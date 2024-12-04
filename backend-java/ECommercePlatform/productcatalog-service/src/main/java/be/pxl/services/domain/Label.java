package be.pxl.services.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "label")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Enumerated(value = EnumType.STRING)
    private LabelColor color;
}

package be.pxl.services.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String description;
    private double price;

    // TODO: private Brand brand
    // TODO: private List<String> images;

    @Enumerated(value = EnumType.STRING)
    private Category category;

    @ManyToMany(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "product_label",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private Set<Label> labels = new HashSet<>();

//    public void addLabel(Label label) {
//        labels.add(label);
//    }
//
//    public void removeLabel(Label label) {
//        labels.remove(label);
//    }
}

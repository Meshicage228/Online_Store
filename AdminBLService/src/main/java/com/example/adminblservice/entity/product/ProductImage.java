package com.example.adminblservice.entity.product;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "images")
public class ProductImage {
    @Id
    @SequenceGenerator(name = "img_seq",
            sequenceName = "image_sequence",
            initialValue = 38, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "img_seq")
    @Column(name = "image_id")
    private Integer id;

    @Lob
    private byte[] image;

    @ManyToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    private ProductEntity product;
}

package com.example.orderblservice.entity.product;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer id;

    @Lob
    private byte[] image;

    @ManyToOne
    @ToString.Exclude
    private ProductEntity product;
}

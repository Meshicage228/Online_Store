package com.example.adminblservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Base64;

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

package com.example.coursework.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private Float price;
    private String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "product")
    private List<ProductImage> images;

    public void addImage(byte[] image){
        if(images == null){
            images = new ArrayList<>();
        }
        ProductImage newImage = ProductImage.builder()
                .product(this)
                .image(image)
                .build();

        images.add(newImage);

        System.out.println("after adding");
    }
}

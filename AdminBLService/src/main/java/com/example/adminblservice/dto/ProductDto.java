package com.example.adminblservice.dto;

import com.example.adminblservice.entity.ProductImage;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductDto {
    Integer id;
    String title;
    String description;
    Float price;
    List<ProductImage> images;
}

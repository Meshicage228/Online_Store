package com.example.coursework.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductDto {
    private MultipartFile fileImage;
    private Integer id;
    private String title;
    private String description;
    private Float price;
    private List<ProductImageDto> images;
}

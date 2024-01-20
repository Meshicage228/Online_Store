package com.example.adminblservice.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductDto implements Serializable {
    private List<MultipartFile> fileImage;
    private Integer id;
    private String title;
    private String description;
    private Float price;
    private List<ProductImageDto> images;
}

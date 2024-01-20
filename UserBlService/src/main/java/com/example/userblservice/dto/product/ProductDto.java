package com.example.userblservice.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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

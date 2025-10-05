package com.example.adminblservice.dto.product;

import com.example.adminblservice.dto.user.CommentaryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductDto implements Serializable {
    private List<byte[]> imagesToThrow;
    private Integer id;
    private String title;
    private String description;
    private Float price;
    private Integer count;
    private List<CommentaryDto> comments;
    private List<ProductImageDto> images;
}

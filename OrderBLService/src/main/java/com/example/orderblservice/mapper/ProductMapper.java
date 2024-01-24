package com.example.orderblservice.mapper;


import com.example.orderblservice.dto.product.ProductDto;
import com.example.orderblservice.entity.product.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;


@Mapper(
        componentModel = "spring",
        uses = ProductImageMapper.class
)
public interface ProductMapper {
    @Mappings({
        @Mapping(target = "id", source = "id"),
        @Mapping(target = "title", source = "title"),
        @Mapping(target = "description", source = "description"),
        @Mapping(target = "price", source = "price"),
        @Mapping(target = "count", source = "count"),
        @Mapping(target = "images", source = "images")
    })
    ProductDto toDto(ProductEntity entity);

    List<ProductDto> toDtos (List<ProductEntity> entities);
}

package com.example.adminblservice.mappers;


import com.example.adminblservice.dto.ProductDto;
import com.example.adminblservice.entity.product.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import java.util.List;


@Mapper(
        componentModel = "spring",
        uses = ProductImageMapper.class
)
public interface ProductMapper {
    @Mappings({
        @Mapping(target = "title", source = "title"),
        @Mapping(target = "description", source = "description"),
        @Mapping(target = "price", source = "price")
    })
    ProductEntity toEntity(ProductDto dto);

    // TODO: 20.01.2024 изменить игнор когда будет ui
    @Mappings({
        @Mapping(target = "id", source = "id"),
        @Mapping(target = "title", source = "title"),
        @Mapping(target = "description", source = "description"),
        @Mapping(target = "price", source = "price"),
        @Mapping(target = "images", source = "images")
    })
    ProductDto toDto(ProductEntity entity);

    List<ProductDto> toDtos (List<ProductEntity> entities);
    List<ProductEntity> toEntities (List<ProductDto> dtos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "creationTime", ignore = true)
    ProductEntity update(@MappingTarget ProductEntity target, ProductEntity source);
}

package com.example.adminblservice.mappers.product;

import com.example.adminblservice.dto.product.ProductDto;
import com.example.adminblservice.entity.product.ProductEntity;
import com.example.adminblservice.mappers.user.CommentaryMapper;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {ProductImageMapper.class, CommentaryMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProductMapper {
    @Mappings({
        @Mapping(target = "title", source = "title"),
        @Mapping(target = "description", source = "description"),
        @Mapping(target = "price", source = "price")
    })
    ProductEntity toEntity(ProductDto dto);

    @Mappings({
        @Mapping(target = "id", source = "id"),
        @Mapping(target = "title", source = "title"),
        @Mapping(target = "description", source = "description"),
        @Mapping(target = "price", source = "price"),
        @Mapping(target = "images", source = "images"),
        @Mapping(target = "comments", source = "comments"),
        @Mapping(target = "count", source = "count")
    })
    ProductDto toDto(ProductEntity entity);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "count", source = "count")
    ProductEntity update(@MappingTarget ProductEntity target, ProductEntity source);
}

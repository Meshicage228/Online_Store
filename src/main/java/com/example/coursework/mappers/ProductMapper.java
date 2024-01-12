package com.example.coursework.mappers;

import com.example.coursework.dto.ProductDto;
import com.example.coursework.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;

@Mapper(
        componentModel = "spring"
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
        @Mapping(target = "images", source = "images")
    })
    ProductDto toDto(ProductEntity entity);

    List<ProductDto> toDtos (List<ProductEntity> entities);
    List<ProductEntity> toEntities (List<ProductDto> dtos);

    @Mapping(target = "id", ignore = true)
    void update(@MappingTarget ProductEntity target, ProductEntity source);

/*    default String EncodeImageToString(ProductEntity entity){
        return Base64.getEncoder().encodeToString(entity.getImages());
    }*/
}

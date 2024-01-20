package com.example.userblservice.mapper.product;


import com.example.userblservice.dto.product.ProductImageDto;
import com.example.userblservice.entity.product.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Base64;
import java.util.List;

@Mapper(
        componentModel = "spring"
)
public interface ProductImageMapper {
    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "image", expression = "java(convertStringToBytes(dto.getConvertedImage()))")
    })
    ProductImage toEntity(ProductImageDto dto);

    @Mappings({
        @Mapping(target = "id", source = "id"),
        @Mapping(target = "convertedImage", expression = "java(convertBytesToString(entity.getImage()))"),
    })
    ProductImageDto toDto(ProductImage entity);

    List<ProductImageDto> toDtos (List<ProductImage> entities);
    List<ProductImage> toEntities (List<ProductImageDto> dtos);

    default String convertBytesToString(byte[] arr){
        return Base64.getEncoder().encodeToString(arr);
    }
    default byte[] convertStringToBytes(String encoded){
        return Base64.getDecoder().decode(encoded);
    }
}

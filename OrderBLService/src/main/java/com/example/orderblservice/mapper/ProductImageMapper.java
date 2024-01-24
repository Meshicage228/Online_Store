package com.example.orderblservice.mapper;

import com.example.orderblservice.dto.product.ProductImageDto;
import com.example.orderblservice.entity.product.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Base64;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

    @Mappings({
        @Mapping(target = "id", source = "id"),
        @Mapping(target = "convertedImage", expression = "java(convertBytesToString(entity.getImage()))"),
    })
    ProductImageDto toDto(ProductImage entity);

    List<ProductImageDto> toDtos (List<ProductImage> entities);

    default String convertBytesToString(byte[] arr){
       return Base64.getEncoder().encodeToString(arr);
    }
}

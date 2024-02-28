package com.example.userblservice.mapper.product;


import com.example.userblservice.dto.product.ProductDto;
import com.example.userblservice.entity.product.ProductEntity;
import com.example.userblservice.entity.product.ProductImage;
import com.example.userblservice.entity.user.UsersCart;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;


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

    @Mappings({
        @Mapping(target = "id", source = "id"),
        @Mapping(target = "title", source = "title"),
        @Mapping(target = "description", source = "description"),
        @Mapping(target = "price", source = "price"),
        @Mapping(target = "count", source = "count"),
        @Mapping(target = "images", source = "images")
    })
    ProductDto toDto(ProductEntity entity);

    @Mapping(target = "id", expression = "java(cart.getProduct().getId())")
    @Mapping(target = "cart_id", source = "id")
    @Mapping(target = "bill", expression = "java(countBill(cart))")
    @Mapping(target = "title", expression = "java(cart.getProduct().getTitle())")
    @Mapping(target = "description", expression = "java(cart.getProduct().getDescription())")
    @Mapping(target = "price", expression = "java(cart.getProduct().getPrice())")
    @Mapping(target = "images", source = "product")
    @Mapping(target = "count", source = "countToBuy")
    ProductDto fromCart(UsersCart cart);

    List<ProductDto> toDtoFromCart (Set<UsersCart> carts);

    default List<ProductImage> mapProductImage(ProductEntity product) {
        return product.getImages();
    }

    default Float countBill(UsersCart cart){
        return cart.getProduct().getPrice() * cart.getCountToBuy();
    }
}

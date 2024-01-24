package com.example.orderblservice.mapper;

import com.example.orderblservice.dto.product.OrderDto;
import com.example.orderblservice.dto.product.ProductDto;
import com.example.orderblservice.entity.product.Orders;
import com.example.orderblservice.entity.product.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(
        componentModel = "spring",
        uses = {ProductMapper.class, UserMapper.class}
)
public interface OrderMapper {
    @Mappings({
         @Mapping(target = "id", source = "id"),
         @Mapping(target = "product", source = "product"),
         @Mapping(target = "user", source = "user"),
         @Mapping(target = "countOfProduct", source = "countOfProduct"),
         @Mapping(target = "priceAtMomentBuying", source = "priceAtMomentBuying"),
         @Mapping(target = "status", source = "status"),
         @Mapping(target = "dateOfPurchase", source = "dateOfPurchase")
    })
    OrderDto toDto(Orders entity);
}

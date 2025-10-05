package com.example.orderblservice.dto.product;

import com.example.orderblservice.domain.OrderStatus;
import com.example.orderblservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderDto {
    private Integer id;
    private ProductDto product;
    private UserDto user;
    private Integer countOfProduct;
    private Float priceAtMomentBuying;
    private OrderStatus status;
    private Date dateOfPurchase;
    private Float bill;
}

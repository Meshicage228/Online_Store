package com.example.adminblservice.dto.product;

import com.example.adminblservice.domain.OrderStatus;
import com.example.adminblservice.dto.user.UserDto;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PurchaseDto {
    private ProductDto product;

    private UserDto user;

    private Integer countOfProduct;

    private Float priceAtMomentBuying;

    private OrderStatus status;

    private Date dateOfPurchase;
}

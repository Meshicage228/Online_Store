package com.example.orderblservice.service;


import com.example.orderblservice.dto.product.OrderDto;
import com.example.orderblservice.dto.product.OrderSearchDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface OrderService {
    boolean acceptPurchase(UUID id);

    Page<OrderDto> findAllWithSort(Integer page, Integer size, OrderSearchDto sortedBy, String sort);

    boolean haveBoughtProd(UUID userId, Integer prodId);
}

package com.example.orderblservice.service;


import com.example.orderblservice.dto.product.OrderDto;
import com.example.orderblservice.dto.product.OrderRequest;
import com.example.orderblservice.dto.product.OrderSearchDto;
import org.springframework.data.domain.Page;

public interface OrderService {
    void acceptPurchase(OrderRequest dto);

    Page<OrderDto> findAllWithSort(Integer page, Integer size, OrderSearchDto sortedBy, String sort);
}

package com.example.orderblservice.service;


import com.example.orderblservice.dto.product.OrderDto;
import com.example.orderblservice.dto.product.OrderRequest;

public interface OrderService {
    OrderDto createPurchase(OrderRequest dto);
}

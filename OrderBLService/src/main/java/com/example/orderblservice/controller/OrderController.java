package com.example.orderblservice.controller;

import com.example.orderblservice.dto.product.OrderDto;
import com.example.orderblservice.dto.product.OrderRequest;
import com.example.orderblservice.service.impl.OrderServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor

@RestController
@RequestMapping("/v1/orders")
public class OrderController {
    OrderServiceImpl service;

    @PostMapping("/create")
    public OrderDto createPurchase(@RequestBody OrderRequest request){
        OrderDto saved  =  service.createPurchase(request);
        return saved;
    }
}

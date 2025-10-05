package com.example.orderblservice.controller;

import com.example.orderblservice.domain.OrderStatus;
import com.example.orderblservice.dto.product.OrderDto;
import com.example.orderblservice.dto.product.OrderSearchDto;
import com.example.orderblservice.service.impl.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orders")
public class OrderController {
    private final OrderServiceImpl service;

    @GetMapping("/{page}/{size}")
    public Page<OrderDto> getOrders(@PathVariable("page") final Integer page,
                                    @PathVariable("size") final Integer size,
                                    @RequestParam(name = "status", required = false) final OrderStatus status,
                                    @RequestParam(name = "name", required = false) final String name,
                                    @RequestParam(name = "title", required = false) final String title,
                                    @RequestParam(name = "user_id", required = false) final UUID user_id,
                                    @RequestParam(name = "sortedBy", required = false, defaultValue = "default") final String sortedBy) {
        final OrderSearchDto searchDto = OrderSearchDto.builder()
                .status(status)
                .name(name)
                .user_id(user_id)
                .title(title)
                .build();
        return service.findAllWithSort(page, size, searchDto, sortedBy);
    }

    @PostMapping("/create/{user_id}")
    public boolean createPurchase(@PathVariable("user_id") final UUID id) {
        return service.acceptPurchase(id);
    }

    @PostMapping("/{user_id}/{prod_id}")
    boolean haveBoughtProduct(@PathVariable("user_id") final UUID user_id,
                              @PathVariable("prod_id") final Integer prod_id) {
        return service.haveBoughtProd(user_id, prod_id);
    }
}

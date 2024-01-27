package com.example.orderblservice.controller;

import com.example.orderblservice.domain.OrderStatus;
import com.example.orderblservice.dto.product.OrderDto;
import com.example.orderblservice.dto.product.OrderSearchDto;
import com.example.orderblservice.service.impl.OrderServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor

@RestController
@RequestMapping("/v1/orders")
public class OrderController {
    OrderServiceImpl service;

    // TODO: 25.01.2024 Придумать уменьшение параметров
    @GetMapping("/{page}/{size}")
    public Page<OrderDto> getOrders(@PathVariable("page") Integer page,
                                    @PathVariable("size") Integer size,
                                    @RequestParam(name = "status", required = false) OrderStatus status,
                                    @RequestParam(name = "name", required = false) String name,
                                    @RequestParam(name = "title", required = false) String title,
                                    @RequestParam(name = "user_id", required = false) UUID user_id,
                                    @RequestParam(name = "sortedBy", required = false, defaultValue = "default") String sortedBy) {
        OrderSearchDto searchDto = OrderSearchDto.builder()
                .status(status)
                .name(name)
                .user_id(user_id)
                .title(title)
                .build();
        return service.findAllWithSort(page, size, searchDto, sortedBy);
    }

    @PostMapping("/create/{user_id}")
    public void createPurchase(@PathVariable("user_id") UUID id) {
        service.acceptPurchase(id);
    }
}

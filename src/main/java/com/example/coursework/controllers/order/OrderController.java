package com.example.coursework.controllers.order;


import com.example.coursework.clients.OrderClient;
import com.example.coursework.domain.OrderStatus;
import com.example.coursework.dto.product.OrderDto;
import com.example.coursework.utils.OrderSearchDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Controller
@RequestMapping("/store/orders")
public class OrderController {
    OrderClient client;

    @GetMapping("/{page}/{size}")
    public Page<OrderDto> getOrders(@PathVariable("page") Integer page,
                                    @PathVariable("size") Integer size,
                                    @RequestParam(name = "status", required = false) OrderStatus status,
                                    @RequestParam(name = "name", required = false) String name,
                                    @RequestParam(name = "title", required = false) String title,
                                    @RequestParam(name = "user_id", required = false) UUID user_id,
                                    @RequestParam(name = "sortedBy", required = false, defaultValue = "default") String sortedBy) {

        return client.getOrders(page, size, status, name, title, user_id, sortedBy);
    }

    @PostMapping("/create/{user_id}")
    public void createPurchase(@PathVariable("user_id") UUID id) {
        client.createPurchase(id);
    }
}

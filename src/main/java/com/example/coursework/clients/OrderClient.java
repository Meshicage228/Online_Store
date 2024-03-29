package com.example.coursework.clients;

import com.example.coursework.domain.OrderStatus;
import com.example.coursework.dto.product.OrderDto;
import com.example.coursework.exceptions.handler.ProjectExceptionHandler;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "${app.clients.orders.name}",
        url = "${app.clients.orders.url}",
        path = "${app.clients.orders.path}",
        configuration = ProjectExceptionHandler.class)
public interface OrderClient {
    @GetMapping("/{page}/{size}")
    Page<OrderDto> getOrders(@PathVariable("page") Integer page,
                             @PathVariable("size") Integer size,
                             @RequestParam(name = "status", required = false) OrderStatus status,
                             @RequestParam(name = "name", required = false) String name,
                             @RequestParam(name = "title", required = false) String title,
                             @RequestParam(name = "user_id", required = false) UUID user_id,
                             @RequestParam(name = "sortedBy", required = false, defaultValue = "default") String sortedBy);

    @PostMapping("/create/{user_id}")
    boolean createPurchase(@PathVariable("user_id") UUID id);

    @PostMapping("/{user_id}/{prod_id}")
    boolean haveBoughtProduct(@PathVariable("user_id") UUID user_id,
                              @PathVariable("prod_id") Integer prod_id);
}

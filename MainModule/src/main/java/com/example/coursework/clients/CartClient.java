package com.example.coursework.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "${app.clients.users.cart-name}",
        url = "${app.clients.users.url}",
        path = "${app.clients.users.cart-path}")
public interface CartClient {
    @PatchMapping("/{cart_id}/changeCount")
    void changeCount(@PathVariable("cart_id") Integer cart_id,
                     @RequestParam(value = "option", required = false) String option);

    @DeleteMapping("/{cart_id}")
    void deleteFromCart(@PathVariable Integer cart_id);

    @PostMapping("/{user_id}/{prod_id}")
    void addToCart(@PathVariable("user_id") UUID user_id,
                   @PathVariable("prod_id") Integer prod_id);
}

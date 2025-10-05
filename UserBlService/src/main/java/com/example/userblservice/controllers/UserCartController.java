package com.example.userblservice.controllers;

import com.example.userblservice.service.impl.CartServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users/cart")
public class UserCartController {
    private final CartServiceImpl cartService;

    @PatchMapping("/{cart_id}/changeCount")
    public void changeCount(@PathVariable("cart_id") final Integer cart_id,
                            @RequestParam(value = "option", required = false) final String option){
        cartService.changeCount(cart_id, option);
    }
    @DeleteMapping("/{cart_id}")
    public void deleteFromCart(@PathVariable final Integer cart_id){
        cartService.deleteFromCart(cart_id);
    }
    @PostMapping("/{user_id}/{prod_id}")
    public void addToCart(@PathVariable("user_id") final UUID user_id,
                          @PathVariable("prod_id") final Integer prod_id) {
        cartService.addToCart(user_id, prod_id);
    }
}

package com.example.userblservice.controllers;

import com.example.userblservice.service.impl.CartServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@RestController
@RequestMapping("/v1/users/cart")
public class UserCartController {
    CartServiceImpl cartService;

    @PatchMapping("/{cart_id}/changeCount")
    public void changeCount(@PathVariable("cart_id") Integer cart_id,
                            @RequestParam(value = "option", required = false) String option){
        cartService.changeCount(cart_id, option);
    }

    @DeleteMapping("/{cart_id}")
    public void deleteFromCart(@PathVariable Integer cart_id){
        cartService.deleteFromCart(cart_id);
    }

    @PostMapping("/{user_id}/{prod_id}")
    public void addToCart(@PathVariable("user_id") UUID user_id,
                          @PathVariable("prod_id") Integer prod_id) {
        cartService.addToCart(user_id, prod_id);
    }
}

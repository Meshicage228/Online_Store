package com.example.coursework.controllers.users;

import com.example.coursework.clients.UsersCartClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Controller
@RequestMapping("*/users/cart")
public class UsersCart {
    UsersCartClient client;
    @PatchMapping("/{cart_id}/changeCount")
    public void changeCount(@PathVariable("cart_id") Integer cart_id,
                            @RequestParam(value = "option", required = false) String option){
        client.changeCount(cart_id, option);
    }
    @DeleteMapping("/{cart_id}")
    public void deleteFromCart(@PathVariable Integer cart_id){
        client.deleteFromCart(cart_id);
    }
    @PostMapping("/{user_id}/{prod_id}")
    public void addToCart(@PathVariable("user_id") UUID user_id,
                          @PathVariable("prod_id") Integer prod_id) {
        client.addToCart(user_id, prod_id);
    }
}

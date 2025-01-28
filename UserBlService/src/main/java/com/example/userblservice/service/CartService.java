package com.example.userblservice.service;

import java.util.UUID;

public interface CartService {
    void changeCount(Integer id, String option);
    void deleteFromCart(Integer cartId);
    void addToCart(UUID user_id, Integer id);
}

package com.example.userblservice.service.impl;

import com.example.userblservice.entity.product.ProductEntity;
import com.example.userblservice.entity.user.UserEntity;
import com.example.userblservice.entity.user.UsersCart;
import com.example.userblservice.exceptions.ProductNotFoundException;
import com.example.userblservice.exceptions.UserNotFoundException;
import com.example.userblservice.repository.product.ProductRepository;
import com.example.userblservice.repository.user.CartRepository;
import com.example.userblservice.repository.user.UserRepository;
import com.example.userblservice.service.CartService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.UUID;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Service
@Transactional
public class CartServiceImpl implements CartService {
    CartRepository cartRepository;
    UserRepository userRepository;
    ProductRepository productRepository;
    @Override
    public void changeCount(Integer id, String option) {
        UsersCart usersCart = cartRepository.findById(id).get();
        Integer currentCount = usersCart.getCountToBuy();
        switch (option){
            case "increment" -> usersCart.setCountToBuy(++currentCount);
            case "decrement" -> {
                if(currentCount - 1 > 0){
                    usersCart.setCountToBuy(--currentCount);
                }
            }
        }
    }
    @Override
    public void addToCart(UUID userId, Integer prodId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        ProductEntity productEntity = productRepository.findById(prodId)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден"));
        // TODO: 12.02.2024 check that exists
        cartRepository.save(
                UsersCart.builder()
                        .countToBuy(1)
                        .product(productEntity)
                        .user(userEntity)
                        .build());
    }
    @Override
    public void deleteFromCart(Integer cartId) {
        cartRepository.deleteById(cartId);
    }
}
